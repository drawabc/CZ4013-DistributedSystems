package client;

import java.util.ArrayList;
import java.util.Scanner;

public class MonitorUpdates {
    static String filePath;
    static int interval;

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
        String status = Utils.unmarshal(response, pointer, 1);
        pointer++;

        if (status.equals("1")) {
            int length = Utils.unmarshal(response, pointer);
            pointer += Constants.INT_SIZE;
            String message = Utils.unmarshal(response, pointer, pointer + length);
            pointer += length;
            System.out.println(message);

        } else if (status.equals("0")) {
            int length = Utils.unmarshal(response, pointer);
            pointer += Constants.INT_SIZE;
            String message = Utils.unmarshal(response, pointer, pointer + length);
            pointer += length;
            System.out.println(message);
        } else {
            System.out.println("Error: failed to parse response");
        }
    }
}