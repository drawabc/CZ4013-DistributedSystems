package server;

import client.Constants;

import java.io.File;
import java.net.InetAddress;
import java.time.Clock;
import java.util.ArrayList;
import java.util.HashMap;

public class HandleMonitor {
    private static Clock clock = Clock.systemDefaultZone();
    private static HashMap<String, ArrayList<Watcher>> map = new HashMap<String, ArrayList<Watcher>>();

    public static void addWatcher(Watcher watcher, String filePath) {
        ArrayList<Watcher> watchers = map.get(filePath);
        if (watchers == null) {
            watchers = new ArrayList<Watcher>();
        }
        watchers.add(watcher);
        map.put(filePath, watchers);
    }

    public static void handleRequest(UDPServer server, byte[] message, InetAddress address, int port) {
        int pointer = 0;
        int length = Utils.unmarshal(message, pointer);
        pointer += Constants.INT_SIZE;
        String filePath = Utils.unmarshal(message, pointer, pointer + length);
        pointer += length;

        length = Utils.unmarshal(message, pointer);
        pointer += Constants.INT_SIZE;
        int interval = Utils.unmarshal(message, pointer);

        System.out.println(String.format("%s %d", filePath, interval));

        String absfilePath = Constants.FILEPATH + filePath;
        if (new File(absfilePath).isFile()) {
            Watcher watcher = new Watcher(address, port, clock.millis(), interval);
            addWatcher(watcher, absfilePath);
            System.out.println("Added " + filePath + " watcher: " + address + ":" + port);

            byte[] response = createACK(server.getID(), "1", String.valueOf(watcher.getRemainingDuration()));
            server.send(response, Constants.MONITORFILE_ID, address, port);
        } else {
            String errorMsg = "An error occured. Cannot find file " + filePath + ".";
            byte[] response = createACK(server.getID(), "0", errorMsg);
            server.send(response, Constants.MONITORFILE_ID, address, port);
        }
    }

    public static void notify(UDPServer server, String filePath) {
        ArrayList<Watcher> watchers = map.get(filePath);
        if (watchers == null || watchers.size() == 0) {
            return;
        }

        ArrayList<Watcher> unavailableWatchers = new ArrayList<Watcher>();

        for (Watcher watcher : watchers) {
            if (watcher.isAvailable()) {
                System.out.println("Notifying: " + watcher.getAddress().toString() + ":" + watcher.getPort());
                byte[] response = createACK(server.getID(), "1", LastModified.getTimestamp(filePath),
                        HandleReadFile.readFile(filePath));
                server.send(response, Constants.MONITORFILE_ID, watcher.getAddress(), watcher.getPort());
            } else {
                unavailableWatchers.add(watcher);
            }
        }

        for (Watcher watcher : unavailableWatchers) {
            System.out.println("Stop notifying: " + watcher.getAddress().toString() + ":" + watcher.getPort());
            watchers.remove(watcher);
        }

        map.put(filePath, watchers);
    }

    public static byte[] createACK(int id, String status, String message) {
        ArrayList<Byte> response = new ArrayList<Byte>();

        Utils.appendMsg(response, id);
        Utils.appendMsg(response, status);
        Utils.appendMsgHeader(response, message);

        return Utils.unwrapList(response);
    }

    public static byte[] createACK(int id, String status, long time, String message) {
        ArrayList<Byte> response = new ArrayList<Byte>();

        Utils.appendMsg(response, id);
        Utils.appendMsg(response, status);
        Utils.appendMsg(response, time);
        Utils.appendMsgHeader(response, message);

        return Utils.unwrapList(response);
    }
}