package main.client;

public class Utils {
    public static byte[] marshal(int x) {
        return new byte[] { (byte) (x >> 24), (byte) (x >> 16), (byte) (x >> 8), (byte) (x >> 0), };
    }

    public static int unmarshal(byte[] b, int start) {
        return b[start] << 24 | (b[start + 1] & 0xFF) << 16 | (b[start + 2] & 0xFF) << 8 | (b[start + 3] & 0xFF);
    }

    public static byte[] marshal(String s) {
        byte[] b = new byte[s.length()];
        for (int i = 0; i < s.length(); i++) {
            b[i] = (byte) s.charAt(i);
        }
        return b;
    }

    public static String unmarshal(byte[] b, int start, int end) {
        char[] c = new char[end - start];
        for (int i = start; i < end; i++) {
            c[i - start] = (char) (b[i]);
        }
        return new String(c);
    }
}