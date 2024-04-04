package pt.tecnico.ulisboa.hbbft.abc.alea;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AleaLog {

    private final Map<Long, Long> bcDuration = new ConcurrentHashMap<>();

    private final Map<Long, Long> baDuration = new ConcurrentHashMap<>();
    private final Map<Long, Boolean> baResults = new ConcurrentHashMap<>();
}
