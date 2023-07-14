package com.itranswarp.jerrymouse.engine;

import java.io.IOException;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

public class ServletInputStreamImpl extends ServletInputStream {

    private final byte[] data;
    private int lastIndexRetrieved = -1;
    private ReadListener readListener = null;

    public ServletInputStreamImpl(byte[] data) {
        this.data = data;
    }

    @Override
    public boolean isFinished() {
        return lastIndexRetrieved == data.length - 1;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        this.readListener = readListener;
        if (!isFinished()) {
            try {
                readListener.onDataAvailable();
            } catch (IOException e) {
                readListener.onError(e);
            }
        } else {
            try {
                readListener.onAllDataRead();
            } catch (IOException e) {
                readListener.onError(e);
            }
        }
    }

    @Override
    public int read() throws IOException {
        if (lastIndexRetrieved < data.length) {
            lastIndexRetrieved++;
            int n = data[lastIndexRetrieved];
            if (readListener != null && isFinished()) {
                try {
                    readListener.onAllDataRead();
                } catch (IOException ex) {
                    readListener.onError(ex);
                    throw ex;
                }
            }
            return n;
        }
        return -1;
    }

    @Override
    public int available() throws IOException {
        return data.length - lastIndexRetrieved - 1;
    }

    @Override
    public void close() throws IOException {
        lastIndexRetrieved = data.length - 1;
    }
}
