package pt.tecnico.ulisboa.hbbft.utils.threshsig.exceptions;

public class NotEnoughSharesException extends Exception {
    private int _existing;
    private int _required;

    public NotEnoughSharesException(int existing, int required) {
        _existing = existing;
        _required = required;
    }
}
