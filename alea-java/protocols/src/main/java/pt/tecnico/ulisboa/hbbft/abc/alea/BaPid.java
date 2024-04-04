package pt.tecnico.ulisboa.hbbft.abc.alea;

public class BaPid {

    private final String protocolId;
    private final Long epoch;

    public BaPid(String pid) {
        String[] components = pid.split("-");
        assert components.length == 2;
        this.protocolId = components[0];
        this.epoch = Long.valueOf(components[1]);
    }

    public BaPid(String protocolId, Long epoch) {
        this.protocolId = protocolId;
        this.epoch = epoch;
    }

    public Long getEpoch() {
        return epoch;
    }

    @Override
    public String toString() {
        return String.format("%s-%d", protocolId, epoch);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaPid baPid = (BaPid) o;

        if (!protocolId.equals(baPid.protocolId)) return false;
        return epoch.equals(baPid.epoch);
    }

    @Override
    public int hashCode() {
        int result = protocolId.hashCode();
        result = 31 * result + epoch.hashCode();
        return result;
    }
}
