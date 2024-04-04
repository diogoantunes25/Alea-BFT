package pt.ulisboa.tecnico.replica.exceptions;

public class KeysNotFoundException extends Exception {
    private int n;

    public KeysNotFoundException(int n) {
        this.n = n;
    }
}
