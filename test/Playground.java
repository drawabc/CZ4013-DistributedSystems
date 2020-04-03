package test;
// TESTBED NOT USED
import java.io.*;
import java.util.RandomAccess;
import java.util.concurrent.TimeUnit;
public class Playground {
    public static void main(String[] args){
        try{
            RandomAccessFile afile = new RandomAccessFile("src/main/server/data/data.txt", "rw");
            afile.write("ASDFas".getBytes());
        } catch (Exception e){
            e.printStackTrace();
        }


    }
}
