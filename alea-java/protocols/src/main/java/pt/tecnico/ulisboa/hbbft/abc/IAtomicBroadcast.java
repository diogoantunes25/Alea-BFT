package pt.tecnico.ulisboa.hbbft.abc;

import pt.tecnico.ulisboa.hbbft.IProtocol;
import pt.tecnico.ulisboa.hbbft.ProtocolMessage;

/**
 * An atomic broadcast is a protocol where inputs are array of bytes (any transaction),
 * the outputs are blocks (of transactions - since a transaction might be submitted but several transaction need to be
 * executed - because other clients also submitted transactions)
 * and the message is an abstract Protocol Message.
 */
public interface IAtomicBroadcast extends IProtocol<byte[], Block, ProtocolMessage> {

    public void reset();

    public default void stop() { }
}
