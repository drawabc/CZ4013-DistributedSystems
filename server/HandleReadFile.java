package server;

import client.Constants;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class HandleReadFile {
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
        int numBytes = Utils.unmarshal(message, pointer);

        System.out.println(String.format("Read file: %s %d %d", filePath, offset, numBytes));

        String fileContent = readFile(filePath, offset, numBytes);
        String errorMsg;
        if (fileContent.equals("FileNotFound")) {
            errorMsg = "An error occured. The file " + filePath + " does not exist.";
            byte[] response = createACK(server.getID(), "0", errorMsg);
            server.send(response, Constants.READFILE_ID, address, port);
        } else if (fileContent.equals("IOException")) {
            errorMsg = "An error occured. Maybe the offset is too large?";
            byte[] response = createACK(server.getID(), "0", errorMsg);
            server.send(response, Constants.READFILE_ID, address, port);
        } else {
            byte[] response = createACK(server.getID(), "1", fileContent);
            server.send(response, Constants.READFILE_ID, address, port);
        }
    }

    public static String readFile(String filePath, int offset, int numBytes) {
        // TODO: CHECK ALL TEST CASES
        filePath = Constants.FILEPATH + filePath;
        RandomAccessFile aFile;
        try {
            aFile = new RandomAccessFile(filePath, "r");
        } catch (FileNotFoundException e) {
            return "FileNotFound";
        }
        FileChannel inChannel = aFile.getChannel();
        MappedByteBuffer buffer;
        try {
            buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, offset,
                    Math.min(inChannel.size() - offset, numBytes));
            byte[] b = new byte[buffer.remaining()];
            buffer.get(b);

            buffer.clear(); // do something with the data and clear/compact it.
            inChannel.close();
            aFile.close();

            return new String(b);
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