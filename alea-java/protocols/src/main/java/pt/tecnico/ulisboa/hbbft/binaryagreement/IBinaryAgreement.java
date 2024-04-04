package pt.tecnico.ulisboa.hbbft.binaryagreement;

import pt.tecnico.ulisboa.hbbft.IProtocol;

public interface IBinaryAgreement extends IProtocol<Boolean, Boolean, BinaryAgreementMessage> {
    String getPid();
}
