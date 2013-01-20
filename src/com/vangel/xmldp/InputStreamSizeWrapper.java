package com.vangel.xmldp;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author VAngeL
 * date: 20.01.13
 */
public class InputStreamSizeWrapper extends InputStream {
    private InputStream stream;

    private volatile long totalRead;

    public InputStreamSizeWrapper(InputStream stream) {
        this.stream = stream;
    }

    public long getTotalBytesRead() {
        return totalRead;
    }

    private void updateTotal(long bytesRead) {
        if (bytesRead != -1) {
            totalRead += bytesRead;
        }
    }

    @Override
    public int read() throws IOException {
        int cnt = stream.read();

        updateTotal(cnt);
        return cnt;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int cnt = stream.read(b);

        updateTotal(cnt);
        return cnt;
    }

    @Override
    public int read(byte[] b, int offset, int length) throws IOException {
        int cnt = stream.read(b, offset, length);

        updateTotal(cnt);
        return cnt;
    }

    @Override
    public long skip(long n) throws IOException {
        long cnt = stream.skip(n);

        updateTotal(cnt);
        return cnt;
    }

    @Override
    public synchronized void reset() throws IOException {
        stream.reset();
        totalRead = 0L;
    }

    @Override
    public int available() throws IOException {
        return stream.available();
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    @Override
    public void mark(int readlimit) {
        stream.mark(readlimit);
    }

    @Override
    public boolean markSupported() {
        return stream.markSupported();
    }
}
