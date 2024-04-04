package pt.tecnico.ulisboa.hbbft.binaryagreement.moustefaoui;

import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementFactory;
import pt.tecnico.ulisboa.hbbft.binaryagreement.IBinaryAgreement;

import java.util.concurrent.atomic.AtomicLong;

public class MoustefaouiBinaryAgreementFactory implements BinaryAgreementFactory {

    private final Integer replicaId;
    private final NetworkInfo networkInfo;

    private final AtomicLong count = new AtomicLong();

    public MoustefaouiBinaryAgreementFactory(
            Integer replicaId,
            NetworkInfo networkInfo
    ) {
        this.replicaId = replicaId;
        this.networkInfo = networkInfo;
    }

    @Override
    public IBinaryAgreement create() {
        String pid = String.format("BA-%d", count.getAndIncrement());
        return this.create(pid);
    }

    @Override
    public IBinaryAgreement create(String pid) {
        return new MoustefaouiBinaryAgreement(pid, replicaId, networkInfo);
    }
}
