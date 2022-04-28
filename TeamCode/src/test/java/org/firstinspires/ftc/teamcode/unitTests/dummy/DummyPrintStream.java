package org.firstinspires.ftc.teamcode.unitTests.dummy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Locale;

public class DummyPrintStream extends PrintStream {
    private String buffer = "";

    public DummyPrintStream() {
        super(new DummyOutputStream());
    }

    public DummyPrintStream(OutputStream out) {
        super(out);
    }

    public DummyPrintStream(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public DummyPrintStream(OutputStream out, boolean autoFlush, String encoding) throws UnsupportedEncodingException {
        super(out, autoFlush, encoding);
    }

    public DummyPrintStream(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    public DummyPrintStream(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(fileName, csn);
    }

    public DummyPrintStream(File file) throws FileNotFoundException {
        super(file);
    }

    public DummyPrintStream(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(file, csn);
    }

    public void print(String s) {
        buffer += s;
    }

    public void flush() {
        throw new RuntimeException("Stub!");
    }

    public void close() {
        throw new RuntimeException("Stub!");
    }

    public boolean checkError() {
        throw new RuntimeException("Stub!");
    }

    protected void setError() {
        throw new RuntimeException("Stub!");
    }

    protected void clearError() {
        throw new RuntimeException("Stub!");
    }

    public void write(int b) {
        throw new RuntimeException("Stub!");
    }

    public void write(byte[] buf, int off, int len) {
        throw new RuntimeException("Stub!");
    }

    public void print(boolean b) {
        print(b + "");
    }

    public void print(char c) {
        print(c + "");
    }

    public void print(int i) {
        print(i + "");
    }

    public void print(long l) {
        print(l + "");
    }

    public void print(float f) {
        print(f + "");
    }

    public void print(double d) {
        print(d + "");
    }

    public void print(char[] s) {
        print(new String(s));
    }

    public void print(Object obj) {
        print(obj + "");
    }

    public void println() {
        println("");
    }

    public void println(boolean x) {
        println(x + "");
    }

    public void println(char x) {
        println(x + "");
    }

    public void println(int x) {
        println(x + "");
    }

    public void println(long x) {
        println(x + "");
    }

    public void println(float x) {
        println(x + "");
    }

    public void println(double x) {
        println(x + "");
    }

    public void println(char[] x) {
        println(new String(x));
    }

    public void println(String x) {
        print(x + "\n");
    }

    public void println(Object x) {
        println(x + "");
    }

    public PrintStream printf(String format, Object... args) {
        print(String.format(format, args));
        return this;
    }

    public PrintStream printf(Locale l, String format, Object... args) {
        print(String.format(l, format, args));
        return this;
    }

    public PrintStream format(String format, Object... args) {
        return printf(format, args);
    }

    public PrintStream format(Locale l, String format, Object... args) {
        return printf(l, format, args);
    }

    public PrintStream append(CharSequence csq) {
        print(csq + "");
        return this;
    }

    public PrintStream append(CharSequence csq, int start, int end) {
        print(csq + "");
        return this;
    }

    public PrintStream append(char c) {
        print(c);
        return this;
    }

    public void printBufferTo(PrintStream real) {
        real.print(buffer);
        buffer = "";
    }
}
