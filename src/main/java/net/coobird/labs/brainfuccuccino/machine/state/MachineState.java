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

package net.coobird.labs.brainfuccuccino.machine.state;

import java.util.Arrays;

/**
 * The brainfuck machine state.
 * <p>
 * If the memory cell is mutable, the memory cell contents is not
 * guaranteed to be the same as when the state object was created.
 * <p>
 * This class does not implement {@link #hashCode()} and
 * {@link #equals(Object)}, so they cannot be used for comparisons.
 *
 * @param <T> Type of the memory cell.
 */
public class MachineState<T> {
    private final int programCounter;
    private final int dataPointer;
    private final T[] memory;

    public MachineState(int programCounter, int dataPointer, T[] memory) {
        this.programCounter = programCounter;
        this.dataPointer = dataPointer;
        this.memory = Arrays.copyOf(memory, memory.length);
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public int getDataPointer() {
        return dataPointer;
    }

    public T[] getMemory() {
        return memory;
    }

    @Override
    public String toString() {
        return "MachineState{" +
                "programCounter=" + programCounter +
                ", dataPointer=" + dataPointer +
                ", memory=" + Arrays.toString(memory) +
                '}';
    }
}
