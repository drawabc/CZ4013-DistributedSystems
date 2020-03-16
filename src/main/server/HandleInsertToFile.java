package main.server;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class HandleInsertToFile {
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
        String content = Utils.unmarshal(message, pointer, pointer + length);

        System.out.println(String.format("%s %d %s", filePath, offset, content));

        // TODO: implement response message
        try {
            return insertToFile(filePath, offset, content);
        } catch (IOException e) {
            System.out.println(e);
            String errorMsg = "An error occured. Either the filename is incorrect or the offset exceeds the length";
            return errorMsg.getBytes();
        }

    }

    public static byte[] insertToFile(String filePath, int offset, String content) throws IOException {
        // Read file
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

        return "Successfully inserted to file".getBytes();
    }
}