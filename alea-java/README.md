# Alea - Java

This repository contains a prototype implementation of **Alea-BFT** and **HoneybadgerBFT**.

> [!WARNING]
> **This is a research prototype/proof of concept.** It is not intended for production use.

## Prerequisites

Before compiling or running the code, ensure the following dependencies are installed and configured:

- `gf-complete` library
- `jerasure` library
- Herumi's `bls` library (specifically its FFI for Java)

## Compilation

Before starting the application, set up the key infrastructure by replacing N with the number of replicas in your system:

```bash
pushd replica
mvn exec:java -Dexec.args="-g N"
popd
```

A configuration file defining the parties involved is required. Use [this example](parties-example.json) as a reference for creating your own file.


To start the server instances, use the following command:

```bash
mvn exec:java -Dexec.args="batch protocol id N Nclients parties results"
```

To start the client instances, use the following command:
```bash
mvn exec:java -Dexec.args="load protocol id N parties results"
```
