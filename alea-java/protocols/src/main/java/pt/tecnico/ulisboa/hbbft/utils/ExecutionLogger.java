package pt.tecnico.ulisboa.hbbft.utils;

import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.TargetedMessage;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExecutionLogger {

    private final List<Event> events = new ArrayList<>();

    public void logEvent(ProtocolMessage trigger, Step<?> result) {
        final long timestamp = ZonedDateTime.now().toInstant().toEpochMilli();
        this.events.add(new Event(timestamp, trigger, result));
    }

    public void printLog() {
        for (Event event: events) {
            System.out.println(event);
        }
    }

    public static class Event {
        private final Long timestamp;
        private final ProtocolMessage trigger;
        private final Step<?> result;

        public Event(Long timestamp, ProtocolMessage trigger, Step<?> result) {
            this.timestamp = timestamp;
            this.trigger = trigger;
            this.result = result;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public ProtocolMessage getTrigger() {
            return trigger;
        }

        public Step<?> getResult() {
            return result;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Event:");
            sb.append("timestamp=").append(timestamp).append("\n");

            sb.append("trigger:").append("\n");
            sb.append("|--- ").append(trigger).append("\n");

            sb.append("result:").append("\n");
            for (TargetedMessage tm: result.getMessages()) {
                sb.append("|--- ").append(tm.getContent()).append("\n");
            }

            return sb.toString();
        }
    }
}
