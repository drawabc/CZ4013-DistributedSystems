package main.client;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class UDPClient {
    private DatagramSocket socket;
    private InetAddress address;
    private int reqID;
    public int semInv;

    public UDPClient() {
        this.reqID = 0;
        this.semInv = Constants.DEFAULT_SEMANTIC_INVOCATION;
        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName("localhost");
            socket.setSoTimeout(Constants.DEFAULT_TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int getSemInv() {
        return this.semInv;
    }

    public int changeSemInv() {
        this.semInv = this.semInv + 1 % 2;
        return this.semInv;
    }

    public void setTimeout(int timeout) throws SocketException {
        socket.setSoTimeout(timeout);
    }

    public void reconnect() {
        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName("localhost");
            socket.setSoTimeout(Constants.DEFAULT_TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] send(byte[] buf) throws IOException {
        byte[] header = Utils.marshal(buf.length);
        DatagramPacket packet = new DatagramPacket(header, header.length, address, 8899);

        // Send packet header
        socket.send(packet);

        // send body/content
        packet = new DatagramPacket(buf, buf.length, address, 8899);
        socket.send(packet);
        return Arrays.copyOfRange(packet.getData(), 4, packet.getData().length);


    }

    public byte[] receive() throws SocketTimeoutException, IOException {
        // Receive packet header & adjust buffer
        byte[] buf = new byte[4];
        DatagramPacket packet = new DatagramPacket(buf, 4);
        socket.receive(packet);
        int bufsize = Utils.unmarshal(packet.getData(), 0);
        buf = new byte[bufsize];

        // Receive packet content
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        int reqID = Utils.unmarshal(packet.getData(), 0);

        return Arrays.copyOfRange(packet.getData(), 4, packet.getData().length);
    }

    /*
        Send and receive, with all settings
    */
    public byte[] requestReply(byte[] buf) {
        byte[] reply = new byte[0];
        int timeoutCount = 0;
        do {
            try {
                this.send(buf);
                reply = this.receive();
                break;
            } catch (SocketTimeoutException e) {
                System.out.println("No Response, Re-send Request");
                timeoutCount++;
                if (timeoutCount == 5) {
                    System.out.println("Request Failed");
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        } while (this.semInv == Constants.AT_LEAST_ONCE || this.semInv == Constants.AT_MOST_ONCE);
        return reply;
    }
    public void close() {
        socket.close();
    }

    public int getID() {
        return ++this.reqID;
    }

}
