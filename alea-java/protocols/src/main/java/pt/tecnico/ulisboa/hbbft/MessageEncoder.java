package pt.tecnico.ulisboa.hbbft;

public interface MessageEncoder<T> {

    /**
     * Encodes the given {@link ProtocolMessage} into a
     * transmissible format specified by the class parameter.
     *
     * @param message the request to encode
     * @return the encoded request
     */
    T encode(ProtocolMessage message);

    ProtocolMessage decode(T data);
}
