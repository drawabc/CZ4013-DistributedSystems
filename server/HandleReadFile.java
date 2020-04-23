package server;

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
        String filePath = Constants.FILEPATH + Utils.unmarshal(message, pointer, pointer + length);
        System.out.println("Read file: " + filePath);

        String fileContent = readFile(filePath);
        String errorMsg;
        if (fileContent.equals("FileNotFound")) {
            errorMsg = "An error occured. The file " + filePath + " does not exist.";
            byte[] response = createNAK(server.getID(), "0", errorMsg);
            server.send(response, Constants.READFILE_ID, address, port);
        } else if (fileContent.equals("IOException")) {
            errorMsg = "An error occured. Maybe the offset is too large?";
            byte[] response = createNAK(server.getID(), "0", errorMsg);
            server.send(response, Constants.READFILE_ID, address, port);
        } else {
            byte[] response = createACK(server.getID(), "1", LastModified.getTimestamp(filePath), fileContent);
            server.send(response, Constants.READFILE_ID, address, port);
        }
    }

    public static String readFile(String filePath) {
        RandomAccessFile aFile;
        try {
            aFile = new RandomAccessFile(filePath, "r");
        } catch (FileNotFoundException e) {
            return "FileNotFound";
        }
        FileChannel inChannel = aFile.getChannel();
        MappedByteBuffer buffer;
        try {
            buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
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
