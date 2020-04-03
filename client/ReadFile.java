package client;

import java.util.ArrayList;
import java.util.Scanner;

public class ReadFile {
    // Parameters: file pathname, offset (bytes), # of bytes to read
    public static void promptUser(Scanner sc) {
        System.out.println("Enter file pathname:");
        String filePath = sc.nextLine();

        System.out.println("Enter how many characters you want to skip:");
        int offset = Integer.parseInt(sc.nextLine());

        System.out.println("How many characters you want to read:");
        int numBytes = Integer.parseInt(sc.nextLine());

        fetchContent(filePath, offset, numBytes);
    }

    public static void fetchContent(String filePath, int offset, int numBytes) {
        Cache cache = App.cacheMap.get(filePath);
        if (cache == null) {
            byte[] response = App.udpclient.requestReply(constructRequest(filePath));
            if (handleResponse(response, filePath, cache) != 1)
                return;
            cache = App.cacheMap.get(filePath);
        } else {
            if (cache.isLocallyValid()) {
                System.out.println("Cache (local) hit!");
            } else if (cache.isRemotelyValid()) {
                System.out.println("Cache (remote) hit!");
            } else {
                System.out.println("Cache missed!");
                byte[] response = App.udpclient.requestReply(constructRequest(filePath));
                handleResponse(response, filePath, cache);
            }
        }
        System.out.println(cache.getFileContent(offset, numBytes));
    }

    public static byte[] constructRequest(String filePath) {
        ArrayList<Byte> request = new ArrayList<Byte>();

        Utils.appendMsg(request, App.udpclient.getID());
        Utils.appendMsg(request, Constants.READFILE_ID);
        Utils.appendMsgHeader(request, filePath);

        return Utils.unwrapList(request);
    }

    public static int handleResponse(byte[] response, String filePath, Cache cache) {
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

            return 1;
        } else if (status.equals("0")) {
            int length = Utils.unmarshal(response, pointer);
            pointer += Constants.INT_SIZE;
            String message = Utils.unmarshal(response, pointer, pointer + length);
            System.out.println(message);
            return 0;
        } else {
            System.out.println("Error: failed to parse response");
            return -1;
        }
    }

}
