package pt.ulisboa.tecnico.client.exceptions;

public class KeysNotFoundException extends Exception {
    private int n;

    public KeysNotFoundException(int n) {
        this.n = n;
    }
}
