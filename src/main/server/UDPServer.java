package main.server;
import java.net.*;
import java.io.*;


public class UDPServer {
    public boolean receiving;
    public UDPServer(){
    }

    public void run(){
        try{
            byte[] buffer = new byte[1000];
            while(true){
                DatagramSocket socket = null;
                System.out.println("ASDF");
                socket = new DatagramSocket(8899);
                System.out.println("ASDU");
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
