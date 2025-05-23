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

package net.coobird.labs.brainfuccuccino.machine.impl;

import net.coobird.labs.brainfuccuccino.Utils;
import net.coobird.labs.brainfuccuccino.machine.debug.Breakpoint;
import net.coobird.labs.brainfuccuccino.machine.debug.Debuggable;
import net.coobird.labs.brainfuccuccino.machine.state.Introspectable;
import net.coobird.labs.brainfuccuccino.machine.state.MachineMetrics;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ByteBasedDebuggableBrainfuckMachineTest {
    public static Stream<Arguments> debuggableMachines() {
        return Stream.of(
                Arguments.of(new SignedByteBrainfuckMachine()),
                Arguments.of(new ClassicBrainfuckMachine())
        );
    }

    private static byte[] slice(Byte[] memory, int size) {
        byte[] tmp = new byte[size];
        for (int i = 0; i < size; i++) {
            tmp[i] = memory[i];
        }
        return tmp;
    }

    @ParameterizedTest
    @MethodSource("debuggableMachines")
    public <T extends Debuggable & Introspectable<Byte>> void breakpointTest(T machine) throws IOException {
        machine.addBreakpoint(new Breakpoint(2, true));
        machine.addBreakpoint(new Breakpoint(6, true));

        machine.load("+>++>+++".getBytes(), null, null);
        machine.execute();
        assertEquals(2, machine.getState().getProgramCounter());
        assertArrayEquals(new byte[] {1, 0, 0}, slice(machine.getState().getMemory(), 3));

        machine.execute();
        assertEquals(6, machine.getState().getProgramCounter());
        assertArrayEquals(new byte[] {1, 2, 1}, slice(machine.getState().getMemory(), 3));

        machine.execute();
        assertEquals(8, machine.getState().getProgramCounter());
        assertArrayEquals(new byte[] {1, 2, 3}, slice(machine.getState().getMemory(), 3));

        MachineMetrics metrics = machine.getMetrics();
        assertEquals(8, metrics.getInstructionsExecuted());
        assertEquals(0, metrics.getInstructionsSkipped());
        assertEquals(8, metrics.getProgramCounterChanges());
    }

    @ParameterizedTest
    @MethodSource("debuggableMachines")
    public <T extends Debuggable & Introspectable<Byte>> void breakpointWithCommentsTest(T machine) throws IOException {
        machine.addBreakpoint(new Breakpoint(2, true));
        machine.addBreakpoint(new Breakpoint(9, true));
        machine.addBreakpoint(new Breakpoint(13, true));

        machine.load("Comment+>++>+++".getBytes(), null, null);
        machine.execute();
        assertEquals(2, machine.getState().getProgramCounter());
        assertArrayEquals(new byte[] {0, 0, 0}, slice(machine.getState().getMemory(), 3));

        machine.execute();
        assertEquals(9, machine.getState().getProgramCounter());
        assertArrayEquals(new byte[] {1, 0, 0}, slice(machine.getState().getMemory(), 3));

        machine.execute();
        assertEquals(13, machine.getState().getProgramCounter());
        assertArrayEquals(new byte[] {1, 2, 1}, slice(machine.getState().getMemory(), 3));

        machine.execute();
        assertEquals(15, machine.getState().getProgramCounter());
        assertArrayEquals(new byte[] {1, 2, 3}, slice(machine.getState().getMemory(), 3));

        MachineMetrics metrics = machine.getMetrics();
        assertEquals(8, metrics.getInstructionsExecuted());
        assertEquals(7, metrics.getInstructionsSkipped());
        assertEquals(15, metrics.getProgramCounterChanges());
    }

    @ParameterizedTest
    @MethodSource("debuggableMachines")
    public <T extends Debuggable & Introspectable<Byte>> void breakpointOnAndOffTest(T machine) throws IOException {
        Breakpoint breakpoint = new Breakpoint(48, true);
        machine.addBreakpoint(breakpoint);

        machine.load(Utils.getScriptFromResources("loop.bf").getBytes(), null, new ByteArrayOutputStream());
        machine.execute();
        assertEquals(48, machine.getState().getProgramCounter());
        assertArrayEquals(new byte[] {42, 2}, slice(machine.getState().getMemory(), 2));

        breakpoint.disable();
        machine.execute();
        assertEquals(51, machine.getState().getProgramCounter());
        assertArrayEquals(new byte[] {42, 0}, slice(machine.getState().getMemory(), 2));

        MachineMetrics metrics = machine.getMetrics();
        assertEquals(55, metrics.getInstructionsExecuted());
        assertEquals(0, metrics.getInstructionsSkipped());
        assertEquals(59, metrics.getProgramCounterChanges());
    }
}
