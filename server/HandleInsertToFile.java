package server;

import client.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class HandleInsertToFile {
    public static void handleRequest(UDPServer server, byte[] message, InetAddress address, int port) {
        int pointer = 0;
        int length = Utils.unmarshal(message, pointer);
        pointer += Constants.INT_SIZE;
        String filePath = Utils.unmarshal(message, pointer, pointer + length);
        pointer += length;

        length = Utils.unmarshal(message, pointer);
        pointer += Constants.INT_SIZE;
        int offset = Utils.unmarshal(message, pointer);
        pointer += length;

        length = Utils.unmarshal(message, pointer);
        pointer += Constants.INT_SIZE;
        String content = Utils.unmarshal(message, pointer, pointer + length);

        System.out.println(String.format("Insert to file: %s %d %s", filePath, offset, content));

        try {
            byte[] response = createACK(server.getID(), "1", insertToFile(filePath, offset, content));
            server.send(response, Constants.INSERTTOFILE_ID, address, port);
            String notification = address.toString() + ":" + port + " editted " + filePath;
            HandleMonitor.notify(server, Constants.FILEPATH + filePath, notification);
        } catch (IOException e) {
            System.out.println(e);
            String errorMsg = "An error occured. Either the filename is incorrect or the offset exceeds the length";
            byte[] response = createACK(server.getID(), "0", errorMsg);
            server.send(response, Constants.INSERTTOFILE_ID, address, port);
        }

    }

    public static String insertToFile(String filePath, int offset, String content) throws IOException {
        // Read file
        filePath = Constants.FILEPATH + filePath;
        RandomAccessFile aFile = new RandomAccessFile(filePath, "rw");
        FileChannel inChannel = aFile.getChannel();
        MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());
        byte[] beforeOffset = new byte[offset];
        buffer.get(beforeOffset, 0, offset);

        byte[] afterOffset = new byte[buffer.remaining()];
        buffer.get(afterOffset);

        // do something with the data and clear/compact it.

        System.out.println("Before offset:");
        for (int i = 0; i < beforeOffset.length; i++) {
            System.out.print((char) beforeOffset[i]);
        }

        System.out.println("\nAfter offset:");
        for (int i = 0; i < afterOffset.length; i++) {
            System.out.print((char) afterOffset[i]);
        }

        // FileOutputStream outputStream = new FileOutputStream(filePath);
        aFile.write(beforeOffset);

        // Insert contents to file
        aFile.write(content.getBytes());

        // Rewrite rest of the file
        aFile.write(afterOffset);
        // outputStream.close();

        // Rewrite file up to offset
        buffer.clear();
        inChannel.close();
        aFile.close();

        return "Successfully inserted to file " + filePath;
    }

    public static byte[] createACK(int id, String status, String message) {
        ArrayList<Byte> response = new ArrayList<Byte>();

        Utils.appendMsg(response, id);
        Utils.appendMsg(response, status);
        Utils.appendMsgHeader(response, message);

        return Utils.unwrapList(response);
    }
}