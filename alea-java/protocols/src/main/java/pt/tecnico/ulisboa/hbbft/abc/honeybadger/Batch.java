package pt.tecnico.ulisboa.hbbft.abc.honeybadger;

import java.util.Map;

public class Batch {

    private final Long epochId;
    private final Map<Integer, byte[]> contributions;

    public Batch(Long epochId, Map<Integer, byte[]> contributions) {
        this.epochId = epochId;
        this.contributions = contributions;
    }

    public Long getEpochId() {
        return epochId;
    }

    public Map<Integer, byte[]> getContributions() {
        return contributions;
    }

    @Override
    public String toString() {
        return "Batch{" +
                "epochId=" + epochId +
                ", contributions=" + contributions +
                '}';
    }
}
