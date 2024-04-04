package pt.tecnico.ulisboa.hbbft.broadcast.avid.crypto.merkle;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

// A Merkle tree: The leaves are values and their hashes. Each level consists of the hashes of
// pairs of values on the previous level. The root is the value in the first level with only one
// entry.
public class MerkleTree {

    private List<byte[]> values;
    private List<List<byte[]>> levels;
    private byte[] rootHash;

    // Creates a new Merkle tree with the given values.
    public MerkleTree(List<byte[]> values) {
        this.values = values;
        this.levels = new ArrayList<>();
        List<byte[]> currentLevel = values.stream().map(MerkleTree::hash).collect(Collectors.toList());
        while (currentLevel.size() > 1) {
            List<byte[]> nextLevel = buildLevel(currentLevel);
            levels.add(nextLevel);
            currentLevel = nextLevel;
        }
        this.rootHash = currentLevel.get(0);
    }

    public byte[] getRootHash() {
        return rootHash;
    }

    private List<byte[]> buildLevel(List<byte[]> children) {
        List<byte[]> level = new ArrayList<>(children.size()/2);
        for (int i=0; i < children.size() - 1; i+=2) {
            byte[] child1 = children.get(i);
            byte[] child2 = children.get(i+1);
            if (child2 == null) level.add(child1);
            else level.add(hashPair(child1, child2));
        }
        return level;
    }

    // Returns the proof for entry `index`, if that is a valid index.
    public Optional<Proof> getProof(Integer index) {
        final byte[] value = this.values.get(index);
        if (value == null) return Optional.empty();

        int levelIndex = index;
        List<byte[]> digests = new ArrayList<>();

        // add leaf pair
        byte[] digest = values.get(levelIndex ^ 1);
        if (digest != null) digests.add(digest);
        levelIndex /= 2;

        for (List<byte[]> level : this.levels) {
            // Insert the sibling hash if there is one.
            if (level.size() <= (levelIndex ^ 1)) continue;
            digest = level.get(levelIndex ^ 1);
            if (digest != null) digests.add(digest);
            levelIndex /= 2;
        }

        return Optional.of(new Proof(value, index, digests, this.rootHash));
    }

    public static byte[] hash(byte[] value) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA3-256");
            return md.digest(value);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] hashPair(byte[] v1, byte[] v2) {
        byte[] value = new byte[v1.length + v2.length];
        System.arraycopy(v1, 0, value, 0, v1.length);
        System.arraycopy(v2, 0, value, v1.length, v2.length);
        return hash(value);
    }

    public static byte[] hashChunk(List<byte[]> chunk) {
        if (chunk.size() == 1) return chunk.get(0);
        else return hashPair(chunk.get(0), chunk.get(1));
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (byte[] leaf : values)
            out.append(Base64.getEncoder().encodeToString(leaf)).append(" | ");
        out.append("\n");
        for (List<byte[]> level: levels) {
            StringBuilder lvl = new StringBuilder();
            for (byte[] v : level) {
                lvl.append(Base64.getEncoder().encodeToString(v)).append(" | ");
            }
            out.append(lvl.toString()).append("\n");
        }
        return out.toString();
    }
}
