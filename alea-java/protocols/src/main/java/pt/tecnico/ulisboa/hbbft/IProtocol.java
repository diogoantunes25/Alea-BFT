package pt.tecnico.ulisboa.hbbft;

import java.util.Optional;

public interface IProtocol<I, O, M extends ProtocolMessage> {

    /**
     * Handles input from client.
     * @param input
     * @return protocol step resulting of handling input (outputs + messages to peers)
     */
    Step<O> handleInput(I input);

    /**
     * Handles message from peer.
     * @param message
     * @return protocol step resulting of handling output (outputs + messages to peers)
     */
    Step<O> handleMessage(M message);

    boolean hasTerminated();

    Optional<O> deliver();
}
