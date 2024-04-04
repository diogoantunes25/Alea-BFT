package pt.tecnico.ulisboa.hbbft.broadcast.bracha2.erasureCodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import pt.tecnico.ulisboa.hbbft.utils.jerasures.ErasureCodec;

public class ErasureCodesUtils {

    // Blocks needed to recover original input
    private int k;

    // Blocks that can be lost
    private int m;

    private volatile int originalSize;

    private static final int WORD_SIZE = 8;

    private ErasureCodec codec;

    public ErasureCodesUtils(int k, int m, int size) {
        this.k = k;
        this.m = m;
        codec = new ErasureCodec.Builder(ErasureCodec.Algorithm.Reed_Solomon)
                .dataBlockNum(k)
                .codingBlockNum(m)
                .wordSize(WORD_SIZE)
                .build();

        originalSize = size;
    }

    // Returns list of k+m arrays
    public List<byte[]> encode(byte[] input) {
        byte[][] data = vectorToMatrix(input);

        byte[][] encoded = codec.encode(data);

        List<byte[]> blocks = new ArrayList<>();
        for (int i = 0; i < data.length; i++) blocks.add(data[i]);
        for (int i = 0; i < encoded.length; i++) blocks.add(encoded[i]);


        return blocks;
    }

    // parts only has to have k entries
    public byte[] decode(Map<Integer, byte[]> parts) {
        try {

            int[] erasures = new int[m];
            int erased = 0;
            for (int i = 0; i < k+m; i++) {
                if (!parts.containsKey(i)) erasures[erased++] = i;
            }

            int size = -1;
            int j = 0;
            while (size == -1) {
                if (parts.containsKey(j)) size = parts.get(j).length;
                j++;
            }

            byte[][] data = new byte[k][size];
            for (int i = 0; i < k; i++) {
                if (parts.containsKey(i)) {
                    System.arraycopy(parts.get(i), 0, data[i], 0, size);
                }
            }

            byte[][] encoded = new byte[m][size];
            for (int i = 0; i < m; i++) {
                if (parts.containsKey(i+k)) {
                    System.arraycopy(parts.get(i+k), 0, encoded[i], 0, size);
                }
            }

            codec.decode(erasures, data, encoded);

            return matrixToVector(data, originalSize);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    public byte[][] vectorToMatrix(byte[] v) {
        int wordCount = (int) Math.ceil(v.length / (double) WORD_SIZE);
        int wordsPerLine = (int) Math.ceil(wordCount / (double) k);
        int size = wordsPerLine * WORD_SIZE;
        byte[][] data = new byte[k][size];

        int r;
        for (r = 0; (r+1) * WORD_SIZE < v.length; r++) {
            System.arraycopy(v, r * WORD_SIZE, data[r / wordsPerLine], (r % wordsPerLine) * WORD_SIZE, WORD_SIZE);
        }

        System.arraycopy(v, r * WORD_SIZE, data[r / wordsPerLine], (r % wordsPerLine) * WORD_SIZE, v.length - r * WORD_SIZE);

        return data;
    }

    public byte[] matrixToVector(byte[][] m, int originalSize) {
        byte[] v = new byte[originalSize];

        int r = 0;
        while (r * WORD_SIZE + WORD_SIZE < originalSize) {
            System.arraycopy(m[(r*WORD_SIZE) / m[0].length], (r*WORD_SIZE) % m[0].length, v, r * WORD_SIZE, WORD_SIZE);
            r++;
        }

        System.arraycopy(m[(r*WORD_SIZE)/m[0].length], (r*WORD_SIZE)%m[0].length, v, r * WORD_SIZE, originalSize - r * WORD_SIZE);

        return v;
    }

    public static byte[][] mergeMatrices(byte[][] m1, byte[][] m2) {
        byte[][] m = new byte[m1.length + m2.length][m1[0].length];

        int i;
        for (i = 0; i < m1.length; i++) {
            System.arraycopy(m1[i], 0, m[i], 0, m1[0].length);
        }

        for (int j = 0; j < m2.length; j++) {
            System.arraycopy(m2[j], 0, m[i+j], 0, m1[0].length);
        }

        return m;
    }

    public static void printVector(int[] vector) {
        for (int j = 0; j < vector.length; ++j) {
            System.out.printf("%02x ", vector[j]);
        }
        System.out.println();
    }

    public static void printVector(byte[] vector) {
        for (int j = 0; j < vector.length; ++j) {
            System.out.printf("%02x ", vector[j]);
        }
        System.out.println();
    }

    public static void printMatrix(byte[][] matrix) {
        for (int i = 0; i < matrix.length; ++i) {
            for (int j = 0; j < matrix[i].length; ++j) {
                System.out.printf("%02x ", matrix[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }
}
