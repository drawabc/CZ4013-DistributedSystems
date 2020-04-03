package test;

import java.io.File;
import java.io.IOException;

import server.HandleDeleteInFile;
import server.HandleInsertToFile;
import server.HandleReadFile;

public class TestFileHandling {
    public static void main(String[] args) {
        /*
         * try { System.out.println(new String(HandleReadFile.readFile("data.txt", 0,
         * 1000))); } catch (Exception e) { System.out.print(e); }
         */
        // TODO: change file path to src/main/server/data + filePath in all functions
        try {
            File aFile = new File("src/main/server/data/data.txt");
            HandleInsertToFile.insertToFile("src/main/server/data/data.txt", 1, "tes");
        } catch (Exception e) {
            System.out.print(e);
        }
    }
}