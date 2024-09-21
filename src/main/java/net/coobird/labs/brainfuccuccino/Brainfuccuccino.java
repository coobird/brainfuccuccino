/*
 * Brainfuccuccino - a brainfuck scripting engine for Java.
 *
 * The MIT License
 *
 * Copyright (c) 2021-2024 Chris Kroells
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.coobird.labs.brainfuccuccino;

import net.coobird.labs.brainfuccuccino.machine.BoundedBrainfuckMachine;
import net.coobird.labs.brainfuccuccino.machine.SignedByteBrainfuckMachine;
import net.coobird.labs.brainfuccuccino.machine.BrainfuckMachine;
import net.coobird.labs.brainfuccuccino.vm.BrainfuckVirtualMachine;
import net.coobird.labs.brainfuccuccino.vm.BrainfuckVirtualMachineCompiler;
import net.coobird.labs.brainfuccuccino.vm.model.Instruction;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Brainfuccuccino is a Java scripting engine which allows
 * <a href="https://en.wikipedia.org/wiki/Brainfuck">brainfuck</a> programs
 * to be embedded and run in Java applications.
 * <p/>
 * The {@code Brainfuccuccino} class provides two styles to running brainfuck
 * programs, using coffee-themed method names:
 *
 * <ol>
 *     <li>Default execution with the {@link Brainfuccuccino#brew(String)} method.</li>
 *     <li>Customized execution using the fluent interface.</li>
 * </ol>
 *
 * <h2>Default execution with the {@code Brainfuccuccino.brew} method</h2>
 *
 * With the {@link #brew(String)} method, a {@link String} representation of
 * a brainfuck program will be run, with the input being {@link System#in},
 * while output being {@link System#out}.
 * <p/>
 * A {@code cat} program can be run by:
 *
 * <p><blockquote><pre>
 * Brainfuccuccino.brew(",[.,]");
 * </pre></blockquote></p>
 *
 * <h2>Customized execution via a fluent interface.</h2>
 *
 * Brainfuccuccino also has a fluent interface for customizing your brew.
 * First, call {@link Brainfuccuccino#customize()}.
 * <p/>
 * Next, use the {@link #flavor(Flavor)} method to pick the execution
 * implementation. (See the {@link Flavor} enum for more information.)
 * This is completely optional.
 * <p/>
 * Attach your inputs and outputs using the {@link #attach(InputStream)}
 * and {@link #attach(OutputStream)} methods. Both are optional, which default
 * to using {@link System#in} and {@link System#out}, respectively.
 * <p/>
 * Finally, to run the program, call {@link #evaluate(String)}:
 * <p><blockquote><pre>
 * ByteArrayOutputStream os = new ByteArrayOutputStream();
 *
 * // Prints "Hello World!" to the ByteArrayOutputStream.
 * Brainfuccuccino.customize()
 *         .attach(new ByteArrayInputStream("Hello World!".getBytes(StandardCharsets.US_ASCII)))
 *         .attach(os)
 *         .evaluate(",[.,]");
 *
 * </pre></blockquote></p>
 */
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
            case SIGNED_BYTE:
                machine = new SignedByteBrainfuckMachine();
                break;
            case INSTANT:
                String programStr = new String(program, StandardCharsets.UTF_8);
                List<Instruction> instructions = new BrainfuckVirtualMachineCompiler()
                        .compile(programStr, 1);
                new BrainfuckVirtualMachine(instructions, this.is, this.os).execute();
                return;

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
