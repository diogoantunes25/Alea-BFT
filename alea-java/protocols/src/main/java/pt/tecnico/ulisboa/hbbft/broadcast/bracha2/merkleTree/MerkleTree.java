package pt.tecnico.ulisboa.hbbft.broadcast.bracha2.merkleTree;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MerkleTree {

    // Note: the three root is at mt[1]
    private long[] mt;

    public MerkleTree(List<byte[]> blocks) {
        int n = blocks.size();
        int leafCount = (int) Math.pow(2, ceil(Math.log(n)/Math.log(2)));
        mt = new long[2 * leafCount + 1];

        for (int i = 0; i < n; i++) {
            mt[leafCount+i] = hash(blocks.get(i));
        }

        for (int i = leafCount - 1; i > 0; i--) {
            mt[i] = hash(BigInteger.valueOf(mt[2*i] + mt[2*i+1]).toByteArray());
        }
    }

    public long getRootHash() {
        return mt[1];
    }

    public List<Long> getMerkleBranch(int index) {
        List<Long> ans = new ArrayList<>();

        int t = index + (mt.length >> 1);
        while (t > 1) {
            ans.add(mt[t ^ 1]);
            t /= 2;
        }

        return ans;
    }


    public static boolean merkleVerify(byte[] val, long rootHash, List<Long> branch, int index) {
        long tmp = hash(val);
        int tindex = index;
        for (long br: branch) {
            if ((tindex & 1) != 0) {
                tmp = hash(BigInteger.valueOf(br + tmp).toByteArray());
            } else {
                tmp = hash(BigInteger.valueOf(tmp + br).toByteArray());
            }
            tindex >>= 1;
        }

        if (tmp != rootHash) {
            return false;
        }

        return true;
    }

    public static long hash(byte[] x) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(x);
            return (new BigInteger(hash)).longValue();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public String toString() {
        return Arrays.toString(mt);
    }

    public static int ceil(double x) {
        return (int) Math.ceil(x);
    }
    public static int ceil(int x) {
        return (int) Math.ceil(x);
    }
}
