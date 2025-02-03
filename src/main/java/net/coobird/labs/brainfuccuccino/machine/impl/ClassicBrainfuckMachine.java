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

import net.coobird.labs.brainfuccuccino.machine.BrainfuckMachine;
import net.coobird.labs.brainfuccuccino.machine.Instruction;
import net.coobird.labs.brainfuccuccino.machine.MemoryRangeOutOfBoundsException;
import net.coobird.labs.brainfuccuccino.machine.ProgramRangeOutOfBoundsException;
import net.coobird.labs.brainfuccuccino.machine.debug.Breakpoint;
import net.coobird.labs.brainfuccuccino.machine.debug.BreakpointManager;
import net.coobird.labs.brainfuccuccino.machine.debug.Debuggable;
import net.coobird.labs.brainfuccuccino.machine.state.Introspectable;
import net.coobird.labs.brainfuccuccino.machine.state.MachineMetrics;
import net.coobird.labs.brainfuccuccino.machine.state.MachineState;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A classic brainfuck interpreter.
 * Memory cells are signed bytes permitting over- and underflow.
 * Memory cells are bounded at 30000 cells.
 * End-of-stream will write a {@code 0} to the current memory cell on read.
 */
public class ClassicBrainfuckMachine
        implements BrainfuckMachine, Introspectable<Byte>, Debuggable {

    private static final int SIZE = 30000;
    private int programCounter = 0;
    private int dataPointer = 0;
    private final byte[] memory = new byte[SIZE];

    private long instructionsExecuted = 0;
    private long nopInstructions = 0;
    private long programCounterChanges = 0;

    protected BreakpointManager breakpointManager = new BreakpointManager();

    // These fields are populated for current evaluation.
    // By keeping these states, execution can be interrupted and resumed.
    private Instruction[] instructions;
    private byte[] program;
    private InputStream is;
    private OutputStream os;

    private boolean isDebug = false;

    private void printState(byte[] program) {
        if (isDebug) {
            System.out.printf("pc: %d   program[pc]: %c   dp: %d   memory[dp]: %d\n", programCounter, program[programCounter], dataPointer, memory[dataPointer]);
        }
    }

    protected boolean isComplete = false;

    @Override
    public boolean isInterrupted() {
        return breakpointManager.isInterrupted();
    }

    @Override
    public boolean isComplete() {
        return isComplete;
    }

    @Override
    public void execute() throws IOException {
        while (programCounter < program.length) {
            printState(program);

            if (breakpointManager.isBreakpoint(programCounter)) {
                return;
            }

            byte instruction = fetchInstruction(program);
            switch (instruction) {
                case '>':
                    incrementPosition();
                    break;
                case '<':
                    decrementPosition();
                    break;
                case '+':
                    incrementValue();
                    break;
                case '-':
                    decrementValue();
                    break;
                case '.':
                    os.write(outputValue());
                    break;
                case ',':
                    byte readByte = (byte) is.read();
                    inputValue(readByte == -1 ? 0 : readByte);
                    break;
                case '[':
                    if (readValue() == 0) {
                        int depth = 0;
                        printState(program);
                        while (true) {
                            programCounter++;
                            programCounterChanges++;
                            if (program[programCounter] == '[') {
                                depth++;
                                continue;
                            } else if (program[programCounter] == ']') {
                                if (depth == 0) {
                                    programCounter--;
                                    programCounterChanges++;
                                    break;
                                } else {
                                    depth--;
                                }
                            }
                            if (programCounter == program.length - 1) {
                                throw new ProgramRangeOutOfBoundsException("Couldn't find closing ']'");
                            }
                        }
                    }
                    break;
                case ']':
                    if (readValue() != 0) {
                        int depth = 0;
                        while (true) {
                            programCounter--;
                            programCounterChanges++;
                            printState(program);
                            if (program[programCounter] == ']') {
                                depth++;
                            } else if (program[programCounter] == '[') {
                                if (depth == 0) {
                                    break;
                                } else {
                                    depth--;
                                }
                            }
                            if (programCounter == 0) {
                                throw new ProgramRangeOutOfBoundsException("Couldn't find opening '['");
                            }
                        }
                    }
                    break;
                default:
                    nopInstructions++;
                    instructionsExecuted--;
            }
            instructionsExecuted++;
            programCounterChanges++;
            programCounter++;
        }
        isComplete = true;
    }

    @Override
    public void load(byte[] program, InputStream is, OutputStream os) {
        // Pre-translate program into instructions.
        // This will reduce interpretation time.
        this.program = program;
        this.is = is;
        this.os = os;
    }

    @Override
    public void evaluate(byte[] program, InputStream is, OutputStream os) throws IOException {
        load(program, is, os);
        execute();
    }

    private void checkBounds(int memoryPosition) {
        if (memoryPosition < 0 || memoryPosition >= SIZE) {
            throw new MemoryRangeOutOfBoundsException(String.format("Memory cell out of bounds: <%s>", memoryPosition));
        }
    }

    private void incrementPosition() {
        checkBounds(++dataPointer);
    }

    private void decrementPosition() {
        checkBounds(--dataPointer);
    }

    private void incrementValue() {
        ++memory[dataPointer];
    }

    private void decrementValue() {
        --memory[dataPointer];
    }

    private byte fetchInstruction(byte[] program) {
        return program[programCounter];
    }

    private byte readValue() {
        return memory[dataPointer];
    }

    private byte outputValue() {
        return memory[dataPointer];
    }

    private void inputValue(byte value) {
        memory[dataPointer] = value;
    }

    @Override
    public MachineState<Byte> getState() {
        Byte[] memoryCopy = new Byte[memory.length];
        for (int i = 0; i < memory.length; i++) {
            memoryCopy[i] = memory[i];
        }
        return new MachineState<>(programCounter, dataPointer, memoryCopy);
    }

    @Override
    public MachineMetrics getMetrics() {
        return new MachineMetrics(
                instructionsExecuted, nopInstructions, programCounterChanges
        );
    }

    @Override
    public void addBreakpoint(Breakpoint breakpoint) {
        breakpointManager.addBreakpoint(breakpoint);
    }

    @Override
    public void removeBreakpoint(Breakpoint breakpoint) {
        breakpointManager.removeBreakpoint(breakpoint);
    }

    @Override
    public String toString() {
        return "ClassicBrainfuckMachine{}";
    }
}
