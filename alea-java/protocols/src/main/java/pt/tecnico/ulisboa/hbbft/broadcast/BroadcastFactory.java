package pt.tecnico.ulisboa.hbbft.broadcast;

public interface BroadcastFactory {
    IBroadcast create();
    IBroadcast create(String pid, Integer proposerId);
}
