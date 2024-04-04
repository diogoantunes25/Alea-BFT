package pt.tecnico.ulisboa.hbbft.abc.alea;

import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.Transaction;
import pt.tecnico.ulisboa.hbbft.abc.Block;
import pt.tecnico.ulisboa.hbbft.abc.IAtomicBroadcast;
import pt.tecnico.ulisboa.hbbft.abc.alea.messages.FillGapMessage;
import pt.tecnico.ulisboa.hbbft.abc.alea.messages.FillerMessage;
import pt.tecnico.ulisboa.hbbft.abc.alea.queue.PriorityQueue;
import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementMessage;
import pt.tecnico.ulisboa.hbbft.binaryagreement.IBinaryAgreement;
import pt.tecnico.ulisboa.hbbft.vbroadcast.IVBroadcast;
import pt.tecnico.ulisboa.hbbft.vbroadcast.VBroadcastMessage;
import pt.tecnico.ulisboa.hbbft.vbroadcast.VOutput;

import java.util.Map;
import java.util.Set;

public interface IAlea extends IAtomicBroadcast {
    Integer getReplicaId();
    NetworkInfo getNetworkInfo();
    Long getPriority();
    Map<Integer, PriorityQueue> getQueues();
    IVBroadcast getVBroadcastInstance(BcPid bcPid);
    IBinaryAgreement getBinaryAgreementInstance(BaPid baPid);
    Set<Transaction> getExecuted();

    /**
     * Called by the protocol to indicate that
     * a {@link VBroadcastMessage} has been received.
     *
     * @param message the received message
     */
    Step<Block> handleVBroadcastMessage(VBroadcastMessage message);

    /**
     * Called by the protocol to indicate that
     * a {@link BinaryAgreementMessage} has been received.
     *
     * @param message the received message
     */
    Step<Block> handleBinaryAgreementMessage(BinaryAgreementMessage message);

    /**
     * Called by the protocol to indicate that
     * a {@link AleaMessage} has been received.
     *
     * @param message the received message
     */
    Step<Block> handleProtocolMessage(ProtocolMessage message);

    /**
     * Called by the protocol to indicate that
     * a {@link FillGapMessage} has been received.
     *
     * @param fillGapMessage the received message
     */
    Step<Block> handleFillGapMessage(FillGapMessage fillGapMessage);

    /**
     * Called by the protocol to indicate that
     * a {@link FillerMessage} has been received.
     *
     * @param fillerMessage the received message
     */
    Step<Block> handleFillerMessage(FillerMessage fillerMessage);


    Step<Block> handleVBroadcastStep(Step<VOutput> bcStep, BcPid bcPid);

    Step<Block> handleBinaryAgreementStep(Step<Boolean> baStep, BaPid baPid);
}
