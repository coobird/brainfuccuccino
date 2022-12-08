package net.coobird.labs.brainfuccuccino.machine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface BrainfuckMachine {
    void evaluate(byte[] program, InputStream is, OutputStream os) throws IOException;
}
