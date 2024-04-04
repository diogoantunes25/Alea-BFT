package pt.tecnico.ulisboa.hbbft.broadcast.echo;

import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastFactory;
import pt.tecnico.ulisboa.hbbft.broadcast.IBroadcast;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class EchoBroadcastFactory implements BroadcastFactory {

    private final Integer replicaId;
    private final NetworkInfo networkInfo;
    private final PrivateKey privateKey;
    private final Map<Integer, PublicKey> publicKeys;

    private AtomicLong count = new AtomicLong();

    public EchoBroadcastFactory(
            Integer replicaId,
            NetworkInfo networkInfo,
            PrivateKey privateKey,
            Map<Integer, PublicKey> publicKeys
    ) {
        this.replicaId = replicaId;
        this.networkInfo = networkInfo;
        this.privateKey = privateKey;
        this.publicKeys = publicKeys;
    }

    @Override
    public IBroadcast create() {
        String pid = String.format("BC-%d-%d", replicaId, count.getAndIncrement());
        return create(pid, replicaId);
    }

    @Override
    public IBroadcast create(String pid, Integer proposerId) {
        return new EchoBroadcast(pid, replicaId, networkInfo, proposerId, privateKey, publicKeys);
    }
}
