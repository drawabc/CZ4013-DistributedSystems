package main.client;

import java.util.ArrayList;
import java.util.Scanner;

public class ReadFile {
    // Parameters: file pathname, offset (bytes), # of bytes to read
    public static byte[] promptUser(Scanner sc) {
        System.out.println("Enter file pathname:");
        String filePath = sc.nextLine();

        System.out.println("Enter how many characters you want to skip:");
        int offset = Integer.parseInt(sc.nextLine());

        System.out.println("How many characters you want to read:");
        int numBytes = Integer.parseInt(sc.nextLine());

        return constructRequest(filePath, offset, numBytes);
    }

    public static byte[] constructRequest(String filePath, int offset, int numBytes) {
        ArrayList<Byte> request = new ArrayList<Byte>();

        Utils.appendMsg(request, 1); // Type of service ID
        Utils.appendMsgHeader(request, filePath);
        Utils.appendMsgHeader(request, offset);
        Utils.appendMsgHeader(request, numBytes);

        return Utils.unwrapList(request);
    }

    public static void deconstructRequest(byte[] message) {
        System.out.println(message + " " + message.length);

        int pointer = 0;
        int length = Utils.unmarshal(message, pointer);
        pointer += 4;
        String filePath = Utils.unmarshal(message, pointer, pointer + length);
        pointer += length;

        length = Utils.unmarshal(message, pointer);
        pointer += 4;
        int offset = Utils.unmarshal(message, pointer);
        pointer += length;

        length = Utils.unmarshal(message, pointer);
        pointer += 4;
        int numBytes = Utils.unmarshal(message, pointer);

        System.out.println(String.format("%s %d %d", filePath, offset, numBytes));
    }
}