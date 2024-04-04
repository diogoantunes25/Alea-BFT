package pt.tecnico.ulisboa.hbbft.agreement.mvba;

public class VbaPid {

    private final String protocol;
    private final Long round;

    public VbaPid(String pid) {
        String[] components = pid.split("-");
        assert components.length == 2;

        this.protocol = components[0];
        this.round = Long.valueOf(components[1]);
    }

    public VbaPid(Long round) {
        this.protocol = "VBA";
        this.round = round;
    }

    public String getProtocol() {
        return protocol;
    }

    public Long getRound() {
        return round;
    }

    @Override
    public String toString() {
        return String.format("%s-%d", protocol, round);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VbaPid baPid = (VbaPid) o;

        if (!protocol.equals(baPid.protocol)) return false;
        return round.equals(baPid.round);
    }

    @Override
    public int hashCode() {
        int result = protocol.hashCode();
        result = 31 * result + round.hashCode();
        return result;
    }
}
