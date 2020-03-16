package main.client;

import java.util.ArrayList;
import java.util.Scanner;

public class InsertToFile {
    // Parameters: file pathname, offset (bytes), content to insert
    public static byte[] promptUser(Scanner sc) {
        System.out.println("Enter file pathname:");
        String filePath = sc.nextLine();

        System.out.println("Enter character offset:");
        int offset = Integer.parseInt(sc.nextLine());

        System.out.println("Enter content to be inserted:");
        String content = sc.nextLine();

        return constructRequest(filePath, offset, content);
    }

    public static byte[] constructRequest(String filePath, int offset, String content) {
        ArrayList<Byte> request = new ArrayList<Byte>();

        Utils.appendMsg(request, 2); // Type of service ID
        Utils.appendMsgHeader(request, filePath);
        Utils.appendMsgHeader(request, offset);
        Utils.appendMsgHeader(request, content);

        return Utils.unwrapList(request);
    }
}