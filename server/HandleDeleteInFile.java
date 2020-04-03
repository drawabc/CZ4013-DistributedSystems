package server;

import client.Constants;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class HandleDeleteInFile {
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
        int numBytes = Utils.unmarshal(message, pointer);

        System.out.println(String.format("Delete in file: %s %d %d", filePath, offset, numBytes));

        try {
            byte[] response = createACK(server.getID(), "1", deleteInFile(filePath, offset, numBytes));
            server.send(response, 4, address, port);
            String notification = address.toString() + ":" + port + " editted " + filePath;
            HandleMonitor.notify(server, Constants.FILEPATH + filePath, notification);
        } catch (IOException e) {
            System.out.println(e);
            String errorMsg = "An error occured. Either the filename is incorrect or the offset exceeds the length";
            byte[] response = createACK(server.getID(), "0", errorMsg);
            server.send(response, 4, address, port);
        }

    }

    public static String deleteInFile(String filePath, int offset, int numBytes) throws IOException {
        // Read file
        filePath = Constants.FILEPATH + filePath;
        RandomAccessFile aFile = new RandomAccessFile(filePath, "r");
        FileChannel inChannel = aFile.getChannel();
        MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());

        byte[] beforeOffset = new byte[offset];
        buffer.get(beforeOffset, 0, offset);

        byte[] afterOffset = new byte[buffer.remaining() - numBytes];
        // Iterate buffer until offset
        for (int i = 0; i < numBytes; i++)
            buffer.get();
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

        FileOutputStream outputStream = new FileOutputStream(filePath);
        outputStream.write(beforeOffset);
        outputStream.write(afterOffset);
        outputStream.close();

        return "Successfully deleted " + numBytes + "bytes in file " + filePath;
    }

    public static byte[] createACK(int id, String status, String message) {
        ArrayList<Byte> response = new ArrayList<Byte>();

        Utils.appendMsg(response, id);
        Utils.appendMsg(response, status);
        Utils.appendMsgHeader(response, message);

        return Utils.unwrapList(response);
    }
}