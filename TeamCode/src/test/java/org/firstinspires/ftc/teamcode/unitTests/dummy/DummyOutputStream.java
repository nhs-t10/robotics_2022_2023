package org.firstinspires.ftc.teamcode.unitTests.dummy;

import java.io.IOException;
import java.io.OutputStream;

public class DummyOutputStream extends OutputStream {
    private boolean closed;
    private String buf = "";

    @Override
    public void write(int i) throws IOException {
        buf += (char)i;
    }
    public void write(byte[] b) throws IOException {
        buf += new String(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        buf += new String(b, off, len);
    }

    public void flush() throws IOException {
        if(closed) throw new IOException("Stream closed");
    }

    public void close() throws IOException {
        this.closed = true;
    }
    public String getBuf() {
        return buf;
    }
}
