package main.client;

import java.util.ArrayList;
import java.util.Scanner;

public class ReadFile {
    // Parameters: file pathname, offset (bytes), # of bytes to read
    public static byte[] promptUser(Scanner sc, int id) {
        System.out.println("Enter file pathname:");
        String filePath = sc.nextLine();

        System.out.println("Enter how many characters you want to skip:");
        int offset = Integer.parseInt(sc.nextLine());

        System.out.println("How many characters you want to read:");
        int numBytes = Integer.parseInt(sc.nextLine());

        return constructRequest(id, filePath, offset, numBytes);
    }

    public static byte[] constructRequest(int id, String filePath, int offset, int numBytes) {
        ArrayList<Byte> request = new ArrayList<Byte>();

        Utils.appendMsg(request, id);
        Utils.appendMsg(request, 1); // Type of service ID
        Utils.appendMsgHeader(request, filePath);
        Utils.appendMsgHeader(request, offset);
        Utils.appendMsgHeader(request, numBytes);

        return Utils.unwrapList(request);
    }

    public static void handleResponse(byte[] response) {
        int pointer = 0;
        String status = Utils.unmarshal(response, pointer, 1);
        pointer++;

        if (status.equals("1")) {
            int length = Utils.unmarshal(response, pointer);
            pointer += 4;
            String message = Utils.unmarshal(response, pointer, pointer + length);
            pointer += length;
            System.out.println(message);
        } else if (status.equals("0")) {
            int length = Utils.unmarshal(response, pointer);
            pointer += 4;
            String message = Utils.unmarshal(response, pointer, pointer + length);
            pointer += length;
            System.out.println(message);
        } else {
            System.out.println("Error: failed to parse response");
        }
    }
}