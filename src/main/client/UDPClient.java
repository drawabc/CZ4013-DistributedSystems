package main.client;

import java.net.*;
import java.util.Scanner;
import java.io.*;

public class UDPClient {
    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf;

    public UDPClient() {
        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName("localhost");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void reconnect() {
        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName("localhost");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String send(byte[] buf) {
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 8899);
        try {
            socket.send(packet);
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String received = new String(packet.getData(), 0, packet.getLength());
        return received;
    }

    public void close() {
        socket.close();
    }

    public static void main(String args[]) {
        UDPClient aClient = new UDPClient();
        Scanner sc = new Scanner(System.in);
        byte[] b = null;

        System.out.println("(1) Read file\t(2) Insert to file");

        int choice = Integer.parseInt(sc.nextLine());
        if (choice == 1) {
            b = ReadFile.promptUser(sc);
        } else if (choice == 2) {
            b = InsertToFile.promptUser(sc);
        } else {
            System.out.println("Wrong choice");
            System.exit(1);
        }

        System.out.println(aClient.send(b));

        aClient.close();
    }

}
