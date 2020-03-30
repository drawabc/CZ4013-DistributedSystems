package main.server;
import javax.xml.crypto.Data;
import java.net.*;
import java.io.*;


public class UDPServer {
    public boolean receiving;
    public DatagramSocket socket;
    public UDPServer(){
        try{
            socket = new DatagramSocket(8899);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void run(){
        try{
            byte[] buffer = new byte[1000];
            while(socket != null){
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                // TODO: implement marshalling/un-marshalling
                // TODO: timeout ack, etc
                System.out.println("AX");
                socket.receive(request);
                System.out.println("ASDFASDA");
                DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(), request.getPort());
                socket.send(reply);
            }

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String args[]){
        UDPServer aServer = new UDPServer();
        aServer.run();
    }
}
