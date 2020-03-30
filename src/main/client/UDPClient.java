package main.client;

import java.net.*;
import java.util.Arrays;

public class UDPClient {
    private DatagramSocket socket;
    private InetAddress address;
    private int reqID;
    public int semInv;
    public int socketTimeout;

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

    public int getSemInv(){
        return this.semInv;
    }

    public int changeSemInv(){
        this.semInv = this.semInv + 1 % 2;
        return this.semInv;
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

    public byte[] send(byte[] buf) {
        byte[] header = Utils.marshal(buf.length);
        DatagramPacket packet = new DatagramPacket(header, header.length, address, 8899);

        try {
            // Send packet header
            socket.send(packet);

            // send body/content
            packet = new DatagramPacket(buf, buf.length, address, 8899);
            socket.send(packet);

            return Arrays.copyOfRange(packet.getData(), 4, packet.getData().length);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];

    }

    public byte[] receive() {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    /*
        Combine send and receive
    */
    public byte[] requestReply(byte[] buf){
        byte[] reply = new byte[0];
        try {
            this.send(buf);
            reply = this.receive();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return reply;
    }

    public void close() {
        socket.close();
    }

    public int getID() {
        return ++this.reqID;
    }

}
