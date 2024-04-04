package pt.tecnico.ulisboa.hbbft.binaryagreement.moustefaoui;

import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.binaryagreement.IBinaryAgreement;
import pt.tecnico.ulisboa.hbbft.binaryagreement.moustefaoui.messages.*;

public interface IMoustefaouiBinaryAgreement extends IBinaryAgreement {
    // Getters
    Long getRound();
    Coin getCoin();
    Boolean getEstimate();

    // Message handlers
    Step<Boolean> handleBValMessage(BValMessage message);
    Step<Boolean> handleAuxMessage(AuxMessage message);
    Step<Boolean> handleConfMessage(ConfMessage message);
    Step<Boolean> handleCoinMessage(CoinMessage message);
    Step<Boolean> handleTermMessage(TermMessage message);

    // Flow control
    Step<Boolean> tryUpdateRound(long mround);
}
