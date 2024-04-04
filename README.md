# Alea-BFT - Benchmark Service

## Overview
There are four roles to play by hosts: master, PCS (process creation service), replicas and clients.
The replicas run the BFT state-machine replication protocol.
PCS are the nodes responsible for creating the replicas. A PCS creates a replica in its owm machine (it's a mechanism
that allows multiple replicas to exist in the same machine).
Clients submit transactions to replicas.
The master sets up the experiments, coordinating all other players.

## Running
The players can be started in any order (the master can only start talking to remaining players after they're running).

### Benchmarking commands

- `pcs <id> <node>`: information about the pcs to spawn
- `replica <pcs>`: pcs that will spawn the replica
- `list`: Lists the PCSs and the replicas
- `topology <replicas_ids>`: sets the topology (meaning that it informs each replica and client about which replicas are participating in the current
benchmarking run)
- `protocol <protocol> <batch_size> <mode> <fault> <load>`: sets the protocol to use (can be HoneyBadgerBFT(`hb`), Dumbo1(`dumbo`), Dumbo2(`dumbo2`) or Alea-BFT(`alea`)), the batch size for the protocol, the metric to measure (can be `latency` or `throughput`), the fault mode (can be `free`, `crash` or `byzantine`) and the load the client sends to
replica (in txs/second)
- `start`: starts the experiments
- `stop`: stops the experiments
- `sleep <duration>`: sleeps for the specified duration (in automated scripts, it's better to sleep between commands)

## Result file name format

The file name has the form `<version>-<version-id>`.
The `<version-id>` is the version's convention for the id.

### Version 0.0

`<version>-<parcipantid>-<n>-<protocol>[-<batchsize>]-<mode>[-<load].csv`
