package pt.tecnico.ulisboa.hbbft.binaryagreement;

public interface BinaryAgreementFactory {
    IBinaryAgreement create();
    IBinaryAgreement create(String pid);
}
