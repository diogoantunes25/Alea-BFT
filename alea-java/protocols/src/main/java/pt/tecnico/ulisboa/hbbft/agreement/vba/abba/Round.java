package pt.tecnico.ulisboa.hbbft.agreement.vba.abba;

import pt.tecnico.ulisboa.hbbft.agreement.vba.abba.messages.CoinMessage;
import pt.tecnico.ulisboa.hbbft.agreement.vba.abba.messages.MainVoteMessage;
import pt.tecnico.ulisboa.hbbft.agreement.vba.abba.messages.PreVoteMessage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Round {

    private final long number;
    private boolean lastRound;

    private final Map<Integer, PreVoteMessage> preVoteMessages = new HashMap<>();
    private final Map<Integer, MainVoteMessage> mainVoteMessages = new HashMap<>();
    private final Map<Integer, CoinMessage> coinVoteMessages = new HashMap<>();

    public Round(long number) {
        this.number = number;
        this.lastRound = false;
    }

    public boolean isLastRound() {
        return lastRound;
    }

    public void setLastRound(boolean lastRound) {
        this.lastRound = lastRound;
    }

    public long getNumber() {
        return number;
    }

    public  Map<Integer, PreVoteMessage> getPreVoteMessages() {
        return preVoteMessages;
    }

    public Map<Integer, MainVoteMessage> getMainVoteMessages() {
        return mainVoteMessages;
    }

    public Map<Integer, CoinMessage> getCoinMessages() {
        return coinVoteMessages;
    }
}
