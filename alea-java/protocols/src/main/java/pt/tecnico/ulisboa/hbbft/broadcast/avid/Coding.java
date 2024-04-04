package pt.tecnico.ulisboa.hbbft.broadcast.avid;


import pt.tecnico.ulisboa.hbbft.broadcast.avid.crypto.reedsolomon.ReedSolomon;

public class Coding {

    // A `ReedSolomon` instance
    private ReedSolomon reedSolomon;

    // Creates a new `Coding` instance with the given number of shards.
    public Coding(Integer dataShardCount, Integer parityShardCount) {
        this.reedSolomon = ReedSolomon.create(dataShardCount, parityShardCount);
    }

    // Returns the number of data shards.
    public Integer getDataShardCount() {
        return this.reedSolomon.getDataShardCount();
    }

    // Returns the number of parity shards.
    public Integer getParityShardCount() {
        return this.reedSolomon.getParityShardCount();
    }

    /// Constructs (and overwrites) the parity shards.
    public void encode(byte[][] slices) {
        final int dataLength = slices[0].length;
        this.reedSolomon.encodeParity(slices, 0, dataLength);
    }

    public void reconstructShards(byte[][] shards) {
        // TODO
    }
}
