package client;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Scanner;

public class App {
    static HashMap<String, Cache> cacheMap = new HashMap<String, Cache>();
    static UDPClient udpclient = new UDPClient();

    public static String fetchCacheContent(Cache cache, int offset, int numBytes) {
        if (cache == null) {
            System.out.println("Cache missed! Creating new cache..");
            return null;
        } else {
            if (cache.isLocallyValid()) {
                System.out.println("Cache (local) hit!");
            } else {
                byte[] b = cache.constructRequest(udpclient.getID());
                byte[] response = udpclient.requestReply(b);
                if (cache.isRemotelyValid(response)) {
                    System.out.println("Cache (remote) hit!");
                } else {
                    System.out.println("Cache missed! Updating cache...");
                    return null;
                }
            }
        }
        return cache.getFileContent(offset, numBytes);
    }

    public static int fetchCacheChar(Cache cache, char character) {
        if (cache == null) {
            return -1;
        } else {
            if (cache.isLocallyValid()) {
                System.out.println("Cache (local) hit!");
            } else {
                byte[] b = cache.constructRequest(udpclient.getID());
                byte[] response = udpclient.requestReply(b);
                if (cache.isRemotelyValid(response)) {
                    System.out.println("Cache (remote) hit!");
                } else {
                    System.out.println("Cache missed!");
                    return -1;
                }
            }
        }
        return cache.countChar(character);
    }

    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        byte[] b = null;
        Cache cache;

        System.out.println("Please enter the refresh rate for cache:");
        Constants.REFRESH_INTERVAL = Integer.parseInt(sc.nextLine()) * 1000;

        while (true) {
            System.out.println(
                    "\n(1) Read file\t(2) Insert to file\t(3) Monitor file updates\t(4) Delete characters in file\t(5)Count Characters\t(0) Exit");

            int choice = Integer.parseInt(sc.nextLine());

            byte[] response = null;

            switch (choice) {
                case 0:
                    udpclient.close();
                    System.exit(0);
                    break;
                case 1:
                    ReadFile.promptUser(sc);
                    cache = cacheMap.get(ReadFile.filePath);
                    String cacheContent = fetchCacheContent(cache, ReadFile.offset, ReadFile.numBytes);
                    if (cacheContent == null) {
                        b = ReadFile.constructRequest(udpclient.getID(), ReadFile.filePath);
                        response = udpclient.requestReply(b);
                        cache = ReadFile.handleResponse(response);
                        if (cache != null) {
                            cacheContent = fetchCacheContent(cache, ReadFile.offset, ReadFile.numBytes);
                        } else
                            break;
                    }
                    System.out.println(cacheContent);
                    break;
                case 2:
                    b = InsertToFile.promptUser(sc, udpclient.getID());
                    response = udpclient.requestReply(b);
                    cache = cacheMap.get(InsertToFile.filePath);
                    InsertToFile.handleResponse(response);
                    break;
                case 3:
                    b = MonitorUpdates.promptUser(sc, udpclient.getID());
                    try {
                        response = udpclient.requestReply(b);
                        Long duration = MonitorUpdates.getDuration(response);
                        System.out.println("Monitoring for " + duration / 1000 + " s");
                        udpclient.setTimeout(duration.intValue());
                        while (true) {
                            try {
                                MonitorUpdates.handleResponse(udpclient.receive());
                            } catch (SocketTimeoutException e) {
                                System.out.println("Timeout reached.");
                                udpclient.setTimeout(Constants.DEFAULT_TIMEOUT);
                                break;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("");
                        e.printStackTrace();
                    }

                    break;
                case 4:
                    b = DeleteInFile.promptUser(sc, udpclient.getID());
                    response = udpclient.requestReply(b);
                    cache = cacheMap.get(DeleteInFile.filePath);
                    DeleteInFile.handleResponse(response);
                    break;

                case 5:
                    CountChar.promptUser(sc);
                    cache = cacheMap.get(ReadFile.filePath);
                    int countedChars = fetchCacheChar(cache, CountChar.selectedChar);
                    if (countedChars == -1) {
                        b = ReadFile.constructRequest(udpclient.getID(), CountChar.filePath);
                        response = udpclient.requestReply(b);
                        cache = ReadFile.handleResponse(response);
                        if (cache != null) {
                            countedChars = fetchCacheChar(cache, CountChar.selectedChar);
                        } else
                            break;
                    }
                    System.out.println("There are " + countedChars + " character(s).");
                    break;
                default:
                    System.out.println("Wrong choice");
                    break;
            }
        }

    }
}
