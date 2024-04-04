package pt.tecnico.ulisboa.hbbft.abc.acs;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class ExecutionLog {

    private final String pid;
    private final Long start;
    private final AtomicLong recvCount = new AtomicLong();
    private final AtomicLong sendCount = new AtomicLong();

    private List<ExecutionLog> childrenLogs;
    private Long finish;

    public ExecutionLog(String pid) {
        this.pid = pid;
        this.start = ZonedDateTime.now().toInstant().toEpochMilli();
    }

    private void logRecvEvent() {
        this.recvCount.incrementAndGet();
    }

    private void logSendEvent(int inc) {
        this.sendCount.addAndGet(inc);
    }

    private void logDeliveryEvent(List<ExecutionLog> childrenLogs) {
        this.childrenLogs = childrenLogs;
        this.finish = ZonedDateTime.now().toInstant().toEpochMilli();
    }

    public String getPid() {
        return pid;
    }

    public Long getStart() {
        return start;
    }

    public AtomicLong getRecvCount() {
        return recvCount;
    }

    public AtomicLong getSendCount() {
        return sendCount;
    }

    public List<ExecutionLog> getChildrenLogs() {
        return childrenLogs;
    }

    public Long getFinish() {
        return finish;
    }
}
