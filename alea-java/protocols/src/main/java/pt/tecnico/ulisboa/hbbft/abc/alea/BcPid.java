package pt.tecnico.ulisboa.hbbft.abc.alea;

public class BcPid {

    private final String protocolId;
    private final Integer queueId;
    private final Long slotId;

    public BcPid(String pid) {
        String[] components = pid.split("-");
        assert components.length == 3;
        this.protocolId = components[0];
        this.queueId = Integer.valueOf(components[1]);
        this.slotId = Long.valueOf(components[2]);
    }

    public BcPid(String protocolId, Integer queueId, Long slotId) {
        this.protocolId = protocolId;
        this.queueId = queueId;
        this.slotId = slotId;
    }

    public String getProtocolId() {
        return protocolId;
    }

    public Integer getQueueId() {
        return queueId;
    }

    public Long getSlotId() {
        return slotId;
    }

    @Override
    public String toString() {
        return String.format("%s-%d-%d", protocolId, queueId, slotId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BcPid bcPid = (BcPid) o;

        if (!protocolId.equals(bcPid.protocolId)) return false;
        if (!queueId.equals(bcPid.queueId)) return false;
        return slotId.equals(bcPid.slotId);
    }

    @Override
    public int hashCode() {
        int result = protocolId.hashCode();
        result = 31 * result + queueId.hashCode();
        result = 31 * result + slotId.hashCode();
        return result;
    }
}
