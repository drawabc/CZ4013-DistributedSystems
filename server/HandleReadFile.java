package server;

import client.Constants;

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

        System.out.println(String.format("Read file: %s %d %d", filePath, offset, numBytes));

        try {
            byte[] response = createACK(server.getID(), "1", readFile(filePath, offset, numBytes));
            server.send(response, 1, address, port);
        } catch (IOException e) {
            System.out.println(e);
            String errorMsg = "An error occured. Either the filename is incorrect or the offset exceeds the length";
            byte[] response = createACK(server.getID(), "0", errorMsg);
            server.send(response, 1, address, port);
        }
    }

    public static String readFile(String filePath, int offset, int numBytes) throws IOException {
        // TODO: CHECK ALL TEST CASES
        filePath = Constants.FILEPATH + filePath;
        RandomAccessFile aFile = new RandomAccessFile(filePath, "r");
        FileChannel inChannel = aFile.getChannel();
        MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, offset,
                Math.min(inChannel.size() - offset, numBytes));

        byte[] b = new byte[buffer.remaining()];
        buffer.get(b);

        buffer.clear(); // do something with the data and clear/compact it.
        inChannel.close();
        aFile.close();

        return new String(b);
    }

    public static byte[] createACK(int id, String status, String message) {
        ArrayList<Byte> response = new ArrayList<Byte>();

        Utils.appendMsg(response, id);
        Utils.appendMsg(response, status);
        Utils.appendMsgHeader(response, message);

        return Utils.unwrapList(response);
    }
}