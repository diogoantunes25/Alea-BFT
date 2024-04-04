package pt.tecnico.ulisboa.hbbft.broadcast.echo.utils;

import java.security.*;

public class SignatureUtils {

    /**
     * Sign a message.
     *
     * @param key the private key to be used to generate the signature
     * @param message the message to be signed
     * @return the signature
     */
    public static byte[] sign(PrivateKey key, byte[] message) {
        try {
            Signature signatureEngine = Signature.getInstance("SHA256withRSA");
            signatureEngine.initSign(key);
            signatureEngine.update(message);
            return signatureEngine.sign();

        } catch (Exception e) {
            return new byte[0];
        }
    }

    /**
     * Verify the signature of a message.
     *
     * @param key the public key to be used to verify the signature
     * @param message the signed message
     * @param signature the signature to be verified
     * @return true if the signature is valid, false otherwise
     */
    public static boolean verify(PublicKey key, byte[] message, byte[] signature) {
        try {
            Signature signatureEngine = Signature.getInstance("SHA256withRSA");
            signatureEngine.initVerify(key);
            signatureEngine.update(message);
            return signatureEngine.verify(signature);

        } catch (Exception e) {
            return false;
        }
    }

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom rnd = SecureRandom.getInstance("SHA1PRNG");
        keyPairGenerator.initialize(2048, rnd);
        return keyPairGenerator.generateKeyPair();
    }
}
