package pt.tecnico.ulisboa.hbbft.example.vbroadcast;

public class Main {

    /*private enum Protocol {
        ECHO,
        PRBC,
    }

    private static final int TOLERANCE = 1;
    private static final int NUM_REPLICAS = 3 * TOLERANCE + 1;
    private static final int KEY_SIZE = 256;

    public static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");

        Map<Integer, ValidatedBroadcastReplica> replicas = setupReplicas();
    }

    private static Map<Integer, ValidatedBroadcastReplica> setupReplicas() {
        CountDownLatch readyLatch = new CountDownLatch(NUM_REPLICAS - 1);

        Set<Integer> validators = IntStream.range(0, NUM_REPLICAS)
                .boxed().collect(Collectors.toSet());
        NetworkInfo.ValidatorSet validatorSet = new NetworkInfo.ValidatorSet(validators, TOLERANCE);

        // Generate key pairs
        Map<Integer, KeyPair> keyPairs = new TreeMap<>();
        try {
            for (int i=0; i < NUM_REPLICAS; i++)
                keyPairs.put(i, SignatureUtils.generateKeyPair());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // Initialize message encoder
        MessageEncoder<String> rbcEncoder = new BrachaBroadcastMessageEncoder();
        MessageEncoder<String> encoder = new PrbcMessageEncoder(rbcEncoder);

        // Initialize transport layer
        Map<Integer, BlockingQueue<String>> messageQueue = new ConcurrentHashMap<>();
        Transport<String> transport = new LocalTransport(messageQueue);

        Map<Integer, BroadcastReplica> replicas = new TreeMap<>();
        for (int replicaId: validatorSet.getAllIds()) {
            NetworkInfo networkInfo = new NetworkInfo(replicaId, validatorSet);

            BroadcastFactory rbcFactory = new BrachaBroadcastFactory(replicaId, networkInfo);
        }
    }*/
}
