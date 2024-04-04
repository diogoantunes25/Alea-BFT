package pt.tecnico.ulisboa.hbbft;

import java.util.Collections;
import java.util.List;

public class TargetedMessage {

    private final ProtocolMessage content;
    private final List<Integer> targets;

    public TargetedMessage(ProtocolMessage content, Integer target) {
        this(content, Collections.singletonList(target));
    }

    public TargetedMessage(ProtocolMessage content, List<Integer> targets) {
        this.content = content;
        this.targets = targets;
    }

    public ProtocolMessage getContent() {
        return content;
    }

    public Integer getTarget() {
        return targets.get(0);
    }

    public List<Integer> getTargets() {
        return targets;
    }

    public String toString() {
        return "TargetedMessage(" + content + ", to: " + targets +")";
    }

}
