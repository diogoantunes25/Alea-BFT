package pt.tecnico.ulisboa.hbbft.vbroadcast.prbc;

import pt.tecnico.ulisboa.hbbft.IProtocol;
import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.vbroadcast.VOutput;

public interface IProvableReliableBroadcast extends IProtocol<byte[], VOutput, ProtocolMessage> {
    String getPid();
}
