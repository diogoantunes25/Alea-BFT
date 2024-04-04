package pt.tecnico.ulisboa.hbbft.abc.alea.benchmark;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExecutionLog<T> {

    private final String pid;
    private final Long start;
    private Long finish;
    private T result;

    private List<ExecutionLog<?>> children = new ArrayList<>();

    public ExecutionLog(String pid) {
        this.pid = pid;
        this.start = ZonedDateTime.now().toInstant().toEpochMilli();
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

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.finish = ZonedDateTime.now().toInstant().toEpochMilli();
        this.result = result;
    }

    public List<ExecutionLog<?>> getChildren() {
        return children;
    }

    public void setChildren(List<ExecutionLog<?>> children) {
        this.children = children;
    }
}
