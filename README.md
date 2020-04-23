# CZ4013-DistributedSystems

This project implements a client-server distributed system which allows sharing and modifying a text (.txt) file stored in the server remotely through distributed clients. This project also implements UDP transport protocol for client-server communication.

## Environment

Tested in these environments:

- Windows 10 and Java 13
- Linux and Java 8

## How to run

### Client

```
javac client/*.java
java client.App
```

### Server

```
javac server/*.java
java server.UDPServer
```

The `txt` files must be put in `server/data` directory.

### Change Invocation Semantics
Modify Constants.java DEFAULT_SEMANTIC_INVOCATION = AT_MOST_ONCE or AT_LEAST_ONCE

### Change Packet Loss Rate
Modify Constants.java PACKET_LOSS_RATE (1.0 means no loss, 0.0 means always loss)
