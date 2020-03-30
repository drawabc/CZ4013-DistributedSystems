package main.server;

import java.net.InetAddress;
import java.time.Clock;

class Watcher {
    private Clock clock = Clock.systemDefaultZone();
    private InetAddress address;
    private int port;
    private long interval;
    private long start;

    Watcher(InetAddress address, int port, long start, int interval) {
        this.address = address;
        this.port = port;
        this.start = start;
        this.interval = interval;
    }

    InetAddress getAddress() {
        return address;
    }

    int getPort() {
        return port;
    }

    long getDuration() {
        return clock.millis() - start;
    }

    long getRemainingDuration() {
        return interval - (clock.millis() - start);
    }

    boolean isAvailable() {
        return getDuration() < interval;
    }
}