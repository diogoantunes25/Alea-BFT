package pt.tecnico.ulisboa.hbbft.agreement.vba;

import pt.tecnico.ulisboa.hbbft.IProtocol;

public interface IValidatedBinaryAgreement extends IProtocol<ValidatedBoolean, ValidatedBoolean, ValidatedBinaryAgreementMessage> {
    String getPid();
}
