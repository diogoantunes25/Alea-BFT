package pt.ulisboa.tecnico.client;

import org.apache.commons.cli.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import pt.ulisboa.tecnico.utils.Utils;
import pt.ulisboa.tecnico.client.domain.Client;

import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClientMain {

    public static void main(String args[]) throws ParseException, IOException, ClassNotFoundException, InterruptedException {

        if (args.length < 6) {
            System.out.printf("Not enough arguments. 6 needed, only provided %d\n", args.length);
            System.out.print("Usage: client <load> <protocol> <id> <n> <parties file> <results file>\n");
            System.exit(2);
        }

        int load = Integer.parseInt(args[0]);
        String protocolName = args[1];
        int id = Integer.parseInt(args[2]);
        int n = Integer.parseInt(args[3]);
        String partiesFile = args[4];
        String resultsFile = args[5];

        Utils.setOutFile(resultsFile);

        List<InetAddress> replicas = loadReplicas(partiesFile, n);

        int oksRequested = 1;
        int f = n / 3;

        if (!protocolName.equals("alea")) {
            oksRequested = 2 * f + 1;
        }

        Client client = new Client(replicas, id, oksRequested, load);
        client.waitReplicasUp(replicas);
        client.start();

        System.out.printf("[%s] exiting\n", id);
        System.exit(0);
    }

    public static List<InetAddress> loadReplicas(String hostsFile, int n) {

        System.out.print("Loading replicas from file...");
        try {
            Reader reader = new FileReader(hostsFile);
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
					e.printStackTrace();
					Thread.sleep(100);
				}
            }

            return replicasAddr;
        } catch (org.json.simple.parser.ParseException | IOException | InterruptedException e) {
            e.printStackTrace();
			throw new RuntimeException(e);
        } finally {
            System.out.println("Done");
        }
    }
}
