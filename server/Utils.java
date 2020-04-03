package server;

import java.util.Arrays;
import java.util.List;

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

    // These methods are necessary to convert primitive to java object bytes
    public static Byte[] wrapByte(byte[] b) {
        Byte[] wrap = new Byte[b.length];
        for (int i = 0; i < b.length; i++) {
            wrap[i] = Byte.valueOf(b[i]);
        }
        return wrap;
    }

    public static byte[] unwrapByte(Byte[] b) {
        byte[] unwrap = new byte[b.length];
        for (int i = 0; i < b.length; i++) {
            unwrap[i] = b[i].byteValue();
        }
        return unwrap;
    }

    public static byte[] unwrapList(List<Byte> list) {
        return unwrapByte((Byte[]) list.toArray(new Byte[list.size()]));
    }

    public static void appendMsg(List<Byte> list, String s) {
        list.addAll(Arrays.asList(wrapByte(marshal(s))));
    }

    public static void appendMsg(List<Byte> list, int x) {
        list.addAll(Arrays.asList(wrapByte(marshal(x))));
    }

    public static void appendMsgHeader(List<Byte> list, String s) {
        list.addAll(Arrays.asList(wrapByte(marshal(s.length()))));
        list.addAll(Arrays.asList(wrapByte(marshal(s))));
    }

    public static void appendMsgHeader(List<Byte> list, int x) {
        list.addAll(Arrays.asList(wrapByte(marshal(4)))); // Int size: 4 bytes
        list.addAll(Arrays.asList(wrapByte(marshal(x))));
    }
}