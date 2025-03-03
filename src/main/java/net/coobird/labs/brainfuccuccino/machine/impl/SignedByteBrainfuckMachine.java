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

import net.coobird.labs.brainfuccuccino.machine.MemoryCellOverflowException;
import net.coobird.labs.brainfuccuccino.machine.MemoryRangeOutOfBoundsException;
import net.coobird.labs.brainfuccuccino.machine.state.Introspectable;
import net.coobird.labs.brainfuccuccino.machine.state.MachineMetrics;
import net.coobird.labs.brainfuccuccino.machine.state.MachineState;
import net.coobird.labs.brainfuccuccino.machine.state.MachineStateListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * A bounded brainfuck machine that uses a signed byte as the memory cell type.
 */
public class SignedByteBrainfuckMachine
        extends AbstractBrainfuckMachine<Byte>
        implements Introspectable<Byte> {

    private static final int DEFAULT_SIZE = 30000;
    private final int memorySize;

    public SignedByteBrainfuckMachine() {
        this(DEFAULT_SIZE);
    }

    public SignedByteBrainfuckMachine(int memorySize) {
        super(init(memorySize));
        this.memorySize = memorySize;
    }

    public SignedByteBrainfuckMachine(MachineStateListener<Byte> listener) {
        this(DEFAULT_SIZE, listener);
    }

    public SignedByteBrainfuckMachine(int memorySize, MachineStateListener<Byte> listener) {
        super(init(memorySize), listener);
        this.memorySize = memorySize;
    }

    private static Byte[] init(int memorySize) {
        Byte[] memory = new Byte[memorySize];
        Arrays.fill(memory, (byte) 0);
        return memory;
    }

    @Override
    protected void writeToOutputStream(OutputStream os, Byte value) throws IOException {
        os.write(getValueFromMemory());
    }

    @Override
    protected Byte readFromInputStream(InputStream is) throws IOException {
        byte readByte = (byte) is.read();
        return readByte == -1 ? 0 : readByte;
    }

    private void checkBounds(int memoryPosition) {
        if (memoryPosition < 0 || memoryPosition >= this.memorySize) {
            throw new MemoryRangeOutOfBoundsException(
                    String.format(
                            "Memory cell out of bounds: <%s>",
                            memoryPosition
                    )
            );
        }
    }

    @Override
    protected void incrementPosition() {
        checkBounds(++dataPointer);
    }

    @Override
    protected void decrementPosition() {
        checkBounds(--dataPointer);
    }

    @Override
    protected void incrementValue() {
        byte value = memory[dataPointer];
        if (value == Byte.MAX_VALUE) {
            throw new MemoryCellOverflowException(
                    String.format(
                            "Value <%s> out of bounds at <%s>",
                            value, dataPointer
                    )
            );
        }
        ++memory[dataPointer];
    }

    @Override
    protected void decrementValue() {
        byte value = memory[dataPointer];
        if (value == Byte.MIN_VALUE) {
            throw new MemoryCellOverflowException(
                    String.format(
                            "Value <%s> out of bounds at <%s>",
                            value, dataPointer
                    )
            );
        }
        --memory[dataPointer];
    }

    @Override
    protected boolean isCurrentMemoryValueZero() {
        return memory[dataPointer] == 0;
    }

    @Override
    public MachineState<Byte> getState() {
        return new MachineState<>(programCounter, dataPointer, memory);
    }

    @Override
    public MachineMetrics getMetrics() {
        return new MachineMetrics(
                instructionsExecuted, nopInstructions, programCounterChanges
        );
    }

    @Override
    public String toString() {
        return "SignedByteBrainfuckMachine{" +
                "memorySize=" + memorySize +
                '}';
    }
}
