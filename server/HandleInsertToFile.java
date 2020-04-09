package server;

import client.Constants;

import java.io.FileNotFoundException;
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

        String fileContent = insertToFile(filePath, offset, content);
        String errorMsg;
        if (fileContent.equals("FileNotFound")) {
            errorMsg = "An error occured. The file " + filePath + " does not exist.";
            byte[] response = createACK(server.getID(), "0", errorMsg);
            server.send(response, Constants.INSERTTOFILE_ID, address, port);
        } else if (fileContent.equals("IOException")) {
            errorMsg = "An error occured. Maybe the offset is too large?";
            byte[] response = createACK(server.getID(), "0", errorMsg);
            server.send(response, Constants.INSERTTOFILE_ID, address, port);
        } else {
            byte[] response = createACK(server.getID(), "1", fileContent);
            server.send(response, Constants.INSERTTOFILE_ID, address, port);
            String notification = address.toString() + ":" + port + " editted " + filePath;
            HandleMonitor.notify(server, Constants.FILEPATH + filePath, notification);
        }
    }

    public static String insertToFile(String filePath, int offset, String content) {
        // Read file
        filePath = Constants.FILEPATH + filePath;
        RandomAccessFile aFile;
        try {
            aFile = new RandomAccessFile(filePath, "rw");
        } catch (FileNotFoundException e) {
            return "FileNotFound";
        }
        FileChannel inChannel = aFile.getChannel();
        MappedByteBuffer buffer;
        try {
            buffer = inChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());
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
        } catch (IOException e) {
            return "IOException";
        }

    }

    public static byte[] createACK(int id, String status, String message) {
        ArrayList<Byte> response = new ArrayList<Byte>();

        Utils.appendMsg(response, id);
        Utils.appendMsg(response, status);
        Utils.appendMsgHeader(response, message);

        return Utils.unwrapList(response);
    }
}