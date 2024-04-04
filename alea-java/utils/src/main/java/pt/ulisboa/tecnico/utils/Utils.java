package pt.ulisboa.tecnico.utils;

import com.sun.management.OperatingSystemMXBean;

import java.io.*;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Generic benchmarking utils.
 * Codification used is the following:
 *  - Generic
 *      - Experiment start              init, timestamp
 *      - Experiment end                end, timestamp
 *      - Machine status update         status,totalmem,freemem,cpuload,totalGCs,timeGC,freeRAM
 *  - For client
 *      - transaction submitted         in,timestamp,txId
 *      - transaction confirmed         out,timestamp,txId
 *  - Alea (high-level) events:
 *      - alea start:                   as,timestamp,txId
 *      - batch allocate:               bcs,timestamp,bcId,tx1Id,tx2Id,...,txnId
 *      - broadcast start:              bcs,timestamp,bcId
 *      - broadcast end:                bce,timestamp,bcId,positionId
 *      - agreement allocate:           baa,timestamp,abaId,positionId
 *      - agreement start:              bas,timestamp,abaId
 *      - agreement end:                bae,timestamp,abaId,result
 *      - alea end:                     ae,timestamp,txId
 *      - handle broadcast start:       hbcs,timestamp,bcId
 *      - handle broadcast end:         hbce,timestamp,bcId
 *      - handle agreement start:       hbas,timestamp,baId
 *      - handle agreement end:         hbae,timestamp,baId
 *  - VCBC events:
 *      - protocol latency              bclat,timestamp,bcId
 *      - protocol init start           vbcis,timestamp,bcId
 *      - protocol init end             vbcie,timestamp,bcId
 *      - protocol final start          vbcfs,timestamp,bcId
 *      - protocol final end            vbcfe,timestamp,bcId
 *  - ABA events:
 *      - protocol latency              balat, timestamp,baId
 *      - bval send start               ababs,timestamp,baId,round
 *      - bval send end                 ababe,timestamp,baId,round
 *      - aux send start                abaauxs,timestamp,baId,round
 *      - aux send end                  abaauxe,timestamp,baId,round
 *      - conf send start               abaconfs,timestamp,baId,round
 *      - conf send end                 abaconfe,timestamp,baId,round
 *      - coin start                    abacoins,timestamp,baId,round
 *      - coin end                      abacoine,timestamp,baId,round
 *      - finish start                  abafins,timestamp,baId,round
 *      - finish end                    abafine,timestamp,baId,round
 *  - ACS events:
 *      - epoch start                   acses,timestamp,eid
 *      - epoch end                     acsee,timestamp,eid
 *
 *  Note: time is in nanoseconds
 */
public class Utils {
    static transient OutputStream os;
    static private final int BUFFER_SIZE = 2 << 17;             // 64 Kb

    static private final boolean AUTO_SYS_STATUS = true;
    static private final long SYS_UPDATE_PERIOD = 1000; // every second
    private static final OperatingSystemMXBean OS
            = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();


    // Globals to control optimizations
    public static boolean TXT_SHUFFLE = true;
    public static boolean DELAY_ABA = false;
    public static boolean DELAY_BC = true;
    public static boolean QUICK_SIG_VER = true;
    public static boolean EARLY_ABA = true;

    public static boolean CRASHED = false;
    public static boolean BYZ = false;

    static private final Map<Long, Long> bcStarts = new ConcurrentHashMap<>();
    static private final Map<Long, Long> baStarts = new ConcurrentHashMap<>();

    public static boolean isCrashed() {
        return CRASHED;
    }

    public static void setCrashed(boolean Crashed) {
        Utils.CRASHED = Crashed;
    }

    public static AtomicBoolean running = new AtomicBoolean(false);

    public static boolean isByz() {
        return BYZ;
    }

    public static void setByz(boolean Byz) {
        Utils.BYZ = Byz;
    }

    public static void setTxtShuffle(boolean txtShuffle) {
        TXT_SHUFFLE = txtShuffle;
    }

    public static void setDelayAba(boolean delayAba) {
        DELAY_ABA = delayAba;
    }

    public static void setDelayBc(boolean delayBc) {
        DELAY_BC = delayBc;
    }

    public static void setQuickSigVer(boolean quickSigVer) {
        QUICK_SIG_VER = quickSigVer;
    }

    public static void setEarlyAba(boolean earlyAba) {
        EARLY_ABA = earlyAba;
    }

    public static boolean isTxtShuffle() {
        return TXT_SHUFFLE;
    }

    public static boolean isDelayAba() {
        return DELAY_ABA;
    }

    public static boolean isDelayBc() {
        return DELAY_BC;
    }

    public static boolean isQuickSigVer() {
        return QUICK_SIG_VER;
    }

    public static boolean isEarlyAba() {
        return EARLY_ABA;
    }

    public static void setOutFile(String filename) {
        System.out.printf("Setting Utils out file...");
        System.out.flush();
        try {
            File file = new File(filename);
            file.createNewFile();
            os = new BufferedOutputStream(new FileOutputStream(file), BUFFER_SIZE);
            if (AUTO_SYS_STATUS) {
                running.set(true);
                launchSysStatusDaemon();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                running.set(false);
                os.flush();
                os.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));

        // init();
        System.out.println("Done");
    }

    private static void launchSysStatusDaemon() {
        new Thread(() -> {
            while (running.get()) {
                try {
                    Runtime rt = Runtime.getRuntime();
                    double cpuLoad = OS.getSystemLoadAverage() / OS.getAvailableProcessors();

                    long totalGarbageCollections = 0;
                    long garbageCollectionTime = 0;

                    for(GarbageCollectorMXBean gc :
                            ManagementFactory.getGarbageCollectorMXBeans()) {

                        long count = gc.getCollectionCount();

                        if(count >= 0) {
                            totalGarbageCollections += count;
                        }

                        long time = gc.getCollectionTime();

                        if(time >= 0) {
                            garbageCollectionTime += time;
                        }
                    }

                    write("status", String.format("%s,%s,%s,%s,%s,%s", rt.totalMemory(), rt.freeMemory(), cpuLoad, totalGarbageCollections, garbageCollectionTime, OS.getFreePhysicalMemorySize()));

                    Thread.sleep(SYS_UPDATE_PERIOD);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private static long sumArray(byte[] a) {
        long s = 0;
        for (byte e: a) s+=e;
        return Math.abs(s);
    }

    private static void write(String event, String props) {
        try {
            // Can't be use nanoTime to make comparisions between machines
            // String content = String.format("%s,%s,%s\n", event, System.nanoTime(), props);
            String content = String.format("%s,%s,%s\n", event, System.currentTimeMillis(), props);
            os.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    public static void init() {
        write("init", "");
        try {
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    public static void end() {
        write("end", "");
    }

    public static void clientSubmit(long tid) {
        write("in", String.format("%s", tid));
    }

    public static void clientConfirm(long tid) {
        write("out", String.format("%s", tid));
    }

    public static void aleaStart(byte[] tx) {
        // write("as", String.format("%s", sumArray(tx)));
    }

    public static void batchAllocate(String bcId, Collection<byte[]> txs) {
        // String txsStr = "";
        // for (byte[] tx: txs) txsStr += "," + sumArray(tx);
        // write("ba", String.format("%s%s", bcId, txsStr));
    }

    public static void bcStart(String bcId) {
        // bcStarts.putIfAbsent(bcToUniqueNumber(bcId), System.currentTimeMillis());
    }

    public static void bcEnd(String bcId, String positionId) {
        // try {
        //     long bcIdL = bcToUniqueNumber(bcId);
        //     long lat = System.currentTimeMillis() - bcStarts.remove(bcIdL);
        //     write("bclat", "" + lat);
        // } catch (RuntimeException e) {
        // }
    }

    public static void baAllocate(String baId, String positionId) {
    }

    public static void baStart(String baId) {
        // baStarts.putIfAbsent(baToUniqueNumber(baId), System.currentTimeMillis());
    }

    public static void baEnd(String baId, boolean result) {
        // try {
        //     long baIdL = baToUniqueNumber(baId);
        //     long lat = System.currentTimeMillis() - bcStarts.remove(baIdL);
        //     write("balat", "" + lat);
        // } catch (RuntimeException e) {
        // }
    }

    public static void aleaEnd(byte[] tx) {
        // write("ae", String.format("%s", sumArray(tx)));
    }

    public static void acsEpochStart(long eid) {
        // write("acses", String.format("%s", eid));
    }

    public static void acsEpochEnd(long eid) {
        // write("acsee", String.format("%s", eid));
    }

    public static void handleBc(String bcId) {
        // write("hbcs", String.format("%s", bcId));
    }

    public static void doneHandleBc(String bcId) {
        // write("hbce", String.format("%s", bcId));
    }

    public static void handleBa(String baId) {
        // write("hbas", String.format("%s", baId));
    }

    public static void doneHandleBa(String baId) {
        // write("hbae", String.format("%s", baId));
    }

    public static void vcbcInitStart(String bcId) {
        // write("vbcis", String.format("%s", bcId));
    }

    public static void vcbcInitEnd(String bcId) {
        // write("vbcie", String.format("%s", bcId));
    }

    public static void vcbcFinalStart(String bcId) {
        // write("vbcfs", String.format("%s", bcId));
    }

    public static void vcbcFinalEnd(String bcId) {
        // write("vbcfe", String.format("%s", bcId));
    }

    public static void abaBValStart(String baId, long round) {
        // write("ababs", String.format("%s,%s", baId, round));
    }
    public static void abaBValEnd(String baId, long round) {
        // write("ababs", String.format("%s,%s", baId, round));
        // write("ababe", String.format("%s,%s", baId, round));
    }

    public static void abaAuxStart(String baId, long round) {
        // write("ababs", String.format("%s,%s", baId, round));
        // write("ababe", String.format("%s,%s", baId, round));
        // write("abaauxs", String.format("%s,%s", baId, round));
    }

    public static void abaAuxEnd(String baId, long round) {
        // write("ababs", String.format("%s,%s", baId, round));
        // write("ababe", String.format("%s,%s", baId, round));
        // write("abaauxs", String.format("%s,%s", baId, round));
        // write("abaauxe", String.format("%s,%s", baId, round));
    }

    public static void abaConfStart(String baId, long round) {
        // write("ababs", String.format("%s,%s", baId, round));
        // write("ababe", String.format("%s,%s", baId, round));
        // write("abaauxs", String.format("%s,%s", baId, round));
        // write("abaauxe", String.format("%s,%s", baId, round));
        // write("abaconfs", String.format("%s,%s", baId, round));
    }
    public static void abaConfEnd(String baId, long round) {
        // write("ababs", String.format("%s,%s", baId, round));
        // write("ababe", String.format("%s,%s", baId, round));
        // write("abaauxs", String.format("%s,%s", baId, round));
        // write("abaauxe", String.format("%s,%s", baId, round));
        // write("abaconfs", String.format("%s,%s", baId, round));
        // write("abaconfe", String.format("%s,%s", baId, round));
    }

    public static void abaCoinStart(String baId, long round) {
        // write("ababs", String.format("%s,%s", baId, round));
        // write("ababe", String.format("%s,%s", baId, round));
        // write("abaauxs", String.format("%s,%s", baId, round));
        // write("abaauxe", String.format("%s,%s", baId, round));
        // write("abaconfs", String.format("%s,%s", baId, round));
        // write("abaconfe", String.format("%s,%s", baId, round));
        // write("abacoins", String.format("%s,%s", baId, round));
    }

    public static void abaCoinEnd(String baId, long round) {
        // write("ababs", String.format("%s,%s", baId, round));
        // write("ababe", String.format("%s,%s", baId, round));
        // write("abaauxs", String.format("%s,%s", baId, round));
        // write("abaauxe", String.format("%s,%s", baId, round));
        // write("abaconfs", String.format("%s,%s", baId, round));
        // write("abaconfe", String.format("%s,%s", baId, round));
        // write("abacoins", String.format("%s,%s", baId, round));
        // write("abacoine", String.format("%s,%s", baId, round));
    }

    public static void abaFinishStart(String baId, long round) {
        // write("ababs", String.format("%s,%s", baId, round));
        // write("ababe", String.format("%s,%s", baId, round));
        // write("abaauxs", String.format("%s,%s", baId, round));
        // write("abaauxe", String.format("%s,%s", baId, round));
        // write("abaconfs", String.format("%s,%s", baId, round));
        // write("abaconfe", String.format("%s,%s", baId, round));
        // write("abacoins", String.format("%s,%s", baId, round));
        // write("abacoine", String.format("%s,%s", baId, round));
        // write("abafins", String.format("%s,%s", baId, round));
    }

    public static void abaFinishEnd(String baId, long round) {
        // write("ababs", String.format("%s,%s", baId, round));
        // write("ababe", String.format("%s,%s", baId, round));
        // write("abaauxs", String.format("%s,%s", baId, round));
        // write("abaauxe", String.format("%s,%s", baId, round));
        // write("abaconfs", String.format("%s,%s", baId, round));
        // write("abaconfe", String.format("%s,%s", baId, round));
        // write("abacoins", String.format("%s,%s", baId, round));
        // write("abacoine", String.format("%s,%s", baId, round));
        // write("abafins", String.format("%s,%s", baId, round));
        // write("abafine", String.format("%s,%s", baId, round));
    }

    public static long bcToUniqueNumber(String bcPid) {
        String[] tokens = bcPid.split("-");
        long slot = Long.valueOf(tokens[2]);
        long queue = Long.valueOf(tokens[1]);
        return slot * 100 + queue;
    }

    public static long baToUniqueNumber(String baPid) {
        String[] tokens = baPid.split("-");
        long epoch = Long.valueOf(tokens[1]);
        return epoch;
    }
}
