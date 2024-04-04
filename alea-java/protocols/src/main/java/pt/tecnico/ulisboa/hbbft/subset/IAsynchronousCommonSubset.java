package pt.tecnico.ulisboa.hbbft.subset;

import pt.tecnico.ulisboa.hbbft.IProtocol;

public interface IAsynchronousCommonSubset extends IProtocol<byte[], Subset, SubsetMessage> {
    String getPid();
}
