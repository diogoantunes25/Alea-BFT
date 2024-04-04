package pt.tecnico.ulisboa.hbbft.agreement.vba.abba;

import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.agreement.vba.IValidatedBinaryAgreement;
import pt.tecnico.ulisboa.hbbft.agreement.vba.ValidatedBinaryAgreementMessage;
import pt.tecnico.ulisboa.hbbft.agreement.vba.ValidatedBoolean;
import pt.tecnico.ulisboa.hbbft.agreement.vba.Validator;
import pt.tecnico.ulisboa.hbbft.agreement.vba.abba.messages.CoinMessage;
import pt.tecnico.ulisboa.hbbft.agreement.vba.abba.messages.MainVoteMessage;
import pt.tecnico.ulisboa.hbbft.agreement.vba.abba.messages.PreProcessMessage;
import pt.tecnico.ulisboa.hbbft.agreement.vba.abba.messages.PreVoteMessage;
import pt.tecnico.ulisboa.hbbft.utils.ThreshsigUtil;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.SigShare;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.ThreshSigUtils;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.exceptions.NotEnoughSharesException;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class ValidatedBinaryAgreement implements IValidatedBinaryAgreement {

    private final String pid;

    private final Integer replicaId;

    private final NetworkInfo networkInfo;

    private final Validator validator;

    private final ThreshSigUtils sigUtils;

    private final VbaMessageFactory messageFactory;

    private final Map<Integer, PreProcessMessage> preProcessMessages = new HashMap<>();

    private Long round = 0L;

    private Map<Long, Round> rounds = new HashMap<>();

    // A cache for messages for future rounds that cannot be handled yet.
    private Map<Long, Map<Integer, ReceivedMessages>> incomingQueue = new TreeMap<>();

    private ValidatedBoolean output;

    public ValidatedBinaryAgreement(String pid, Integer replicaId, NetworkInfo networkInfo, Validator validator) {
        this.pid = pid;
        this.replicaId = replicaId;
        this.networkInfo = networkInfo;
        this.validator = validator;

        this.sigUtils = networkInfo.getSigUtils();
        this.messageFactory = new VbaMessageFactory(pid, replicaId, 0L);
    }

    @Override
    public String getPid() {
        return pid;
    }

    private Round getRound(Long roundId) {
        return this.rounds.computeIfAbsent(roundId, Round::new);
    }

    /*@Override
    public Step<ValidatedBoolean> handleInput(ValidatedBoolean input) {
        if (!this.validator.validate(input)) return new Step<>();

        byte[] toSign = String.format("%s-%d-%b", pid, PreProcessMessage.PRE_PROCESS, input).getBytes();
        SigShare share = ThreshsigUtils.sigShare(toSign, networkInfo.getKeyShare());
        PreProcessMessage preProcessMessage = messageFactory.createPreProcessMessage(input.getValue(), input.getProof(), share);

        return this.send(preProcessMessage);
    }*/

    @Override
    public Step<ValidatedBoolean> handleInput(ValidatedBoolean input) {
        if (!this.validator.validate(input)) return new Step<>();
        return new Step<>();
    }

    @Override
    public Step<ValidatedBoolean> handleMessage(ValidatedBinaryAgreementMessage message) {
        Step<ValidatedBoolean> step = new Step<>();

        if (!message.getPid().equals(pid)) return step;

        final int type = message.getType();
        switch (type) {
            case PreProcessMessage.PRE_PROCESS:
                return this.handlePreProcessMessage((PreProcessMessage) message);
            case PreVoteMessage.PRE_VOTE:
                return this.handlePreVoteMessage((PreVoteMessage) message);
            case MainVoteMessage.MAIN_VOTE:
                return this.handleMainVoteMessage((MainVoteMessage) message);
            case CoinMessage.COIN:
                return this.handleCoinMessage((CoinMessage) message);
            default:
                return step;
        }

        // Message is for a later round. We can't handle that yet.
        /*if (message.getRound() > this.round) {
            Map<Integer, ReceivedMessages> epochState = incomingQueue.computeIfAbsent(round, r -> new TreeMap<>());
            ReceivedMessages received = epochState.computeIfAbsent(message.getSender(), s -> new ReceivedMessages());
            received.insert(message);
        }

        return step;
        */
    }

    @Override
    public boolean hasTerminated() {
        return output != null;
    }

    @Override
    public Optional<ValidatedBoolean> deliver() {
        return Optional.ofNullable(output);
    }

    /**
     * Called by the protocol to indicate that
     * a {@link PreProcessMessage} has been received.
     *
     * @param preProcessMessage the received message
     */
    private Step<ValidatedBoolean> handlePreProcessMessage(PreProcessMessage preProcessMessage) {
        Step<ValidatedBoolean> step = new Step<>();

        ValidatedBoolean value = new ValidatedBoolean(preProcessMessage.getValue(), preProcessMessage.getJustification());
        if (!this.validator.validate(value)) return step;

        final int senderId = preProcessMessage.getSender();
        preProcessMessages.putIfAbsent(senderId, preProcessMessage);

        final int quorum = networkInfo.getN() - networkInfo.getF();
        if (preProcessMessages.size() == quorum && this.round == 0L) {
            ValidatedBoolean proposal = new ValidatedBoolean(true, new byte[0]);
            // TODO return this.propose(proposal);
        }
        return step;
    }

    private Step<ValidatedBoolean> handlePreVoteMessage(PreVoteMessage message) {
        final long roundNumber = message.getRound();
        Round round = this.getRound(roundNumber);
        Map<Integer, PreVoteMessage> preVoteMessages = round.getPreVoteMessages();

        final int senderId = message.getSender();
        if (preVoteMessages.containsKey(senderId)) return new Step<>();

        byte[] toVerify = String.format("%s-%d-%d-%b", message.getPid(), PreVoteMessage.PRE_VOTE, message.getRound(), message.getValue()).getBytes();
        if (!sigUtils.verifyShare(toVerify, message.getShare(), message.getSender())) return new Step<>();

        preVoteMessages.putIfAbsent(senderId, message);

        final int quorum = networkInfo.getN() - networkInfo.getF();
        if (preVoteMessages.size() == quorum) {
            Optional<PreVoteMessage> yesVote = preVoteMessages.values().stream().filter(m -> m.getValue()).findAny();
            Optional<PreVoteMessage> noVote = preVoteMessages.values().stream().filter(m -> !m.getValue()).findAny();
            MainVoteMessage mainVoteMessage;

            if (noVote.isEmpty()) {
                Set<SigShare> shares = preVoteMessages.values().stream().map(PreVoteMessage::getShare).collect(Collectors.toSet());
                byte[] justification = new byte[0];
                try {
                    justification = sigUtils.combine(toVerify, shares).toBytes();
                } catch (NotEnoughSharesException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

                byte[] toSign = String.format("%s-%d-%d-%b", pid, MainVoteMessage.MAIN_VOTE, roundNumber, true).getBytes();
                SigShare share = sigUtils.sign(toSign);

                mainVoteMessage = messageFactory.createMainVoteMessage(true, justification, share);
            }

            else if (yesVote.isEmpty()) {
                Set<SigShare> shares = preVoteMessages.values().stream().map(PreVoteMessage::getShare).collect(Collectors.toSet());
                byte[] justification = new byte[0];
                try {
                    justification = sigUtils.combine(toVerify, shares).toBytes();
                } catch (NotEnoughSharesException e) {
                    throw new RuntimeException(e);
                }

                byte[] toSign = String.format("%s-%d-%d-%b", pid, MainVoteMessage.MAIN_VOTE, roundNumber, false).getBytes();
                SigShare share = sigUtils.sign(toSign);

                mainVoteMessage = messageFactory.createMainVoteMessage(false, justification, share);
            }

            else {
                byte[] toSign = String.format("%s-%d-%d", pid, MainVoteMessage.MAIN_VOTE, roundNumber).getBytes();
                SigShare share = sigUtils.sign(toSign);

                mainVoteMessage = messageFactory.createMainVoteMessage(yesVote.get().getShare(), noVote.get().getShare(), share);
            }

            return this.send(mainVoteMessage);
        }

        return new Step<>();
    }

    private Step<ValidatedBoolean> handleMainVoteMessage(MainVoteMessage message) {
        final long roundNumber = message.getRound();
        Round round = this.getRound(roundNumber);
        Map<Integer, MainVoteMessage> mainVoteMessages = round.getMainVoteMessages();

        final int senderId = message.getSender();
        if (mainVoteMessages.containsKey(senderId)) return new Step<>();

        byte[] toVerify = (message.getValue() != null) ?
                String.format("%s-%d-%d-%b", message.getPid(), PreVoteMessage.PRE_VOTE, message.getRound(), message.getValue()).getBytes()
                : String.format("%s-%d-%d", message.getPid(), PreVoteMessage.PRE_VOTE, message.getRound()).getBytes();
        if (!sigUtils.verifyShare(toVerify, message.getShare(), message.getSender())) return new Step<>();

        mainVoteMessages.putIfAbsent(senderId, message);

        final int quorum = networkInfo.getN() - networkInfo.getF();
        if (mainVoteMessages.size() == quorum) {
            String coinName = String.format("%s-%d", pid, roundNumber);
            SigShare coinShare = sigUtils.sign(coinName.getBytes());
            CoinMessage coinMessage = messageFactory.createCoinMessage(coinShare);
            return this.send(coinMessage);
        }

        return new Step<>();
    }

    private Step<ValidatedBoolean> handleCoinMessage(CoinMessage message) {
        final long roundNumber = message.getRound();
        Round round = this.getRound(roundNumber);
        Map<Integer, CoinMessage> coinMessages = round.getCoinMessages();

        final int senderId = message.getSender();
        if (coinMessages.containsKey(senderId)) return new Step<>();

        coinMessages.putIfAbsent(senderId, message);

        final int quorum = networkInfo.getN() - networkInfo.getF();
        if (coinMessages.size() == quorum) {
            String coinName = String.format("%s-%d", pid, roundNumber);
            Set<SigShare> shares = coinMessages.values().stream().map(CoinMessage::getShare).collect(Collectors.toSet());
            try {
                boolean coin = new BigInteger(sigUtils.combine(coinName.getBytes(), shares).toBytes()).intValue() % 2 == 1;
            } catch (NotEnoughSharesException e) {
                throw new RuntimeException(e);
            }
            // F(PID, r) = coin

            // TODO PreVoteMessage preVoteMessage = messageFactory.createPreVoteMessage(coin);
        }

        return new Step<>();
    }

    private Step<ValidatedBoolean> tryProgress() {
        Step<ValidatedBoolean> step = new Step<>();

        Round round = this.getRound(this.round);
        Map<Integer, PreVoteMessage> preVoteMessages = round.getPreVoteMessages();
        if (!preVoteMessages.containsKey(replicaId)) {

        }

        // Check for decision
        Map<Integer, MainVoteMessage> mainVoteMessages = round.getMainVoteMessages();
        final int quorum = networkInfo.getN() - networkInfo.getF();
        if (mainVoteMessages.size() >= quorum) {
            Collection<MainVoteMessage> yesMessages = mainVoteMessages.values().stream().filter(m -> m.getValue() != null && m.getValue()).collect(Collectors.toList());
            Collection<MainVoteMessage> noMessages = mainVoteMessages.values().stream().filter(m -> m.getValue() != null && !m.getValue()).collect(Collectors.toList());
            // all main votes for 1
            if (yesMessages.size() >= quorum) {
                byte[] value = String.format("%s-%d-%d-%b", pid, MainVoteMessage.MAIN_VOTE, round.getNumber(), true).getBytes();
                Set<SigShare> shares = yesMessages.stream().map(MainVoteMessage::getShare).collect(Collectors.toSet());
                try {
                    byte[] justification = sigUtils.combine(value, shares).toBytes();
                } catch (NotEnoughSharesException e) {
                    throw new RuntimeException(e);
                }

                this.round += 1;
                Round newRound = this.getRound(this.round);
                newRound.setLastRound(true);

                // TODO this.output = new ValidatedBoolean()
            }

            // all main votes for 0
            else if (noMessages.size() >= quorum) {
                byte[] value = String.format("%s-%d-%d-%b", pid, MainVoteMessage.MAIN_VOTE, round.getNumber(), false).getBytes();
                Set<SigShare> shares = yesMessages.stream().map(MainVoteMessage::getShare).collect(Collectors.toSet());
                try {
                    byte[] justification = sigUtils.combine(value, shares).toBytes();
                } catch (NotEnoughSharesException e) {
                    throw new RuntimeException(e);
                }

                // TODO this.output = new ValidatedBoolean()
            }
        }

        return step;
    }

    private Step<ValidatedBoolean> send(ValidatedBinaryAgreementMessage message) {
        Step<ValidatedBoolean> step = new Step<>();
        step.add(this.handleMessage(message));
        step.add(message, this.networkInfo.getValidatorSet().getAllIds().stream()
                .filter(id -> !id.equals(this.replicaId)).collect(Collectors.toList()));
        return step;
    }
}
