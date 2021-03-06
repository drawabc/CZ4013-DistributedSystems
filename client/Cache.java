package client;

import java.time.Clock;
import java.util.ArrayList;

public class Cache {
    private Clock clock = Clock.systemDefaultZone();
    private String fileContent;
    private long lastValidated;
    private long lastModified;
    private String filePath;

    public Cache(String filePath) {
        this.filePath = filePath;
        // this.lastValidated = clock.millis();
    }

    public String getFileContent(int offset, int numBytes) {
        int contentlength = Math.min(offset + numBytes, fileContent.length());
        return fileContent.substring(offset, contentlength);
    }

    public String getFileContent() {
        return fileContent;
    }

    public int countChar(char selected) {
        int counter = 0;
        for (int i = 0; i < fileContent.length(); i++) {
            if (fileContent.charAt(i) == selected) {
                counter++;
            }
        }

        return counter;
    }

    // public String getFileContent(int offset, int numBytes) {
    // int contentlength = Math.min(offset + numBytes, fileContent.length());
    // if (isLocallyValid()) {
    // System.out.println("Cache (local) hit!");
    // return fileContent.substring(offset, contentlength);
    // } else if (isRemotelyValid()) {
    // System.out.println("Cache (remote) hit!");
    // return fileContent.substring(offset, contentlength);
    // } else {
    // System.out.println("Cache missed!");
    // fetchContent();
    // return fileContent.substring(offset, contentlength);
    // }
    // }

    // public void fetchContent() {
    // System.out.println("Fetching data...");
    // byte[] response =
    // App.udpclient.requestReply(ReadFile.constructRequest(filePath));
    // ReadFile.handleResponse(response, filePath, this);
    // }

    public void updateCache(long lastModified, String fileContent) {
        this.lastModified = lastModified;
        this.fileContent = fileContent;
        this.lastValidated = clock.millis();
    }

    public boolean isLocallyValid() {
        return clock.millis() - lastValidated < Constants.REFRESH_INTERVAL;
    }

    public byte[] constructRequest(int id) {
        ArrayList<Byte> request = new ArrayList<Byte>();

        Utils.appendMsg(request, id);
        Utils.appendMsg(request, Constants.CHECKCACHE_ID);
        Utils.appendMsgHeader(request, filePath);

        return Utils.unwrapList(request);
    }

    public boolean isRemotelyValid(byte[] response) {
        long serverLastModified = handleResponse(response);

        lastValidated = clock.millis();
        if (serverLastModified == lastModified) {
            return true;
        } else {
            return false;
        }
    }

    private long handleResponse(byte[] response) {
        int pointer = 0;
        String status = Utils.unmarshal(response, pointer, 1);
        pointer++;

        if (status.equals("1")) {
            return Utils.unmarshalLong(response, pointer);
        } else {
            return -1;
        }
    }

}
