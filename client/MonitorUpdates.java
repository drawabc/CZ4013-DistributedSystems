package client;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Scanner;

public class MonitorUpdates {
    static String filePath;
    static int interval;
    static long start;
    static Clock clock = Clock.systemDefaultZone();

    // Parameters: file pathname, monitor interval
    public static byte[] promptUser(Scanner sc, int id) {
        System.out.println("Enter file pathname:");
        filePath = sc.nextLine();

        System.out.println("Enter monitor interval (in s):");
        interval = Integer.parseInt(sc.nextLine()) * 1000;

        return constructRequest(id);
    }

    public static byte[] constructRequest(int id) {
        ArrayList<Byte> request = new ArrayList<Byte>();

        Utils.appendMsg(request, id);
        Utils.appendMsg(request, Constants.MONITORFILE_ID);
        Utils.appendMsgHeader(request, filePath);
        Utils.appendMsgHeader(request, interval);

        return Utils.unwrapList(request);
    }

    public static byte[] constructEndRequest(int id) {
        ArrayList<Byte> request = new ArrayList<Byte>();

        Utils.appendMsg(request, id);
        Utils.appendMsg(request, Constants.MONITOREND_ID);
        Utils.appendMsgHeader(request, filePath);
        Utils.appendMsgHeader(request, interval);

        return Utils.unwrapList(request);
    }

    public static long getDuration(byte[] response) {
        int pointer = 0;
        String status = Utils.unmarshal(response, pointer, 1);
        pointer++;

        if (status.equals("1")) {
            int length = Utils.unmarshal(response, pointer);
            pointer += Constants.INT_SIZE;
            String message = Utils.unmarshal(response, pointer, pointer + length);
            pointer += length;

            return Long.valueOf(message);
        } else if (status.equals("0")) {
            int length = Utils.unmarshal(response, pointer);
            pointer += Constants.INT_SIZE;
            String message = Utils.unmarshal(response, pointer, pointer + length);
            pointer += length;
            System.out.println(message);
            return 0;
        } else {
            System.out.println("Error: failed to parse response");
            return 0;
        }
    }

    public static void handleResponse(byte[] response) {
        int pointer = 0;
        String status = "null";
        if (response!=null){
            status = Utils.unmarshal(response, pointer, 1);
            pointer++;
        }

        if (status.equals("1")) {
            long lastModified = Utils.unmarshalLong(response, pointer);
            pointer += Constants.LONG_SIZE;
            int length = Utils.unmarshal(response, pointer);
            pointer += Constants.INT_SIZE;
            String content = Utils.unmarshal(response, pointer, pointer + length);
            pointer += length;

            Cache cache = App.cacheMap.get(filePath);
            if (cache == null) {
                cache = new Cache(filePath);
                App.cacheMap.put(filePath, cache);
            }
            cache.updateCache(lastModified, content);

            System.out.println("File updated!");

        } else if (status.equals("0")) {
            int length = Utils.unmarshal(response, pointer);
            pointer += Constants.INT_SIZE;
            String message = Utils.unmarshal(response, pointer, pointer + length);
            System.out.println(message);
        } else {
            System.out.println("Error: failed to parse response");
        }
    }

    static void startMonitoring() {
        start = clock.millis();
    }

    static long getDuration() {
        return clock.millis() - start;
    }

    static boolean isAvailable() {
        return getDuration() < interval;
    }
}