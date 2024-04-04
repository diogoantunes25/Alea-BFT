package pt.ulisboa.tecnico.replica;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.herumi.bls.Bls;

import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.AbstractThreshSigFactory;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.bls.BLSSigFactory;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.dummy.DummyUtils;
import pt.ulisboa.tecnico.replica.domain.Replica;
import pt.ulisboa.tecnico.replica.exceptions.KeysNotFoundException;
import pt.ulisboa.tecnico.utils.Utils;

public class ReplicaMain {

    private static final String KEY_FILE = "/tmp/bls.keys";

    public static CommandLineParser parser = new DefaultParser();
    public static Options options = new Options();

    static {
        System.loadLibrary("blsjava");
        Bls.init(Bls.BN254);
    }

    // Initialize options
    static {
        options.addOption(new Option("g", "generateKeys", true, "Run program for generating keys only for n up to arg"));
    }

    public static int readIntOption(String optionName, CommandLine cmd) {
        try {
           return Integer.parseInt(cmd.getOptionValue(optionName));
        } catch (NumberFormatException e) {
            System.err.printf("Failed to read %s\n", optionName);
            System.exit(1);
        }

        return -1;
    }

    public static void main(String args[]) throws ParseException, IOException, ClassNotFoundException, KeysNotFoundException, InterruptedException {
        CommandLine cmd = parser.parse(options, args);

        String blsKeysFile = KEY_FILE;

        if (cmd.hasOption("generateKeys")) {
            int maxN = Integer.parseInt(cmd.getOptionValue("generateKeys"));
            generateBLSKeys(maxN, blsKeysFile);
            return;
        }

        if (args.length < 7) {
            System.out.printf("Not enough arguments. 7 needed, only provided %d\n", args.length);
            System.out.print("Usage: replica [-g ARG] <batch> <protocol> <id> <n> <nclients> <parties file> <results file>\n");
            System.exit(2);
        }

        int batch = Integer.parseInt(args[0]);
        String protocolName = args[1];
        int id = Integer.parseInt(args[2]);
        int n = Integer.parseInt(args[3]);
        int nclients = Integer.parseInt(args[4]);
        String partiesFile = args[5];
        String resultsFile = args[6];

        Utils.setOutFile(resultsFile);


        NetworkInfo.ValidatorSet validatorSet = new NetworkInfo.ValidatorSet(
                IntStream.range(0, n).boxed().collect(Collectors.toSet()),
                n/3);

        // NetworkInfo networkInfo = new NetworkInfo(id, validatorSet, new DummyUtils(n));

        AbstractThreshSigFactory factory = loadBLSKeys(n, blsKeysFile);
        NetworkInfo networkInfo = new NetworkInfo(id, validatorSet, factory.get(id));

        List<InetAddress> replicas = loadReplicas(partiesFile, n);
        List<InetAddress> clients = loadClients(partiesFile, nclients);

        Replica.Protocol protocol = Replica.Protocol.ALEA;
        if (protocolName.equals("hb")) {
            protocol = Replica.Protocol.HB;
        }
        else if (protocolName.equals("dumbo")) {
            protocol = Replica.Protocol.DUMBO;
        } else if (!protocolName.equals("alea")) {
            System.out.printf("Invalid protocol %s\n", protocolName);
            System.exit(2);
        }

        Replica.ReplicaConfig config = Replica.ReplicaConfig.newBuilder()
                .setBatchsize(batch)
                .build();

        Replica replica = new Replica(id, networkInfo, replicas, protocol, config, clients);
        replica.waitReplicasUp(replicas);
        replica.waitClientsUp(clients);
        replica.start();
        System.exit(0);
    }

    public static List<InetAddress> loadReplicas(String partiesFile, int n) {

        System.out.print("Loading replicas from file...");
        System.out.flush();
        try {
            Reader reader = new FileReader(partiesFile);
            List<InetAddress> replicasAddr = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            JSONArray replicas = (JSONArray) jsonObject.get("replicas");
            Iterator<String> replicaIt = replicas.iterator();
			String replicaHost = replicaIt.next();
            while (replicasAddr.size() < n) {
				try {
					replicasAddr.add(InetAddress.getByName(replicaHost));
					if (replicasAddr.size() < n) replicaHost = replicaIt.next();
				}
				catch (IOException e) {
				}
            }

            System.out.println("Done");
            return replicasAddr;
        } catch (org.json.simple.parser.ParseException | IOException e) {
            e.printStackTrace();
			throw new RuntimeException(e);
        }
    }

    public static List<InetAddress> loadClients(String partiesFile, int n) {

        System.out.print("Loading clients from file...");
        System.out.flush();
        try {
            Reader reader = new FileReader(partiesFile);
            List<InetAddress> clientsAddr = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            JSONArray clients = (JSONArray) jsonObject.get("clients");
            Iterator<String> clientIt = clients.iterator();
			String replicaHost = clientIt.next();
            while (clientsAddr.size() < n) {
				try {
					clientsAddr.add(InetAddress.getByName(replicaHost));
					if (clientsAddr.size() < n) replicaHost = clientIt.next();
				}
				catch (IOException e) {
				}
            }

            System.out.println("Done");
            return clientsAddr;
        } catch (org.json.simple.parser.ParseException | IOException e) {
            e.printStackTrace();
			throw new RuntimeException(e);
        }
    }

    public static void generateBLSKeys(int maxN, String keysFile) throws IOException {
        ObjectOutputStream outs = new ObjectOutputStream(new FileOutputStream(keysFile));

        outs.writeInt((maxN-1) / 3);
        for (int f = 1; 3 * f + 1 <= maxN; f++) {
            int n = 3 * f + 1;
            BLSSigFactory factory = new BLSSigFactory();
            factory.init(n);
            outs.writeObject(factory.getSerializable());
        }
        System.out.printf("Generated BLS keys for n up to %d. Saved to %s\n", maxN, keysFile);

        outs.flush();
        outs.close();
    }

    public static AbstractThreshSigFactory loadBLSKeys(int n, String keysFile) throws IOException, ClassNotFoundException, KeysNotFoundException {
        System.out.print("Loading cached keys...");
        System.out.flush();
        ObjectInputStream ins = new ObjectInputStream(new FileInputStream(keysFile));
        try {
            int c = ins.readInt();
            for (int i = 0; i < c; i++) {
                BLSSigFactory.SerializableFactory temp = (BLSSigFactory.SerializableFactory) ins.readObject();
                if (temp.getN() == n) {
                    return temp.toFactory();
                }
            }

            throw new KeysNotFoundException(n);
        } finally {
            System.out.println("Done");
            ins.close();
        }

    }
}
