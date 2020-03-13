package main.client;
import java.net.*;
import java.io.*;


public class UDPClient {
    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf;

    public UDPClient() {
        try{
            socket = new DatagramSocket();
            address = InetAddress.getByName("localhost");
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void reconnect(){
        try{
            socket = new DatagramSocket();
            address = InetAddress.getByName("localhost");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String send(byte[] buf) {
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, address, 8899);
        System.out.println("111213");
        try{
            socket.send(packet);
            System.out.println("1112133");
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
        } catch (Exception e){
            e.printStackTrace();
        }

        String received = new String(
                packet.getData(), 0, packet.getLength());
        return received;
    }

    public void close() {
        socket.close();
    }

    public static void main(String args[]){
        UDPClient aClient = new UDPClient();
        byte[] b = ("gaas").getBytes();
        System.out.println(aClient.send(b));
        aClient.close();
    }

}
