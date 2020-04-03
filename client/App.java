package client;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Scanner;

public class App {
    static long refreshRate;
    static HashMap<String, Cache> cacheMap = new HashMap<String, Cache>();
    static UDPClient udpclient = new UDPClient();

    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        byte[] b = null;

        System.out.println("Please enter the refresh rate for cache:");
        App.refreshRate = Integer.parseInt(sc.nextLine()) * 1000;

        while (true) {
            System.out.println(
                    "\n(1) Read file\t(2) Insert to file\t(3) Monitor file updates\t(4) Delete characters in file\t(0) Exit");

            int choice = Integer.parseInt(sc.nextLine());

            byte[] response = null;

            switch (choice) {
                case 0:
                    udpclient.close();
                    System.exit(0);
                    break;
                case 1:
                    ReadFile.promptUser(sc);
                    break;
                case 2:
                    InsertToFile.promptUser(sc);
                    break;
                case 3:
                    b = MonitorUpdates.promptUser(sc, udpclient.getID());
                    try {
                        response = udpclient.requestReply(b);
                        Long duration = MonitorUpdates.getDuration(response);
                        System.out.println("Monitoring for " + duration / 1000 + " s");
                        udpclient.setTimeout(duration.intValue());
                        // TODO: fix or add timer (?)
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
                    DeleteInFile.promptUser(sc);
                    break;
                default:
                    System.out.println("Wrong choice");
                    break;
            }
        }

    }
}
