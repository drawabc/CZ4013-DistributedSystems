package client;

import java.util.ArrayList;
import java.util.Scanner;

public class DeleteInFile {
    static String filePath;

    // Parameters: file pathname, offset (bytes), # of bytes to delete
    public static byte[] promptUser(Scanner sc, int id) {
        System.out.println("Enter file pathname:");
        filePath = sc.nextLine();

        System.out.println("Enter character offset:");
        int offset = Integer.parseInt(sc.nextLine());

        System.out.println("How many characters you want to delete:");
        int numBytes = Integer.parseInt(sc.nextLine());

        return constructRequest(id, filePath, offset, numBytes);
    }

    public static byte[] constructRequest(int id, String filePath, int offset, int numBytes) {
        ArrayList<Byte> request = new ArrayList<Byte>();

        Utils.appendMsg(request, id);
        Utils.appendMsg(request, Constants.DELETEINFILE_ID);
        Utils.appendMsgHeader(request, filePath);
        Utils.appendMsgHeader(request, offset);
        Utils.appendMsgHeader(request, numBytes);

        return Utils.unwrapList(request);
    }

    public static void handleResponse(byte[] response) {
        int pointer = 0;
        String status = "null";
        System.out.println(response);
        if (response != null){
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