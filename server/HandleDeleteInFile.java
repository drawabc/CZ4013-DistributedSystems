package server;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.util.ArrayList;

public class HandleDeleteInFile {
    public static void handleRequest(UDPServer server, byte[] message, InetAddress address, int port) {
        int pointer = 0;
        int length = Utils.unmarshal(message, pointer);
        pointer += Constants.INT_SIZE;
        String filePath = Constants.FILEPATH + Utils.unmarshal(message, pointer, pointer + length);
        pointer += length;

        length = Utils.unmarshal(message, pointer);
        pointer += Constants.INT_SIZE;
        int offset = Utils.unmarshal(message, pointer);
        pointer += length;

        length = Utils.unmarshal(message, pointer);
        pointer += Constants.INT_SIZE;
        int numBytes = Utils.unmarshal(message, pointer);

        System.out.println(String.format("Delete in file: %s %d %d", filePath, offset, numBytes));

        String fileContent = deleteInFile(filePath, offset, numBytes);
        String errorMsg;
        if (fileContent.equals("FileNotFound")) {
            errorMsg = "An error occured. The file " + filePath + " does not exist.";
            byte[] response = createNAK(server.getID(), "0", errorMsg);
            server.send(response, Constants.DELETEINFILE_ID, address, port);
        } else if (fileContent.equals("IOException")) {
            errorMsg = "An error occured. Maybe the offset is too large?";
            byte[] response = createNAK(server.getID(), "0", errorMsg);
            server.send(response, Constants.DELETEINFILE_ID, address, port);
        } else {
            LastModified.update(filePath);

            byte[] response = createACK(server.getID(), "1", LastModified.getTimestamp(filePath), fileContent);
            server.send(response, Constants.DELETEINFILE_ID, address, port);
            HandleMonitor.notify(server, filePath);
        }
    }

    public static String deleteInFile(String filePath, int offset, int numBytes) {
        // Read file

        RandomAccessFile aFile;
        try {
            aFile = new RandomAccessFile(filePath, "rw");
        } catch (FileNotFoundException e1) {
            return "FileNotFound";
        }
        /*
         * FileChannel inChannel = aFile.getChannel();
         * 
         * MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_WRITE, 0,
         * inChannel.size());
         * 
         * byte[] beforeOffset = new byte[offset]; buffer.get(beforeOffset, 0, offset);
         * 
         * byte[] afterOffset = new byte[buffer.remaining() - numBytes]; // Iterate
         * buffer until offset for (int i = 0; i < numBytes; i++) buffer.get();
         * buffer.get(afterOffset);
         * 
         * System.out.println("Before offset:"); for (int i = 0; i <
         * beforeOffset.length; i++) { System.out.print((char) beforeOffset[i]); }
         * 
         * System.out.println("\nAfter offset:"); for (int i = 0; i <
         * afterOffset.length; i++) { System.out.print((char) afterOffset[i]); }
         * buffer.clear(); aFile.setLength(0);
         */

        // Read file contents
        ArrayList<Byte> x = new ArrayList<Byte>();
        byte c;
        try {
            while (true) {
                c = aFile.readByte();
                x.add(c);
            }
        } catch (EOFException e) {
            System.out.println("File Length = " + x.size());
        } catch (IOException e) {
            return "IOException";
        }

        // seperate file before Offset
        byte[] beforeOffset = new byte[offset];
        for (int i = 0; i < offset; i++) {
            beforeOffset[i] = x.get(i);
        }

        // seperate file after deletion
        byte[] afterOffset = new byte[x.size() - beforeOffset.length - numBytes];
        int j = 0;
        for (int i = offset + numBytes; i < x.size(); i++) {
            afterOffset[j] = x.get(i);
            j++;
        }
        try {
            aFile.setLength(0);
            aFile.write(beforeOffset);
            aFile.write(afterOffset);

            aFile.close();

            return new String(beforeOffset) + new String(afterOffset);
        } catch (IOException e) {
            return "IOException";
        }
        // do something with the data and clear/compact it.

        /*
         * FileOutputStream outputStream = new FileOutputStream(filePath);
         * outputStream.write(beforeOffset); outputStream.write(afterOffset);
         * outputStream.close();
         */
    }

    public static byte[] createACK(int id, String status, long time, String message) {
        ArrayList<Byte> response = new ArrayList<Byte>();

        Utils.appendMsg(response, id);
        Utils.appendMsg(response, status);
        Utils.appendMsg(response, time);
        Utils.appendMsgHeader(response, message);

        return Utils.unwrapList(response);
    }

    public static byte[] createNAK(int id, String status, String message) {
        ArrayList<Byte> response = new ArrayList<Byte>();

        Utils.appendMsg(response, id);
        Utils.appendMsg(response, status);
        Utils.appendMsgHeader(response, message);

        return Utils.unwrapList(response);
    }
}