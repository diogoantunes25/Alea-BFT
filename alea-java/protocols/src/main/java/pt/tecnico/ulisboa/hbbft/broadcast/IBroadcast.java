package pt.tecnico.ulisboa.hbbft.broadcast;

import pt.tecnico.ulisboa.hbbft.IProtocol;

public interface IBroadcast extends IProtocol<byte[], byte[], BroadcastMessage> {
    String getPid();
}
