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

public class MachineMetrics {
    private final long instructionsExecuted;
    private final long instructionsSkipped;
    private final long programCounterChanges;

    public MachineMetrics(long instructionsExecuted, long instructionsSkipped, long programCounterChanges) {
        this.instructionsExecuted = instructionsExecuted;
        this.instructionsSkipped = instructionsSkipped;
        this.programCounterChanges = programCounterChanges;
    }

    public long getInstructionsExecuted() {
        return instructionsExecuted;
    }

    public long getInstructionsSkipped() {
        return instructionsSkipped;
    }

    public long getProgramCounterChanges() {
        return programCounterChanges;
    }

    @Override
    public String toString() {
        return "MachineMetrics{" +
                "instructionsExecuted=" + instructionsExecuted +
                ", instructionsSkipped=" + instructionsSkipped +
                ", programCounterChanges=" + programCounterChanges +
                '}';
    }
}
