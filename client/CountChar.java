package client;

import java.util.ArrayList;
import java.util.Scanner;

public class CountChar {
    static String filePath;
    static char selectedChar;

    // Parameters: file pathname, offset (bytes), # of bytes to read
    public static void promptUser(Scanner sc) {
        System.out.println("Enter file pathname:");
        filePath = sc.nextLine();

        System.out.println("Enter the character you want to count");
        String s = sc.nextLine();
        selectedChar = s.charAt(0);

        // return constructRequest(id, filePath, selected);
    }

    public static byte[] constructRequest(int id) {
        ArrayList<Byte> request = new ArrayList<Byte>();

        String selected = Character.toString(selectedChar);

        Utils.appendMsg(request, id);
        Utils.appendMsg(request, Constants.COUNTCHAR_ID);
        Utils.appendMsgHeader(request, filePath);
        Utils.appendMsgHeader(request, selected);

        return Utils.unwrapList(request);
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