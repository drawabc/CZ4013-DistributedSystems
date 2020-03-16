package test;

import java.io.IOException;

import main.server.HandleInsertToFile;
import main.server.HandleReadFile;

public class TestFileHandling {
    public static void main(String[] args) {
        try {
            System.out.println(new String(HandleReadFile.readFile("/home/nydia/Documents/CZ4013/tes.txt", 0, 1000)));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.print(e);
        }

        try {
            HandleInsertToFile.insertToFile("/home/nydia/Documents/CZ4013/tespendek.txt", 1, "tes");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}