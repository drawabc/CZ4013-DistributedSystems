package client;

import java.util.ArrayList;
import java.util.Scanner;

public class ReadFile {
    static String filePath;
    static int offset;
    static int numBytes;

    // Parameters: file pathname, offset (bytes), # of bytes to read
    public static void promptUser(Scanner sc) {
        System.out.println("Enter file pathname:");
        filePath = sc.nextLine();

        System.out.println("Enter how many characters you want to skip:");
        offset = Integer.parseInt(sc.nextLine());

        System.out.println("How many characters you want to read:");
        numBytes = Integer.parseInt(sc.nextLine());
    }

    public static byte[] constructRequest(int id, String filePath) {
        ArrayList<Byte> request = new ArrayList<Byte>();

        Utils.appendMsg(request, id);
        Utils.appendMsg(request, Constants.READFILE_ID);
        Utils.appendMsgHeader(request, filePath);

        return Utils.unwrapList(request);
    }

    public static Cache handleResponse(byte[] response) {
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

            return cache;
        } else if (status.equals("0")) {
            int length = Utils.unmarshal(response, pointer);
            pointer += Constants.INT_SIZE;
            String message = Utils.unmarshal(response, pointer, pointer + length);
            System.out.println(message);
            return null;
        } else {
            System.out.println("Error: failed to parse response");
            return null;
        }
    }

}
