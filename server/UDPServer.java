package server;

import client.Constants; //TODO: need to make Server's own constants

import java.net.*;
import java.util.Arrays;
import java.util.HashMap;

public class UDPServer {
    public boolean receiving;
    public DatagramSocket socket;
    private HashMap<String, byte[]> clientHistory;
    private int resID;
    public int semInv;

    public UDPServer() {
        resID = 0;
        clientHistory = new HashMap<String, byte[]>();
        try {
            socket = new DatagramSocket(8899);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.semInv = Constants.DEFAULT_SEMANTIC_INVOCATION;
    }

    public int getID() {
        return ++this.resID;
    }

    public void handleClientRequest(int serviceID, byte[] data, InetAddress address, int port) {
        if (serviceID == 1) {
            HandleReadFile.handleRequest(this, data, address, port);
        } else if (serviceID == 2) {
            HandleInsertToFile.handleRequest(this, data, address, port);
        } else if (serviceID == 3) {
            HandleMonitor.handleRequest(this, data, address, port);
        } else if (serviceID == 4) {
            HandleDeleteInFile.handleRequest(this, data, address, port);
        } else {
            System.out.println("Error: serviceID is invalid.");
            String errorMsg = "Requested service is invalid.";
            byte[] response = HandleReadFile.createACK(this.getID(), "0", errorMsg);
            this.send(response, 2, address, port);
        }
    }

    // TODO: replace History
    public byte[] checkHistory(String address, int requestID) {
        String hashKey = address + "|" + requestID;
        return clientHistory.get(hashKey);
    }

    public void updateHistory(String address, int requestID, byte[] response) {
        String hashKey = address + "|" + requestID;
        clientHistory.put(hashKey, response);
    }

    public void send(byte[] response, int requestID, InetAddress address, int port) {
        try {
            if (socket != null) {
                byte[] header = Utils.marshal(response.length);
                socket.send(new DatagramPacket(header, header.length, address, port));
                socket.send(new DatagramPacket(response, response.length, address, port));
                String fullAddress = address.toString() + "|" + port;
                updateHistory(fullAddress, requestID, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (socket != null) {
                // Receive packet header & adjust buffer size
                byte[] buffer = new byte[4];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);
                int buffersize = Utils.unmarshal(request.getData(), 0);

                buffer = new byte[buffersize];
                request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                String fullAddress = request.getAddress().toString() + ":" + request.getPort();
                int requestID = Utils.unmarshal(request.getData(), 0);
                System.out.println("Got request from " + fullAddress + " with ID: " + requestID);

                byte[] response = checkHistory(fullAddress, requestID);
                if (response != null && this.semInv == Constants.AT_MOST_ONCE) {
                    int serviceID = Utils.unmarshal(request.getData(), 4);
                    byte[] requestContent = response;
                    this.send(response, serviceID, request.getAddress(), request.getPort());
                } else {
                    int serviceID = Utils.unmarshal(request.getData(), 4);
                    byte[] requestContent = Arrays.copyOfRange(request.getData(), 8, request.getData().length);
                    handleClientRequest(serviceID, requestContent, request.getAddress(), request.getPort());
                }

                // DatagramPacket reply = new DatagramPacket(response, response.length,
                // request.getAddress(),
                // request.getPort());

                // // Send packet header that contains the content size
                // byte[] header = Utils.marshal(response.length);
                // socket.send(new DatagramPacket(header, header.length, request.getAddress(),
                // request.getPort()));
                // socket.send(reply);
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
