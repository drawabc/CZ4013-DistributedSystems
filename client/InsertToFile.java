package client;

import java.util.ArrayList;
import java.util.Scanner;

public class InsertToFile {
    static String filePath;

    // Parameters: file pathname, offset (bytes), content to insert
    public static byte[] promptUser(Scanner sc, int id) {
        System.out.println("Enter file pathname:");
        filePath = sc.nextLine();

        System.out.println("Enter character offset:");
        int offset = Integer.parseInt(sc.nextLine());

        System.out.println("Enter content to be inserted:");
        String content = sc.nextLine();

        return constructRequest(id, filePath, offset, content);
    }

    public static byte[] constructRequest(int id, String filePath, int offset, String content) {
        ArrayList<Byte> request = new ArrayList<Byte>();

        Utils.appendMsg(request, id);
        Utils.appendMsg(request, Constants.INSERTTOFILE_ID);
        Utils.appendMsgHeader(request, filePath);
        Utils.appendMsgHeader(request, offset);
        Utils.appendMsgHeader(request, content);

        return Utils.unwrapList(request);
    }

    public static void handleResponse(byte[] response, Cache cache) {
        int pointer = 0;
        String status = Utils.unmarshal(response, pointer, 1);
        pointer++;

        if (status.equals("1")) {
            long lastModified = Utils.unmarshalLong(response, pointer);
            pointer += Constants.LONG_SIZE;
            int length = Utils.unmarshal(response, pointer);
            pointer += Constants.INT_SIZE;
            String content = Utils.unmarshal(response, pointer, pointer + length);
            pointer += length;

            if (cache == null) {
                cache = new Cache(filePath);
                App.cacheMap.put(filePath, cache);
            }
            cache.updateCache(lastModified, content);

            System.out.println("Successfully editted file! New file contents:");
            System.out.println(cache.getFileContent());

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