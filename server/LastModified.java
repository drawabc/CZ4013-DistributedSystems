package server;

import java.net.InetAddress;
import java.time.Clock;
import java.util.ArrayList;
import java.util.HashMap;

import client.Constants;

public class LastModified {
    static HashMap<String, Long> fileTimestampMap = new HashMap<String, Long>();

    public static void update(String filePath) {
        fileTimestampMap.put(filePath, Clock.systemDefaultZone().millis());
    }

    public static long getTimestamp(String filePath) {
        if (fileTimestampMap.get(filePath) == null) {
            update(filePath);
        }
        return fileTimestampMap.get(filePath);
    }

    public static void handleRequest(UDPServer server, byte[] message, InetAddress address, int port) {
        int pointer = 0;
        int length = Utils.unmarshal(message, pointer);
        pointer += Constants.INT_SIZE;
        String filePath = Utils.unmarshal(message, pointer, pointer + length);
        Long lastModified = fileTimestampMap.get(filePath);
        if (lastModified == null) {
            update(filePath);
            lastModified = fileTimestampMap.get(filePath);
        }
        System.out.println(String.format("Checking last modified timestamp of file: %s %d", filePath, lastModified));

        byte[] response = createACK(server.getID(), "1", fileTimestampMap.get(filePath));
        server.send(response, Constants.CHECKCACHE_ID, address, port);
    }

    public static byte[] createACK(int id, String status, long time) {
        ArrayList<Byte> response = new ArrayList<Byte>();

        Utils.appendMsg(response, id);
        Utils.appendMsg(response, status);
        Utils.appendMsg(response, time);

        return Utils.unwrapList(response);
    }
}