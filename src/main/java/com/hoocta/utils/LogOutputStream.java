package com.hoocta.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LogOutputStream extends OutputStream {
    private final FileOutputStream fileOutputStream;

    public LogOutputStream(String filePath) throws IOException {
        fileOutputStream = new FileOutputStream(filePath, true);
    }

    @Override
    public void write(int b) throws IOException {
        fileOutputStream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        fileOutputStream.write(b, off, len);
    }

    @Override
    public void close() throws IOException {
        fileOutputStream.close();
    }
}

