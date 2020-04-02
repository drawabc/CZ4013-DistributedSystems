package server;

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
        pointer += 4;
        String filePath = Utils.unmarshal(message, pointer, pointer + length);
        pointer += length;

        length = Utils.unmarshal(message, pointer);
        pointer += 4;
        int offset = Utils.unmarshal(message, pointer);
        pointer += length;

        length = Utils.unmarshal(message, pointer);
        pointer += 4;
        String content = Utils.unmarshal(message, pointer, pointer + length);

        System.out.println(String.format("Insert to file: %s %d %s", filePath, offset, content));

        try {
            byte[] response = createACK(server.getID(), "1", insertToFile(filePath, offset, content));
            server.send(response, 2, address, port);
            String notification = address.toString() + ":" + port + " editted " + filePath;
            HandleMonitor.notify(server, filePath, notification);
        } catch (IOException e) {
            System.out.println(e);
            String errorMsg = "An error occured. Either the filename is incorrect or the offset exceeds the length";
            byte[] response = createACK(server.getID(), "0", errorMsg);
            server.send(response, 2, address, port);
        }

    }

    public static String insertToFile(String filePath, int offset, String content) throws IOException {
        // Read file
        filePath = "server/data/" + filePath;
        RandomAccessFile aFile = new RandomAccessFile(filePath, "r");
        FileChannel inChannel = aFile.getChannel();
        MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());

        byte[] beforeOffset = new byte[offset];
        buffer.get(beforeOffset, 0, offset);

        byte[] afterOffset = new byte[buffer.remaining()];
        buffer.get(afterOffset);

        buffer.clear(); // do something with the data and clear/compact it.
        inChannel.close();
        aFile.close();

        System.out.println("Before offset:");
        for (int i = 0; i < beforeOffset.length; i++) {
            System.out.print((char) beforeOffset[i]);
        }

        System.out.println("\nAfter offset:");
        for (int i = 0; i < afterOffset.length; i++) {
            System.out.print((char) afterOffset[i]);
        }

        // Rewrite file up to offset
        FileOutputStream outputStream = new FileOutputStream(filePath);
        outputStream.write(beforeOffset);

        // Insert contents to file
        outputStream.write(content.getBytes());

        // Rewrite rest of the file
        outputStream.write(afterOffset);
        outputStream.close();

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