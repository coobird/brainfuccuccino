/*
 * Brainfuccuccino - a brainfuck scripting engine for Java.
 *
 * The MIT License
 *
 * Copyright (c) 2021-2025 Chris Kroells
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

package net.coobird.labs.brainfuccuccino.vm;

import net.coobird.labs.brainfuccuccino.Utils;
import net.coobird.labs.brainfuccuccino.machine.MemoryRangeOutOfBoundsException;
import net.coobird.labs.brainfuccuccino.machine.state.MachineMetrics;
import net.coobird.labs.brainfuccuccino.machine.state.MachineState;
import net.coobird.labs.brainfuccuccino.vm.model.Instruction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BrainfuckVirtualMachineTest {
    private final BrainfuckVirtualMachineCompiler compiler = new BrainfuckVirtualMachineCompiler();

    static Stream<Arguments> optimizationLevels() {
        return Stream.of(
                Arguments.of(0),
                Arguments.of(1)
        );
    }

    @ParameterizedTest(name = "optimizationLevel = {0}")
    @MethodSource("optimizationLevels")
    public void printHelloWorld(int optimizationLevel) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<Instruction> instructions = compiler.compile(Utils.getScriptFromResources("hello_world.bf"), optimizationLevel);

        new BrainfuckVirtualMachine(instructions, null, baos).execute();

        assertEquals("Hello World!\n", baos.toString());
    }

    @ParameterizedTest(name = "optimizationLevel = {0}")
    @MethodSource("optimizationLevels")
    public void printHelloWorldSignedBytes(int optimizationLevel) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<Instruction> instructions = compiler.compile(Utils.getScriptFromResources("signed_hello_world.bf"), optimizationLevel);

        new BrainfuckVirtualMachine(instructions, null, baos).execute();

        assertEquals("Hello World!\n", baos.toString());
    }

    @ParameterizedTest(name = "optimizationLevel = {0}")
    @MethodSource("optimizationLevels")
    public void loop(int optimizationLevel) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<Instruction> instructions = compiler.compile(Utils.getScriptFromResources("loop.bf"), optimizationLevel);

        new BrainfuckVirtualMachine(instructions, null, baos).execute();

        assertEquals("*", baos.toString());
    }

    @ParameterizedTest(name = "optimizationLevel = {0}")
    @MethodSource("optimizationLevels")
    public void nestedLoop(int optimizationLevel) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<Instruction> instructions = compiler.compile(Utils.getScriptFromResources("nested_loop.bf"), optimizationLevel);

        new BrainfuckVirtualMachine(instructions, null, baos).execute();

        assertEquals("*", baos.toString());
    }

    @ParameterizedTest(name = "optimizationLevel = {0}")
    @MethodSource("optimizationLevels")
    public void dots(int optimizationLevel) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<Instruction> instructions = compiler.compile(Utils.getScriptFromResources("dots.bf"), optimizationLevel);

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

    @ParameterizedTest(name = "optimizationLevel = {0}")
    @MethodSource("optimizationLevels")
    public void catInput(int optimizationLevel) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<Instruction> instructions = compiler.compile(Utils.getScriptFromResources("cat.bf"), optimizationLevel);

        new BrainfuckVirtualMachine(
                instructions,
                new ByteArrayInputStream("Hello World!".getBytes(StandardCharsets.US_ASCII)),
                baos
        ).execute();

        assertEquals("Hello World!", baos.toString());
    }

    @ParameterizedTest(name = "optimizationLevel = {0}")
    @MethodSource("optimizationLevels")
    public void catInputUtf8(int optimizationLevel) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<Instruction> instructions = compiler.compile(Utils.getScriptFromResources("cat.bf"), optimizationLevel);

        new BrainfuckVirtualMachine(
                instructions,
                new ByteArrayInputStream("こんにちは世界！".getBytes(StandardCharsets.UTF_8)),
                baos
        ).execute();

        assertEquals("こんにちは世界！", baos.toString());
    }

    public static Stream<Arguments> memoryInRangeCases() {
        return Stream.of(
                Arguments.of("+.", 0),
                Arguments.of("+.", 1),
                Arguments.of(">+.", 0),
                Arguments.of(">+.", 1),
                Arguments.of(">>+.", 0),
                Arguments.of(">>+.", 1)
        );
    }

    @ParameterizedTest(name = "program = {0}, optimizationLevel = {1}")
    @MethodSource("memoryInRangeCases")
    public void whenMemoryPositionWithinSizeThenCorrectValueInMemoryCell(String program, int optimizationLevel) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        List<Instruction> instructions = compiler.compile(program, optimizationLevel);

        new BrainfuckVirtualMachine(instructions, null, os).execute();

        byte[] actualOutput = os.toByteArray();
        assertEquals(1, actualOutput.length);
        assertEquals((byte) 1, actualOutput[0]);
    }

    public static Stream<Arguments> memoryOutOfRangeCases() {
        final String moveRightOutOfRange = String.join("", Collections.nCopies(30000, ">"));
        return Stream.of(
                Arguments.of("<", 0),
                Arguments.of("<", 1),
                Arguments.of(moveRightOutOfRange, 0),
                Arguments.of(moveRightOutOfRange, 1)
        );
    }

    @ParameterizedTest(name = "program = {0}, optimizationLevel = {1}")
    @MethodSource("memoryOutOfRangeCases")
    public void whenMemoryOutOfRangeThenExceptionThrown(String program, int optimizationLevel) {
        List<Instruction> instructions = compiler.compile(program, optimizationLevel);
        assertThrows(
                MemoryRangeOutOfBoundsException.class,
                () -> new BrainfuckVirtualMachine(instructions, null, null).execute()
        );
    }

    @Test
    public void introspectionTest() throws IOException {
        BrainfuckVirtualMachineCompiler compiler = new BrainfuckVirtualMachineCompiler();
        List<Instruction> instructions = compiler.compile("+>++>+++", 1);

        BrainfuckVirtualMachine machine = new BrainfuckVirtualMachine(
                instructions, null, null
        );
        machine.execute();

        MachineState<Byte> state = machine.getState();
        assertEquals(5, state.getProgramCounter());
        assertEquals(2, state.getDataPointer());
        assertArrayEquals(
                new Byte[] {1, 2, 3},
                Arrays.stream(state.getMemory()).limit(3).toArray()
        );
    }

    @Test
    public void inspectionTest() throws IOException {
        BrainfuckVirtualMachineCompiler compiler = new BrainfuckVirtualMachineCompiler();
        List<Instruction> instructions = compiler.compile("+>++>+++", 5);

        BrainfuckVirtualMachine machine = new BrainfuckVirtualMachine(
                instructions, null, null
        );
        machine.execute();

        MachineMetrics metrics = machine.getMetrics();
        assertEquals(5, metrics.getInstructionsExecuted());
        assertEquals(0, metrics.getInstructionsSkipped());
        assertEquals(5, metrics.getProgramCounterChanges());
    }
}
