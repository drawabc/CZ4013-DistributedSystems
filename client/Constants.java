package client;

public class Constants {
    public static final String DEFAULT_HOST = "127.0.0.1";
    public static final int DEFAULT_PORT = 8000;
    public static final double FAIL_RATE = 0.0;
    public static final int AT_LEAST_ONCE = 0;
    public static final int AT_MOST_ONCE = 1;
    public static final int DEFAULT_SEMANTIC_INVOCATION = AT_MOST_ONCE;
    public static final int DEFAULT_TIMEOUT = 2000;
    public static long REFRESH_INTERVAL;
    public static final double PACKET_LOSS_RATE = 1;

    public static final int READFILE_ID = 1;
    public static final int INSERTTOFILE_ID = 2;
    public static final int MONITORFILE_ID = 3;
    public static final int MONITOREND_ID = 7;
    public static final int DELETEINFILE_ID = 4;
    public static final int CHECKCACHE_ID = 5;
    public static final int COUNTCHAR_ID = 6;

    public static final int LONG_SIZE = 8;
    public static final int INT_SIZE = 4;
}
