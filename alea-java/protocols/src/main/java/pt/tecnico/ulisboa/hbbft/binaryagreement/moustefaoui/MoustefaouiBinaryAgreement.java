package pt.tecnico.ulisboa.hbbft.binaryagreement.moustefaoui;

import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementMessage;
import pt.tecnico.ulisboa.hbbft.binaryagreement.moustefaoui.messages.*;
import pt.ulisboa.tecnico.utils.Utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoustefaouiBinaryAgreement implements IMoustefaouiBinaryAgreement {

    private final String pid;

    // Our ID.
    private final Integer replicaId;

    // The network configuration.
    private final NetworkInfo networkInfo;

    // The Binary Agreement algorithm round.
    private volatile long round = 0L;

    private long start;

    private Map<Long, Long> roundStart = new ConcurrentHashMap<>();
    private Map<Long, Long> startAux = new ConcurrentHashMap<>();
    private Map<Long, Long> startConf = new ConcurrentHashMap<>();
    private Map<Long, Long> startCoin = new ConcurrentHashMap<>();

    // Maximum number of future epochs for which incoming messages are accepted.
    private long maxFutureRounds = 100L;

    // The set of values for which _2 f + 1_ `BVal`s have been received.
//    private volatile Map<Integer,BoolSet> binValues = BoolSet.NONE();
    private volatile Map<Long, BoolSet> binValues = new ConcurrentHashMap<>();

    // The nodes that sent us a `BVal(b)`, by `b`.
//    private volatile Map<Integer,BoolMultimap> receivedBVal = new BoolMultimap();
    private volatile Map<Long, BoolMultimap> receivedBVal = new ConcurrentHashMap<>();

    // The values `b` for which we already sent `BVal(b)`.
//    private Map<Integer,BoolSet> sentBVal = BoolSet.NONE();
    private Map<Long, BoolSet> sentBVal = new ConcurrentHashMap<>();

    // The nodes that sent us an `Aux(b)`, by `b`.
//    private volatile Map<Integer,BoolMultimap> receivedAux = new BoolMultimap();
    private volatile Map<Long, BoolMultimap> receivedAux = new ConcurrentHashMap<>();

    // Whether the sbv broadcast has already output.
//    private volatile Map<Integer,Boolean> sbvTerminated = false;
    private volatile Map<Long, Boolean> sbvTerminated = new ConcurrentHashMap<>();

    // This round's common coin.
    private Map<Long, Coin> coin = new ConcurrentHashMap<>();

    // Received `Conf` messages. Reset on every epoch update.
//    private volatile Map<Integer, ConfMessage> receivedConf = new ConcurrentHashMap<>();
    private volatile Map<Long, Map<Integer, ConfMessage>> receivedConf = new ConcurrentHashMap<>();

    // Received `Term` messages. Kept throughout epoch updates. These count as `BVal`, `Aux` and
    // `Conf` messages for all future epochs.
//    private volatile Map<Integer, TermMessage> receivedTerm = new ConcurrentHashMap<>();
    private volatile Map<Long, Map<Integer, TermMessage>> receivedTerm = new ConcurrentHashMap<>();

    // The values we found in the first _N - f_ `Aux` messages that were in `bin_values`.
//    private volatile Map<Integer,BoolSet> confValues = BoolSet.NONE();
    private volatile Map<Long,BoolSet> confValues = new ConcurrentHashMap<>();

    // The estimate of the decision value in the current epoch.
    private Map<Long, Boolean> estimate = new ConcurrentHashMap<>();

    private Map<Long, AtomicBoolean> coinStarted = new ConcurrentHashMap<>();

    // A permanent, latching copy of the output value.
    private volatile Boolean decision = null;

    // A cache for messages for future epochs that cannot be handled yet.
    private volatile Map<Long, List<BinaryAgreementMessage>> incomingQueue = new ConcurrentHashMap<>();

    private final MoustefaouiBinaryAgreementMessageFactory messageFactory;

    // Round number as seen at beginning of thread (use instead of round to guarantee consistency)
    private ThreadLocal<Long> myRound =
            ThreadLocal.withInitial(() -> round);

    public MoustefaouiBinaryAgreement(
            String pid,
            Integer replicaId,
            NetworkInfo networkInfo
    ) {
        this.pid = pid;
        this.replicaId = replicaId;
        this.networkInfo = networkInfo;

        this.messageFactory = new MoustefaouiBinaryAgreementMessageFactory(pid, replicaId);
        myRound.set(0L);

        this.binValues.put(0L, BoolSet.NONE());
        this.receivedBVal.put(0L, new BoolMultimap());
        this.sentBVal.put(0L, BoolSet.NONE());
        this.receivedAux.put(0L, new BoolMultimap());
        this.sbvTerminated.put(0L, false);
        this.coin.put(0L, new Coin(pid, this.networkInfo.getSigUtils(), 0));
        this.receivedConf.put(0L, new ConcurrentHashMap<>());
        this.receivedTerm.put(0L, new ConcurrentHashMap<>());
        this.confValues.put(0L, BoolSet.NONE());
        this.coinStarted.put(0L, new AtomicBoolean(false));

        this.messageFactory.setRound(round);
        this.roundStart.put(0L, System.nanoTime());
        this.start = System.nanoTime();
    }

    public void setRound(long r) {
        this.round = r;
        myRound.set(r);
    }

    @Override
    public String getPid() {
        return pid;
    }

    @Override
    public Long getRound() {
        return round;
    }

    @Override
    public Coin getCoin() {
        return coin.computeIfAbsent(myRound.get(), r->
                new Coin(pid, this.networkInfo.getSigUtils(), myRound.get())
        );
    }

    @Override
    public Boolean getEstimate() {
        return estimate.get(myRound.get());
    }

    public BoolSet getBinValues() {
        return binValues.computeIfAbsent(myRound.get(), r -> BoolSet.NONE());
    }

    public BoolMultimap getReceivedBVal() {
        return receivedBVal.computeIfAbsent(myRound.get(), r -> new BoolMultimap());
    }

    public BoolSet getSentBVal() {
        return sentBVal.computeIfAbsent(myRound.get(), r -> BoolSet.NONE());
    }

    public BoolMultimap getReceivedAux() {
        return receivedAux.computeIfAbsent(myRound.get(), r -> new BoolMultimap());
    }

    public boolean getSbvTerminated() {
        return sbvTerminated.computeIfAbsent(myRound.get(), r -> false);
    }

    public Map<Integer, ConfMessage> getReceivedConfMessage() {
        return receivedConf.computeIfAbsent(myRound.get(), r -> new ConcurrentHashMap<>());
    }

    public Map<Integer, TermMessage> getReceivedTermMessage() {
        return receivedTerm.computeIfAbsent(myRound.get(), r -> new ConcurrentHashMap<>());
    }

    public BoolSet getConfValues() {
        return confValues.computeIfAbsent(myRound.get(), r -> BoolSet.NONE());
    }

    public AtomicBoolean getCoinStarted() {
        return coinStarted.computeIfAbsent(myRound.get(), r -> new AtomicBoolean(false));
    }

    public long getRoundStart() {
        return roundStart.computeIfAbsent(myRound.get(), r -> System.nanoTime());
    }

    public long getStartAux() {
        return startAux.computeIfAbsent(myRound.get(), r -> System.nanoTime());
    }

    public long getStartConf() {
        return startConf.computeIfAbsent(myRound.get(), r -> System.nanoTime());
    }

    public long getStartCoin() {
        return startCoin.computeIfAbsent(myRound.get(), r -> System.nanoTime());
    }

    public BoolSet getBinValues(long mround) {
        return binValues.computeIfAbsent(mround, r -> BoolSet.NONE());
    }

    public BoolMultimap getReceivedBVal(long mround) {
        return receivedBVal.computeIfAbsent(mround, r -> new BoolMultimap());
    }

    public BoolSet getSentBVal(long mround) {
        return sentBVal.computeIfAbsent(mround, r -> BoolSet.NONE());
    }

    public BoolMultimap getReceivedAux(long mround) {
        return receivedAux.computeIfAbsent(mround, r -> new BoolMultimap());
    }

    public boolean getSbvTerminated(long mround) {
        return sbvTerminated.computeIfAbsent(mround, r -> false);
    }

    public Map<Integer, ConfMessage> getReceivedConfMessage(long mround) {
        return receivedConf.computeIfAbsent(mround, r -> new ConcurrentHashMap<>());
    }

    public Map<Integer, TermMessage> getReceivedTermMessage(long mround) {
        return receivedTerm.computeIfAbsent(mround, r -> new ConcurrentHashMap<>());
    }

    public Coin getCoin(long mround) {
        return coin.computeIfAbsent(mround, r->
                new Coin(pid, this.networkInfo.getSigUtils(), myRound.get())
        );
    }

    public BoolSet getConfValues(long mround) {
        return confValues.computeIfAbsent(mround, r -> BoolSet.NONE());
    }

    public AtomicBoolean getCoinStarted(long mround) {
        return coinStarted.computeIfAbsent(mround, r -> new AtomicBoolean(false));
    }

    public long getRoundStart(long mround) {
        return roundStart.computeIfAbsent(mround, r -> System.nanoTime());
    }

    public long getStartAux(long mround) {
        return startAux.computeIfAbsent(mround, r -> System.nanoTime());
    }

    public long getStartConf(long mround) {
        return startConf.computeIfAbsent(mround, r -> System.nanoTime());
    }

    public long getStartCoin(long mround) {
        return startCoin.computeIfAbsent(mround, r -> System.nanoTime());
    }

    @Override
    public Step<Boolean> handleInput(Boolean input) {
        myRound.set(round);

        Step<Boolean> step = new Step<>();

        if (Utils.BYZ) return step;

        if (!this.canPropose()) {
            step.addFault(pid, "CANNOT PROPOSE");
            return step;
        }

        System.out.printf("input %s for %s\n", input, pid);

        Utils.abaBValStart(pid, myRound.get());

        // Set the initial estimated value to the input value.
        this.estimate.put(myRound.get(), input);


        // Record the value `b` as sent. If it was already there, don't send it again.
        if (this.getSentBVal().insert(input)) {
            BValMessage bValMessage = messageFactory.createBValMessage(input, myRound.get());
            step.add(this.sendBValMessage(bValMessage));
        }

        Utils.abaBValEnd(pid, myRound.get());
        return step;
    }

    @Override
    public Step<Boolean> handleMessage(BinaryAgreementMessage message) {

        Step<Boolean> step = new Step<>();

        if (Utils.BYZ) return step;

        Long messageRound = message.getRound();

        // Message pid does not match protocol instance
        if (!message.getPid().equals(pid)) {
            step.addFault(pid, "INVALID PID");
            return step;
        }

        // Have to get lock to add messages to incomingQueue, otherwise I can see round=2 with message for 3, someone upgrades
        // round and then I add to pending queue. That message will never be processed.
        synchronized (this) {
            myRound.set(this.round);
            long currentRound = myRound.get();


            // Message is obsolete: We are already in a later epoch or terminated.
            // if (messageRound < currentRound && message.canExpire()) {
            //     step.addFault(pid, "MESSAGE OBSOLETE");
            //     System.out.printf("Message obsolete (%s < %s)\n", messageRound, currentRound);
            //     return step;
            // }

            // Message outside of the valid agreement round range
            if (messageRound > currentRound + this.maxFutureRounds) {
                step.addFault(pid, "MESSAGE OUTSIDE ROUND RANGE");
                return step;
            }

            // Message is for a later epoch. We can't handle that yet.
            if (messageRound > currentRound && message.getType() != TermMessage.TERM) {
                List<BinaryAgreementMessage> received = incomingQueue.computeIfAbsent(messageRound, r -> new ArrayList<>());
                received.add(message);
                return step;
            }
        }

        // Handle message content
        Integer type = message.getType();
        switch (type) {
            case BValMessage.BVAL: {
                step.add(handleBValMessage((BValMessage) message));
                break;
            }
            case AuxMessage.AUX: {
                step.add(handleAuxMessage((AuxMessage) message));
                break;
            }
            case ConfMessage.CONF: {
                step.add(handleConfMessage((ConfMessage) message));
                break;
            }
            case CoinMessage.COIN: {
                step.add(handleCoinMessage((CoinMessage) message));
                break;
            }
            case TermMessage.TERM: {
                step.add(handleTermMessage((TermMessage) message));
                break;
            }
        }

        return step;
    }

    @Override
    public boolean hasTerminated() {
        return this.decision != null;
    }

    @Override
    public Optional<Boolean> deliver() {
        return Optional.ofNullable(decision);
    }

    // Whether we can still input a value. It is not an error to input if this returns `false`,
    // but it will have no effect on the outcome.
    private Boolean canPropose() {
        return this.getRound() == 0L && this.getEstimate() == null;
    }

    // Handles a `BVal(b)` message.
    //
    // Upon receiving _f + 1_ `BVal(b)`, multicasts `BVal(b)`. Upon receiving _2 f + 1_ `BVal(b)`,
    // updates `bin_values`. When `bin_values` gets its first entry, multicasts `Aux(b)`.
    @Override
    public Step<Boolean> handleBValMessage(BValMessage message) {
        final boolean value = message.getValue();
        final long mround = message.getRound();

        Step<Boolean> step = new Step<>();
        if (!this.getReceivedBVal(mround).getIndex(value).add(message.getSender())) {
            step.addFault(pid, "DUPLICATE BVAL MESSAGE");
            return step;
        }

        Utils.abaBValStart(pid, myRound.get());

        int countBVal = this.getReceivedBVal(mround).getIndex(value).size();

        // If received N that are the same, can do an early ending
        if (Utils.EARLY_ABA) {
            if (networkInfo.getN().equals(countBVal) && this.decision == null) {
                step.add(this.decide(value));
                System.out.printf("Deciding via early ABA\n");
            }
        }

        // Upon receiving `2*f + 1` valid `BVAL`s for the same value
        if (countBVal >= (2*networkInfo.getF() + 1)) {

            Utils.abaAuxStart(pid, myRound.get());

            // Add value to bin values set
            this.getBinValues(mround).insert(value);

            // If bin_values != {true,false} (it's the first time I added something to bin_values)
            if (!this.getBinValues(mround).equals(BoolSet.BOTH())) {
                // First entry: send `Aux` for the value.
                AuxMessage auxMessage = messageFactory.createAuxMessage(value, mround);
                step.add(this.sentAuxMessage(auxMessage));
                startAux.put(myRound.get(), System.nanoTime());
            } else {
                // I already had inserted something into bin_values (thus I had already broadcast AUX(v,r))
                // Otherwise just check for `Conf` condition.
                step.add(this.tryOutputSbv(mround));
            }

            Utils.abaAuxEnd(pid, myRound.get());
        }



        // Echo behaviour (if I received f+1 Bvals and didn't send this value)
        if (countBVal == this.networkInfo.getF() + 1 && this.getSentBVal(mround).insert(value)) {
            BValMessage bValMessage = messageFactory.createBValMessage(value, mround);

            boolean includeSelf = !this.getReceivedBVal(mround).getIndex(value).contains(this.replicaId);
            step.add(this.sendMessage(bValMessage, includeSelf));

            Utils.abaBValEnd(pid, myRound.get());
        }

        return step;
    }

    private Step<Boolean> sendBValMessage(BValMessage bValMessage) {
        Step<Boolean> step = new Step<>();
        for (int id=0; id < this.networkInfo.getN(); id++) {
            if (id == this.replicaId) {
                step.add(this.handleBValMessage(bValMessage));
            } else {
                step.add(bValMessage, id);
            }
        }

        return step;
    }

    // Handles an `Aux` message.
    public Step<Boolean> handleAuxMessage(AuxMessage auxMessage) {
        boolean b = auxMessage.getValue();
        if (!getReceivedAux(auxMessage.getRound()).getIndex(b).add(auxMessage.getSender())) {
            return new Step<>();
        }

        return this.tryOutputSbv(auxMessage.getRound());
    }

    private Step<Boolean> sentAuxMessage(AuxMessage auxMessage) {
        Step<Boolean> step = new Step<>();
        for (int id=0; id<this.networkInfo.getN(); id++) {
            if (id == this.replicaId) {
                step.add(this.handleAuxMessage(auxMessage));
            } else {
                step.add(auxMessage, id);
            }
        }

        return step;
    }

    // Handles a `Conf` message. When _N - f_ `Conf` messages with values in `bin_values` have
    // been received, updates the epoch or decides.
    public Step<Boolean> handleConfMessage(ConfMessage message) {

        getReceivedConfMessage(message.getRound()).putIfAbsent(message.getSender(), message);
        Step<Boolean> step = this.tryFinishConfRound(message.getRound());

        return step;
    }

    // Multicast a `Conf(values)` message, and handle it.
    private Step<Boolean> sendConfMessage(ConfMessage message) {
        Step<Boolean> step = new Step<>();
        if (!getConfValues(message.getRound()).equals(BoolSet.NONE())) {
            // Only one `Conf` message is allowed in an epoch.
            return step;
        }

        // Trigger the start of the `Conf` round.
        this.confValues.put(myRound.get(), message.getValue());
        if (!this.networkInfo.isValidator()) {
            return step;
        }

        for (int id=0; id<this.networkInfo.getN(); id++) {
            if (id == this.replicaId) {
                step.add(this.handleConfMessage(message));
            } else {
                step.add(message, id);
            }
        }
        return step;
    }

    /// Handles a `ThresholdSign` message. If there is output, starts the next epoch. The function
    /// may output a decision value.
    public Step<Boolean> handleCoinMessage(CoinMessage message) {
        Step<Boolean> step = new Step<>();

        Coin coin = getCoin(message.getRound());

        if (coin.hasDecided()) {
            return step;
        }

        final int senderId = message.getSender();
        final byte[] share = message.getValue();
        coin.addShare(senderId, share);

        if (coin.hasDecided()) {
            // Utils.abaCoinEnd(pid, myRound.get());
            step.add(this.tryUpdateRound(message.getRound()));
        }

        return step;
    }

    // Handles a `Term(v)` message. If we haven't yet decided on a value and there are more than
    // _f_ such messages with the same value from different nodes, performs expedite termination:
    // decides on `v`, broadcasts `Term(v)` and terminates the instance.
    @Override
    public Step<Boolean> handleTermMessage(TermMessage message) {
        Step<Boolean> step = new Step<>();
        final long mround = message.getRound();

        this.getReceivedTermMessage(mround).putIfAbsent(message.getSender(), message);


        if (this.decision != null) {
            return step;
        }

        Boolean b  = message.getValue();

        if (this.getReceivedTermMessage(mround).values().stream().filter(t -> t.getValue() == b).count() > this.networkInfo.getF()) {
            // Check for the expedite termination condition.
            System.out.printf("Deciding via term at round (%s)\n", message.getRound());
            step.add(this.decide(b));
            return step;
        }

        // FIXME (dsa): Where in the protocol is this mentioned ???
        // Otherwise handle the `Term` as a `BVal`, `Aux` and `Conf`.
        step.add(this.handleBValMessage(new BValMessage(pid, message.getSender(), message.getRound(), b)));
        step.add(this.handleAuxMessage(new AuxMessage(pid, message.getSender(), message.getRound(), b)));
        step.add(this.handleConfMessage(new ConfMessage(pid, message.getSender(), message.getRound(), new BoolSet(Stream.of(b).collect(Collectors.toCollection(HashSet::new))))));

        return step;
    }

    private Step<Boolean> sendTermMessage(TermMessage termMessage) {
        Step<Boolean> step = new Step<>();
        for (int id=0; id<this.networkInfo.getN(); id++) {
            if (id == this.replicaId) {
                step.add(this.handleTermMessage(termMessage));
            } else {
                step.add(termMessage, id);
            }
        }
        return step;
    }

    // Called when there's a change N-f AUX messages are received and there might be progress to make
    private Step<Boolean> tryOutputSbv(long mround) {

        Step<Boolean> step = new Step<>();
        if (this.getSbvTerminated(mround) || getBinValues(mround).equals(BoolSet.NONE())) {
            return step;
        }

        BoolSet aux_values = BoolSet.NONE();
        int aux_count = 0;
        for (Boolean b : this.getBinValues(mround).getValues()) {
            if (!this.getReceivedAux(mround).getIndex(b).isEmpty()) {
                aux_values.insert(b);
                aux_count += this.getReceivedAux(mround).getIndex(b).size();
            }
        }

        if (aux_count < this.networkInfo.getNumCorrect()) {
            return step;
        }


        Utils.abaConfStart(pid, myRound.get());

        this.sbvTerminated.put(mround, true);

        if (!getConfValues(mround).equals(BoolSet.NONE())) {
            // The `Conf` round has already started.
            return step;
        }

        ConfMessage confMessage = messageFactory.createConfMessage(this.getBinValues(mround), mround);
        step.add(this.sendConfMessage(confMessage));
        startConf.put(myRound.get(), System.nanoTime());

        Utils.abaConfEnd(pid, myRound.get());

        // TODO: Check if it's a correct optimization
//        if (this.coin.hasDecided()) {
//            this.confValues = this.binValues;
//            step.add(this.tryUpdateRound());
//        } else {
//            // Start the `Conf` message round.
//            ConfMessage confMessage = messageFactory.createConfMessage(this.binValues);
//            step.add(this.sendConfMessage(confMessage));
//        }

        return step;
    }

    // Checks whether the _N - f_ `Conf` messages have arrived, and if so, activates the coin.
    private Step<Boolean> tryFinishConfRound(long mround) {

        Step<Boolean> step = new Step<>();
        if (this.getConfValues(mround).equals(BoolSet.NONE()) || this.countConf(mround) < this.networkInfo.getNumCorrect()) {
            return step;
        }

        if (!getCoinStarted(mround).getAndSet(true)) {

            // Invoke the coin
            Utils.abaCoinStart(pid, myRound.get());
            byte[] share = getCoin(mround).getMyShare();

            CoinMessage coinMessage = messageFactory.createCoinMessage(share, mround);
            step.add(this.sendCoinMessage(coinMessage));
            startCoin.put(mround, System.nanoTime());

            step.add(this.tryUpdateRound(mround));
        }

        return step;
    }

    private Step<Boolean> sendCoinMessage(CoinMessage coinMessage) {
        Step<Boolean> step = new Step<>();
        for (int id=0; id<this.networkInfo.getN(); id++) {
            if (id == this.replicaId) {
                step.add(this.handleCoinMessage(coinMessage));
            } else {
                step.add(coinMessage, id);
            }
        }
        return step;
    }

    // Counts the number of received `Conf` messages with values in `bin_values`.
    private Integer countConf(long mround) {
        return (int) getReceivedConfMessage(mround).values().stream()
                .filter(cm -> cm.getValue().isSubset(getBinValues(mround))).count();
    }

    // If this round's coin value or conf values are not known yet, does nothing, otherwise
    // updates the round or decides.
    //
    // With two conf values, the next round's estimate is the coin value. If there is only one conf
    // value and that disagrees with the coin, the conf value is the next round's estimate. If
    // the unique conf value agrees with the coin, terminates and decides on that value.
    @Override
    public Step<Boolean> tryUpdateRound(long mround) {
        Step<Boolean> step = new Step<>();

        if (this.decision != null || !getCoin(mround).hasDecided() || getConfValues(mround).equals(BoolSet.NONE())) {
            return step;
        }

        Utils.abaFinishStart(pid, myRound.get());

        Set<Boolean> defBinValues = getConfValues(mround).getValues();
        Boolean coinValue = getCoin(mround).getValue();
        if (defBinValues.size() == 1) {
            if (defBinValues.contains(coinValue)) {
                Utils.abaFinishEnd(pid, myRound.get());
                step.add(this.decide(coinValue));
            } else {
                step.add(this.updateRound(defBinValues.iterator().next(), mround));
            }
        } else {
            step.add(this.updateRound(coinValue, mround));
        }


        return step;
    }

    private Step<Boolean> updateRound(Boolean estimate, long mround) {

        Step<Boolean> step = new Step<>();

        // If the round of message where this was called is smaller than my round, just give up
        if (mround < myRound.get()) {
            return step;
        }

        boolean updated = false;
        synchronized (this) {
            if (this.round == myRound.get()) {
                System.out.printf("Updating round %s\n", this.round);
                updated = true;
                this.estimate.put(this.round, estimate);
                this.round++;
                this.messageFactory.setRound(this.round);
                this.roundStart.put(this.round, System.nanoTime());
            }
        }

        if (updated) {
            this.myRound.set(this.round);
            if (this.getSentBVal().insert(estimate)) {
                Utils.abaBValStart(pid, myRound.get());
                BValMessage bValMessage = messageFactory.createBValMessage(estimate, myRound.get());
                step.add(this.sendBValMessage(bValMessage));
            }

            // Deliver pending messages
            List<BinaryAgreementMessage> received = incomingQueue.put(this.round, new ArrayList<>());
            if (received != null) {
                for (BinaryAgreementMessage message: received) {
                    step.add(this.handleMessage(message));
                }
            }
            Utils.abaBValEnd(pid, myRound.get());
        }

        return step;
    }

    // Decides on a value and broadcasts a `Term` message with that value.
    private Step<Boolean> decide(Boolean value) {
        Step<Boolean> step = new Step<>();

        // synchronized (this) {
            // if (this.decision != null) return step;

            // Latch the decided state.
            // TODO: remove
            this.decision = value;
            step.add(decision);

            long r = myRound.get();
            Utils.abaBValStart(pid, r);
            Utils.abaBValEnd(pid, r);
            Utils.abaAuxStart(pid, r);
            Utils.abaAuxEnd(pid, r);
            Utils.abaConfStart(pid, r);
            Utils.abaConfEnd(pid, r);
            Utils.abaCoinStart(pid, r);
            Utils.abaCoinEnd(pid, r);
            Utils.abaFinishStart(pid, r);
            Utils.abaFinishEnd(pid, r);
        // }

        TermMessage message = messageFactory.createTermMessage(value, myRound.get());
        step.add(this.sendTermMessage(message));

        return step;
    }

    private Step<Boolean> sendMessage(BinaryAgreementMessage message, Boolean includeSelf) {
        Step<Boolean> step = new Step<>();

        if (Utils.BYZ) return step;

        for (int target=0; target < this.networkInfo.getN(); target++) {
            if (target == this.replicaId) {
                if (includeSelf) step.add(this.handleMessage(message));
            } else {
                step.add(message, target);
            }
        }
        return step;
    }
}
