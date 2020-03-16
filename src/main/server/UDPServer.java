package main.server;

import java.net.*;
import java.util.Arrays;
import java.io.*;

public class UDPServer {
    public boolean receiving;
    public DatagramSocket socket;

    public UDPServer() {
        try {
            socket = new DatagramSocket(8899);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void run() {
        try {
            while (socket != null) {
                byte[] buffer = new byte[512];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                // TODO: timeout ack, etc
                socket.receive(request);

                // Handle client request here
                int serviceID = Utils.unmarshal(request.getData(), 0);

                DatagramPacket reply = null;

                if (serviceID == 1) {
                    byte[] response = HandleReadFile
                            .handleRequest(Arrays.copyOfRange(request.getData(), 4, request.getData().length));
                    reply = new DatagramPacket(response, response.length, request.getAddress(), request.getPort());
                } else if (serviceID == 2) {
                    byte[] response = HandleInsertToFile
                            .handleRequest(Arrays.copyOfRange(request.getData(), 4, request.getData().length));
                    reply = new DatagramPacket(response, response.length, request.getAddress(), request.getPort());
                } else {
                    reply = new DatagramPacket("error".getBytes(), 5, request.getAddress(), request.getPort());
                }

                socket.send(reply);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        UDPServer aServer = new UDPServer();
        aServer.run();

    }
}
