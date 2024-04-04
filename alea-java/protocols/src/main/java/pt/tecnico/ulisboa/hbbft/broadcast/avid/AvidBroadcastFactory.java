package pt.tecnico.ulisboa.hbbft.broadcast.avid;

import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastFactory;
import pt.tecnico.ulisboa.hbbft.broadcast.IBroadcast;

import java.util.concurrent.atomic.AtomicLong;

public class AvidBroadcastFactory implements BroadcastFactory {

    private final Integer replicaId;
    private final NetworkInfo networkInfo;

    private AtomicLong count = new AtomicLong();

    public AvidBroadcastFactory(Integer replicaId, NetworkInfo networkInfo) {
        this.replicaId = replicaId;
        this.networkInfo = networkInfo;
    }


    @Override
    public IBroadcast create() {
        String pid = String.format("BC-%d-%d", replicaId, count.getAndIncrement());
        return create(pid, replicaId);
    }

    @Override
    public IBroadcast create(String pid, Integer proposerId) {
        return new AvidBroadcast(pid, replicaId, networkInfo, proposerId);
    }
}
