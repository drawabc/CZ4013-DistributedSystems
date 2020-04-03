package server;

import client.Constants;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class HandleCountChar {
    public static void handleRequest(UDPServer server, byte[] message, InetAddress address, int port) {
        int pointer = 0;
        int length = Utils.unmarshal(message, pointer);
        pointer += 4;
        String filePath = Utils.unmarshal(message, pointer, pointer + length);
        pointer += length;

        length = Utils.unmarshal(message, pointer);
        pointer += 4;
        String selectedString = Utils.unmarshal(message, pointer, pointer + length);
        char selected = selectedString.charAt(0);
        pointer += length;


        System.out.println(String.format("Count Character: %s %s", filePath, selected));

        try {
            byte[] response = createACK(server.getID(), "1", countChar(filePath, selected));
            server.send(response, 5, address, port);
        } catch (IOException e) {
            System.out.println(e);
            String errorMsg = "An error occured. Either the filename is incorrect or the input exceeds the 1";
            byte[] response = createACK(server.getID(), "0", errorMsg);
            server.send(response, 5, address, port);
        }
    }

    public static String countChar(String filePath, char selected) throws IOException {
        filePath = Constants.FILEPATH + filePath;
        RandomAccessFile aFile = new RandomAccessFile(filePath, "r");
        FileChannel inChannel = aFile.getChannel();
        MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0,
                inChannel.size());
        byte[] byteData = new byte[buffer.capacity()];
        buffer.get(byteData, 0, buffer.capacity());
        String data = new String(byteData, StandardCharsets.UTF_8);
        int counter = 0;
        for (int i = 0; i< data.length(); i++){
            if (data.charAt(i) == selected){
                counter++;
            }
        }
        System.out.println("There are " + counter +  " character(s)");

        buffer.clear(); // do something with the data and clear/compact it.
        inChannel.close();
        aFile.close();

        return Integer.toString(counter);
    }

    public static byte[] createACK(int id, String status, String message) {
        ArrayList<Byte> response = new ArrayList<Byte>();

        Utils.appendMsg(response, id);
        Utils.appendMsg(response, status);
        Utils.appendMsgHeader(response, message);

        return Utils.unwrapList(response);
    }
}