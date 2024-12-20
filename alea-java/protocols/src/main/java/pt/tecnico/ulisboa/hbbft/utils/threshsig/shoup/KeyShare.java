package pt.tecnico.ulisboa.hbbft.utils.threshsig.shoup;

import pt.tecnico.ulisboa.hbbft.utils.threshsig.shoup.ShoupSigShare;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Random;

/**
 * A Secret Key Share for an RSA (k,l) Threshold Scheme
 *
 * Reference: "Practical Threshold Signatures",<br>
 * Victor Shoup (sho@zurich.ibm.com), IBM Research Paper RZ3121, 4/30/99<BR>
 *
 * @author Steve Weis <sweis@mit.edu>
 */
public class KeyShare implements Serializable {
    // Constants and variables
    //............................................................................
    /** Secret key value */
    private BigInteger secret;
    /** Verifier used to authenticate self to other shares */
    private BigInteger verifier;
    private BigInteger groupVerifier;

    // TODO: This information should be moved to a GroupKey object
    // It's redundant to put into every share
    private BigInteger n;
    private final BigInteger delta;

    /** The secret key value used to sign messages. Should not be exposed */
    private BigInteger signVal;
    // TODO: Maybe get a better id scheme. Use verifier?
    private int id;

    // TODO-Rand: This is klunky.
    // Maybe use a utility class with a RNG for everyone
    private static SecureRandom random;
    private MessageDigest md;
    static {
        final byte[] randSeed = new byte[20];
        (new Random()).nextBytes(randSeed);
        random = new SecureRandom(randSeed);
    }

    // Constructors
    //............................................................................

    /**
     * Create a new share
     *
     * @param id - the identifier of this share
     * @param secret - a secret value generated by a Dealer
     * @param n - the modulo of the group public key
     * @param delta - l! (group size factorial)
     */
    public KeyShare(final int id, final BigInteger secret, final BigInteger n, final BigInteger delta) {
        this.id = id;
        this.secret = secret;

        // Verifier must be set later using setVerifier()
        // TODO: Merge Dealer.generateShares and Dealer.generateVerifiers
        // and generate them simultaneously
        verifier = null;

        this.n = n;
        this.delta = delta;
        signVal = ThreshUtil.FOUR.multiply(delta).multiply(secret);
    }

    // Public Methods
    //............................................................................

    public int getId() {
        return id;
    }

    public BigInteger getSecret() {
        return secret;
    }

    public void setVerifiers(final BigInteger verifier, final BigInteger groupVerifier) {
        this.verifier = verifier;
        this.groupVerifier = groupVerifier;
    }

    public BigInteger getVerifier() {
        return verifier;
    }

    public BigInteger getSignVal() {
        return signVal;
    }

    @Override
    public String toString() {
        return "KeyShare[" + id + "]";
    }

    /**
     * Create a SigShare and a Verifier for byte[] b<BR>
     *
     * Refer to Shoup pg. 8 <BR>
     *
     * @param b The array of bytes to produce a signature share for.
     * @return a sig share with a verifier
     */
    public ShoupSigShare sign(final byte[] b) {
        final BigInteger x = (new BigInteger(b)).mod(n);

        final int randbits = n.bitLength() + 3 * ThreshUtil.L1;

        // r \elt (0, 2^L(n)+3*l1)
        final BigInteger r = (new BigInteger(randbits, random));
        final BigInteger vprime = groupVerifier.modPow(r, n);
        final BigInteger xtilde = x.modPow(ThreshUtil.FOUR.multiply(delta), n);
        final BigInteger xprime = xtilde.modPow(r, n);

        BigInteger c = null;
        BigInteger z = null;
        // Try to generate C and Z
        try {
            md = MessageDigest.getInstance("SHA");
            md.reset();

            // debug("v: " + groupVerifier.mod(n));
            md.update(groupVerifier.mod(n).toByteArray());

            // debug("xtilde: " + xtilde);
            md.update(xtilde.toByteArray());

            // debug("vi: " + verifier.mod(n));
            md.update(verifier.mod(n).toByteArray());

            // debug("xi^2: " + x.modPow(signVal,n).modPow(TWO,n));
            md.update(x.modPow(signVal, n).modPow(ThreshUtil.TWO, n).toByteArray());

            // debug("v': "+ vprime);
            md.update(vprime.toByteArray());

            // debug("x': " + xprime);
            md.update(xprime.toByteArray());
            c = new BigInteger(md.digest()).mod(n);
            z = (c.multiply(secret)).add(r);
        } catch (final java.security.NoSuchAlgorithmException e) {
            debug("Provider could not locate SHA message digest .");
            e.printStackTrace();
        }

        final Verifier ver = new Verifier(z, c, verifier, groupVerifier);

        return new ShoupSigShare(id, x.modPow(signVal, n), ver);
    }

    // Debugging
    //............................................................................
    private static void debug(final String s) {
        System.err.println("KeyShare: " + s);
    }


}
