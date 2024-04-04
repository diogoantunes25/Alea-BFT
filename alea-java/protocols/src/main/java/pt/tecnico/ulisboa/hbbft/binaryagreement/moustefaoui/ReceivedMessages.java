package pt.tecnico.ulisboa.hbbft.binaryagreement.moustefaoui;


import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementMessage;
import pt.tecnico.ulisboa.hbbft.binaryagreement.moustefaoui.messages.*;

import java.util.*;

// Binary Agreement messages received from other nodes for a particular Binary Agreement epoch.
public class ReceivedMessages {

    // Received `BVal` messages.
    private Map<Integer, BValMessage> bValMessages = new TreeMap<>();

    // Received `Aux` messages.
    private Map<Integer, AuxMessage> auxMessages = new TreeMap<>();

    // Received `Coin` messages.
    private Map<Integer, CoinMessage> coinMessages = new TreeMap<>();

    // Received `Conf` message.
    private Map<Integer, ConfMessage> confMessages = new TreeMap<>();

    // Received `Term` message.
    private Map<Integer, TermMessage> termMessages = new TreeMap<>();

    public void insert(BinaryAgreementMessage message) {
        Integer type = message.getType();
        switch (type) {
            case BValMessage.BVAL: {
                this.insert((BValMessage) message); break;
            }
            case AuxMessage.AUX: {
                this.insert((AuxMessage) message); break;
            }
            case CoinMessage.COIN: {
                this.insert((CoinMessage) message); break;
            }
            case ConfMessage.CONF: {
                this.insert((ConfMessage) message); break;
            }
            case TermMessage.TERM: {
                this.insert((TermMessage) message); break;
            }
        }
    }

    public void insert(BValMessage message) {
        this.bValMessages.putIfAbsent(message.getSender(), message);
    }

    public void insert(AuxMessage message) {
        this.auxMessages.putIfAbsent(message.getSender(), message);
    }

    public void insert(CoinMessage message) {
        this.coinMessages.putIfAbsent(message.getSender(), message);
    }

    public void insert(ConfMessage message) {
        this.confMessages.putIfAbsent(message.getSender(), message);
    }

    public void insert(TermMessage message) {
        this.termMessages.putIfAbsent(message.getSender(), message);
    }

    // Creates message content from `ReceivedMessages`. That message content can then be handled.
    public List<BinaryAgreementMessage> getMessages() {
        List<BinaryAgreementMessage> messages = new ArrayList<>();

        messages.addAll(bValMessages.values());
        messages.addAll(auxMessages.values());
        messages.addAll(coinMessages.values());
        messages.addAll(confMessages.values());
        messages.addAll(termMessages.values());

        return messages;
    }
}
