package com.itranswarp.jerrymouse.engine;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

public class ServletOutputStreamImpl extends ServletOutputStream {

    private final OutputStream output;
    private WriteListener writeListener = null;

    public ServletOutputStreamImpl(OutputStream output) {
        this.output = output;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void close() throws IOException {
        this.output.close();
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        this.writeListener = writeListener;
        try {
            this.writeListener.onWritePossible();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void write(int b) throws IOException {
        try {
            this.output.write(b);
        } catch (IOException e) {
            if (this.writeListener != null) {
                this.writeListener.onError(e);
            }
            throw e;
        }
    }
}
