package pt.tecnico.ulisboa.hbbft.agreement.mvba;

public class VcbcPid {

    private final String protocol;
    private final Integer proposer;

    public VcbcPid(String pid) {
        String[] components = pid.split("-");
        assert components.length == 2;

        this.protocol = components[0];
        this.proposer = Integer.valueOf(components[1]);
    }

    public VcbcPid(Integer proposer) {
        this.protocol = "VCBC";
        this.proposer = proposer;
    }

    public String getProtocol() {
        return protocol;
    }

    public Integer getProposer() {
        return proposer;
    }

    @Override
    public String toString() {
        return String.format("%s-%d", protocol, proposer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VcbcPid vcbcPid = (VcbcPid) o;

        if (!protocol.equals(vcbcPid.protocol)) return false;
        return proposer.equals(vcbcPid.proposer);
    }

    @Override
    public int hashCode() {
        int result = protocol.hashCode();
        result = 31 * result + proposer.hashCode();
        return result;
    }
}
