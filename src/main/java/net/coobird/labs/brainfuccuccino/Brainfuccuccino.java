package net.coobird.labs.brainfuccuccino;

import net.coobird.labs.brainfuccuccino.machine.BoundedBrainfuckMachine;
import net.coobird.labs.brainfuccuccino.machine.SignedByteBoundedBrainfuckMachine;
import net.coobird.labs.brainfuccuccino.machine.BrainfuckMachine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public final class Brainfuccuccino {
    private final InputStream is;
    private final OutputStream os;
    private final Flavor flavor;

    private Brainfuccuccino(InputStream is, OutputStream os, Flavor flavor) {
        this.is = is;
        this.os = os;
        this.flavor = flavor;
    }

    public static void brew(String s) throws IOException {
        customize().evaluate(s);
    }

    public void evaluate(byte[] program) throws IOException {
        BrainfuckMachine machine;
        switch (this.flavor) {
            case REGULAR:
                machine = new BoundedBrainfuckMachine();
                break;
            case SIGNED_BYTE_BOUNDED:
                machine = new SignedByteBoundedBrainfuckMachine();
                break;
            default:
                machine = null;
        }

        machine.evaluate(program, this.is, this.os);
    }

    public void evaluate(String s) throws IOException {
        evaluate(s.getBytes(StandardCharsets.US_ASCII));
    }

    public static Brainfuccuccino customize() {
        return new Brainfuccuccino(System.in, System.out, Flavor.REGULAR);
    }

    public Brainfuccuccino flavor(Flavor flavor) {
        return new Brainfuccuccino(this.is, this.os, flavor);
    }

    public Brainfuccuccino attach(InputStream is) {
        return new Brainfuccuccino(is, this.os, this.flavor);
    }

    public Brainfuccuccino attach(OutputStream os) {
        return new Brainfuccuccino(this.is, os, this.flavor);
    }
}
