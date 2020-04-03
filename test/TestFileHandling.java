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
            HandleDeleteInFile.deleteInFile("data.txt", 2, 3);
        } catch (Exception e) {
            System.out.println("Exception raised");
        }
    }
}