package pt.tecnico.ulisboa.hbbft.vbroadcast;

import pt.tecnico.ulisboa.hbbft.IProtocol;

public interface IVBroadcast extends IProtocol<byte[], VOutput, VBroadcastMessage> {
    String getPid();
}
