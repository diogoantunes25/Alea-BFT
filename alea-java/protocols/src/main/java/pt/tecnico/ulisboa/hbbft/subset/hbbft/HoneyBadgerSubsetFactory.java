package pt.tecnico.ulisboa.hbbft.subset.hbbft;

import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementFactory;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastFactory;
import pt.tecnico.ulisboa.hbbft.subset.SubsetFactory;

import java.util.concurrent.atomic.AtomicLong;

public class HoneyBadgerSubsetFactory implements SubsetFactory {

    private final Integer replicaId;
    private final NetworkInfo networkInfo;
    private final BroadcastFactory bcFactory;
    private final BinaryAgreementFactory baFactory;

    private final AtomicLong count = new AtomicLong();

    public HoneyBadgerSubsetFactory(
            Integer replicaId,
            NetworkInfo networkInfo,
            BroadcastFactory bcFactory,
            BinaryAgreementFactory baFactory
    ) {
        this.replicaId = replicaId;
        this.networkInfo = networkInfo;
        this.bcFactory = bcFactory;
        this.baFactory = baFactory;
    }

    @Override
    public HoneyBadgerSubset create() {
        return this.create(String.format("ACS-%d", count.getAndIncrement()));
    }

    @Override
    public HoneyBadgerSubset create(String pid) {
        return new HoneyBadgerSubset(pid, replicaId, networkInfo, bcFactory, baFactory);
    }
}
