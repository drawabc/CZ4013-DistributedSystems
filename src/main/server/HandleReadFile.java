package main.server;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class HandleReadFile {
    public static byte[] handleRequest(byte[] message) {
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

        System.out.println(String.format("%s %d %d", filePath, offset, numBytes));

        // TODO: implement response message
        try {
            return readFile(filePath, offset, numBytes);
        } catch (IOException e) {
            System.out.println(e);
            String errorMsg = "An error occured. Either the filename is incorrect or the offset exceeds the length";
            return errorMsg.getBytes();
        }
    }

    public static byte[] readFile(String filePath, int offset, int numBytes) throws IOException {
        RandomAccessFile aFile = new RandomAccessFile(filePath, "r");
        FileChannel inChannel = aFile.getChannel();
        MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, offset,
                Math.min(inChannel.size() - offset, numBytes));

        byte[] b = new byte[buffer.remaining()];
        buffer.get(b);

        buffer.clear(); // do something with the data and clear/compact it.
        inChannel.close();
        aFile.close();

        return b;
    }
}