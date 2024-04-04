package pt.tecnico.ulisboa.hbbft.broadcast.bracha2;

import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastFactory;
import pt.tecnico.ulisboa.hbbft.broadcast.IBroadcast;

import java.util.concurrent.atomic.AtomicLong;

public class BrachaBroadcast2Factory implements BroadcastFactory {

    private final Integer replicaId;
    private final NetworkInfo networkInfo;

    private AtomicLong count = new AtomicLong();

    public BrachaBroadcast2Factory(Integer replicaId, NetworkInfo networkInfo) {
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
        return new BrachaBroadcast2(pid, replicaId, networkInfo, proposerId);
    }
}
