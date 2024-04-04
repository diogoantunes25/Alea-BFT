package pt.tecnico.ulisboa.hbbft.abc.alea.benchmark;

public class Interval {

    private final String pid;
    private final Long start;

    private Long finish;

    public Interval(String pid, Long start) {
        this.pid = pid;
        this.start = start;
    }

    public String getPid() {
        return pid;
    }

    public Long getStart() {
        return start;
    }

    public Long getFinish() {
        return finish;
    }

    public void setFinish(Long finish) {
        this.finish = finish;
    }
}
