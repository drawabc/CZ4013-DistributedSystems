package server;

import java.net.*;
import java.util.Arrays;
import java.util.HashMap;

public class UDPServer {
    public boolean receiving;
    public DatagramSocket socket;
    private HashMap<String, byte[]> clientHistory;
    private int resID;
    public int semInv;
    private int curReqID;

    public UDPServer() {
        resID = 0;
        clientHistory = new HashMap<String, byte[]>();
        try {
            socket = new DatagramSocket(Constants.DEFAULT_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.semInv = Constants.DEFAULT_SEMANTIC_INVOCATION;
    }

    public int getID() {
        return ++this.resID;
    }

    public void handleClientRequest(int serviceID, byte[] data, InetAddress address, int port) {
        if (serviceID == Constants.READFILE_ID) {
            HandleReadFile.handleRequest(this, data, address, port);
        } else if (serviceID == Constants.INSERTTOFILE_ID) {
            HandleInsertToFile.handleRequest(this, data, address, port);
        } else if (serviceID == Constants.MONITORFILE_ID) {
            HandleMonitor.handleRequest(this, data, address, port);
        } else if (serviceID == Constants.MONITOREND_ID) {
            HandleMonitor.handleEndRequest(this, data, address, port);
        } else if (serviceID == Constants.DELETEINFILE_ID) {
            HandleDeleteInFile.handleRequest(this, data, address, port);
        } else if (serviceID == Constants.CHECKCACHE_ID) {
            LastModified.handleRequest(this, data, address, port);
        } else if (serviceID == Constants.COUNTCHAR_ID) {
            HandleCountChar.handleRequest(this, data, address, port);
        } else {
            System.out.println("Error: serviceID is invalid.");
            String errorMsg = "Requested service is invalid.";
            byte[] response = HandleInsertToFile.createNAK(this.getID(), "0", errorMsg);
            this.send(response, 2, address, port);
        }
    }

    public byte[] checkHistory(String address, int requestID) {
        String hashKey = address + "|" + requestID;
        return clientHistory.get(hashKey);
    }

    public void updateHistory(String address, int requestID, byte[] response) {
        String hashKey = address + "|" + requestID;
        clientHistory.put(hashKey, response);
    }

    public void send(byte[] response, int requestID, InetAddress address, int port) {
        String fullAddress = address.toString() + "|" + port;
        updateHistory(fullAddress, this.curReqID, response);
        if (Math.random() >= Constants.PACKET_LOSS_RATE){
            System.out.println("Packet Lost, Send Failed");
            return;
        }
        try {
            if (socket != null) {
                byte[] header = Utils.marshal(response.length);
                socket.send(new DatagramPacket(header, header.length, address, port));
                socket.send(new DatagramPacket(response, response.length, address, port));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (socket != null) {
                // Receive packet header & adjust buffer size
                byte[] buffer = new byte[Constants.INT_SIZE];
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
                this.curReqID = requestID;
                if (response != null && this.semInv == Constants.AT_MOST_ONCE) {
                    System.out.println("Using at most once semantics, sending from history");
                    int serviceID = Utils.unmarshal(request.getData(), Constants.INT_SIZE);
                    byte[] requestContent = response;
                    this.send(response, serviceID, request.getAddress(), request.getPort());
                } else {
                    int serviceID = Utils.unmarshal(request.getData(), Constants.INT_SIZE);
                    byte[] requestContent = Arrays.copyOfRange(request.getData(), Constants.INT_SIZE * 2,
                            request.getData().length);
                    handleClientRequest(serviceID, requestContent, request.getAddress(), request.getPort());
                }
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
