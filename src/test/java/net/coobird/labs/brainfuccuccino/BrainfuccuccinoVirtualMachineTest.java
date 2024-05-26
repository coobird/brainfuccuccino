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

import net.coobird.labs.brainfuccuccino.vm.BrainfuckVirtualMachine;
import net.coobird.labs.brainfuccuccino.vm.BrainfuckVirtualMachineCompiler;
import net.coobird.labs.brainfuccuccino.vm.model.Instruction;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BrainfuccuccinoVirtualMachineTest {
    private final BrainfuckVirtualMachineCompiler compiler = new BrainfuckVirtualMachineCompiler();

    @Test
    public void printHelloWorld() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<Instruction> instructions = compiler.compile(Utils.getScriptFromResources("hello_world.bf"));

        new BrainfuckVirtualMachine(instructions, null, baos).execute();

        assertEquals("Hello World!\n", baos.toString());
    }

    @Test
    public void printHelloWorldSignedBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<Instruction> instructions = compiler.compile(Utils.getScriptFromResources("signed_hello_world.bf"));

        new BrainfuckVirtualMachine(instructions, null, baos).execute();

        assertEquals("Hello World!\n", baos.toString());
    }

    @Test
    public void loop() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<Instruction> instructions = compiler.compile(Utils.getScriptFromResources("loop.bf"));

        new BrainfuckVirtualMachine(instructions, null, baos).execute();

        assertEquals("*", baos.toString());
    }

    @Test
    public void nestedLoop() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<Instruction> instructions = compiler.compile(Utils.getScriptFromResources("nested_loop.bf"));

        new BrainfuckVirtualMachine(instructions, null, baos).execute();

        assertEquals("*", baos.toString());
    }

    @Test
    public void dots() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<Instruction> instructions = compiler.compile(Utils.getScriptFromResources("dots.bf"));

        new BrainfuckVirtualMachine(instructions, null, baos).execute();

        StringBuilder expectedBuilder = new StringBuilder();
        for (int lines = 0; lines < 10; lines++) {
            for (int width = 0; width < 16; width++) {
                expectedBuilder.append('*');
            }
            expectedBuilder.append('\n');
        }

        assertEquals(expectedBuilder.toString(), baos.toString());
    }

    @Test
    public void catInput() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<Instruction> instructions = compiler.compile(Utils.getScriptFromResources("cat.bf"));

        new BrainfuckVirtualMachine(
                instructions,
                new ByteArrayInputStream("Hello World!".getBytes(StandardCharsets.US_ASCII)),
                baos
        ).execute();

        assertEquals("Hello World!", baos.toString());
    }

    @Test
    public void catInputUtf8() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<Instruction> instructions = compiler.compile(Utils.getScriptFromResources("cat.bf"));

        new BrainfuckVirtualMachine(
                instructions,
                new ByteArrayInputStream("こんにちは世界！".getBytes(StandardCharsets.UTF_8)),
                baos
        ).execute();

        assertEquals("こんにちは世界！", baos.toString());
    }
}
