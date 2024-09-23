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

package net.coobird.labs.brainfuccuccino.machine.impl;

import net.coobird.labs.brainfuccuccino.machine.BrainfuckMachine;
import net.coobird.labs.brainfuccuccino.machine.MemoryRangeOutOfBoundsException;
import net.coobird.labs.brainfuccuccino.machine.state.MachineMetrics;
import net.coobird.labs.brainfuccuccino.machine.state.MachineState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SignedByteBrainfuckMachineTest {

    @ParameterizedTest
    @ValueSource(strings = {"+.", ">+.", ">>+."})
    public void whenMemoryPositionWithinSizeThenCorrectValueInMemoryCell(String program) throws IOException {
        BrainfuckMachine machine = new SignedByteBrainfuckMachine(3);
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        machine.evaluate(program.getBytes(), null, os);

        byte[] actualOutput = os.toByteArray();
        assertEquals(1, actualOutput.length);
        assertEquals((byte) 1, actualOutput[0]);
    }

    @ParameterizedTest
    @ValueSource(strings = {"<", ">>>"})
    public void whenMemoryOutOfRangeThenExceptionThrown(String program) {
        BrainfuckMachine machine = new SignedByteBrainfuckMachine(3);
        assertThrows(
                MemoryRangeOutOfBoundsException.class,
                () -> machine.evaluate(program.getBytes(), null, null)
        );
    }

    @Test
    public void introspectionTest() throws IOException {
        SignedByteBrainfuckMachine machine = new SignedByteBrainfuckMachine(3);
        machine.evaluate("+>++>+++".getBytes(), null, null);

        MachineState<Byte> state = machine.getState();
        assertEquals(8, state.getProgramCounter());
        assertEquals(2, state.getDataPointer());
        assertArrayEquals(new Byte[] {1, 2, 3}, state.getMemory());
    }

    @Test
    public void inspectionTest() throws IOException {
        SignedByteBrainfuckMachine machine = new SignedByteBrainfuckMachine(3);
        machine.evaluate("+>++>+++".getBytes(), null, null);

        MachineMetrics metrics = machine.getMetrics();
        assertEquals(8, metrics.getInstructionsExecuted());
        assertEquals(0, metrics.getInstructionsSkipped());
        assertEquals(8, metrics.getProgramCounterChanges());
    }
}