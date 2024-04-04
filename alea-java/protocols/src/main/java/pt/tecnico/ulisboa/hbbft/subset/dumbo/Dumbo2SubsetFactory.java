package pt.tecnico.ulisboa.hbbft.subset.dumbo;

import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.subset.IAsynchronousCommonSubset;
import pt.tecnico.ulisboa.hbbft.subset.SubsetFactory;

import java.util.concurrent.atomic.AtomicLong;

public class Dumbo2SubsetFactory implements SubsetFactory {

    private final Integer replicaId;
    private final NetworkInfo networkInfo;

    // private final PrbcFactory prbcFactory;
    // private final MvbaFactory mvbaFactory;

    private final AtomicLong count = new AtomicLong();

    public Dumbo2SubsetFactory(Integer replicaId, NetworkInfo networkInfo) {
        this.replicaId = replicaId;
        this.networkInfo = networkInfo;
    }


    @Override
    public IAsynchronousCommonSubset create() {
        String pid = String.format("ACS-%d", count.getAndIncrement());
        return this.create(pid);
    }

    @Override
    public IAsynchronousCommonSubset create(String pid) {
        return new Dumbo2Subset(pid, replicaId, networkInfo);
    }
}