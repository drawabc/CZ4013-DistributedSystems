package main.client;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class App {
    public static void main(String args[]) {
        UDPClient udpclient = new UDPClient();
        Scanner sc = new Scanner(System.in);
        byte[] b = null;

        while (true) {
            System.out.println("\n(1) Read file\t(2) Insert to file\t(3) Monitor file updates\t(0) Exit");

            int choice = Integer.parseInt(sc.nextLine());

            byte[] response = null;

            switch (choice) {
                case 0:
                    udpclient.close();
                    System.exit(0);
                    break;
                case 1:
                    b = ReadFile.promptUser(sc, udpclient.getID());
                    response = udpclient.send(b);
                    ReadFile.handleResponse(response);
                    break;
                case 2:
                    b = InsertToFile.promptUser(sc, udpclient.getID());
                    response = udpclient.send(b);
                    InsertToFile.handleResponse(response);
                    break;
                case 3:
                    b = MonitorUpdates.promptUser(sc, udpclient.getID());
                    response = udpclient.send(b);
                    Long duration = MonitorUpdates.getDuration(response);
                    System.out.println("Monitoring for " + duration / 1000 + " s");

                    try {
                        udpclient.setTimeout(duration.intValue());
                        while (true) {
                            try {
                                MonitorUpdates.handleResponse(udpclient.receive());
                            } catch (SocketTimeoutException e) {
                                System.out.println("Timeout reached.");
                                udpclient.setTimeout(0);
                                break;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (SocketException e) {
                        System.out.println("");
                    }

                    break;
                // while (MonitorUpdates.isMonitoring()) { // TODO: implement proper timeout
                // MonitorUpdates.handleResponse(udpclient.receive());
                // }
                default:
                    System.out.println("Wrong choice");
                    break;
            }
        }

    }
}