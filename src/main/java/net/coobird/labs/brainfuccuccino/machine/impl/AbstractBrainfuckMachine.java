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
import net.coobird.labs.brainfuccuccino.machine.debug.Breakpoint;
import net.coobird.labs.brainfuccuccino.machine.debug.Debuggable;
import net.coobird.labs.brainfuccuccino.machine.state.MachineStateListener;
import net.coobird.labs.brainfuccuccino.machine.ProgramRangeOutOfBoundsException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A brainfuck machine with an abstract memory type.
 * The program is bounded (finite).
 * @param <T>   Type used by the memory cells in the brainfuck machine.
 */
public abstract class AbstractBrainfuckMachine<T> implements BrainfuckMachine, Debuggable {
    protected int programCounter = 0;
    protected int dataPointer = 0;
    protected final T[] memory;
    private final MachineStateListener<T> listener;

    protected long instructionsExecuted = 0;
    protected long nopInstructions = 0;
    protected long programCounterChanges = 0;

    protected Set<Breakpoint> breakpoints = new HashSet<>();
    protected Map<Integer, Breakpoint> breakpointAddresses;

    // These fields are populated for current evaluation.
    // By keeping these states, execution can be interrupted and resumed.
    private Instruction[] instructions;
    private byte[] program;
    private InputStream is;
    private OutputStream os;

    protected AbstractBrainfuckMachine(T[] memory) {
        this(memory, null);
    }

    protected AbstractBrainfuckMachine(T[] memory, MachineStateListener<T> listener) {
        this.memory = memory;
        this.listener = listener;
    }

    /**
     * Write given value to the given {@link OutputStream}.
     */
    protected abstract void writeToOutputStream(OutputStream os, T value) throws IOException;

    /**
     * Reads a value from the given {@link InputStream}.
     */
    protected abstract T readFromInputStream(InputStream is) throws IOException;

    /**
     * Translate a byte-based program into {@link Instruction}s.
     */
    protected Instruction[] bytesToInstructions(byte[] program) {
        Instruction[] instructions = new Instruction[program.length];
        for (int i = 0; i < program.length; i++) {
            instructions[i] = Instruction.getInstruction(program[i]);
        }
        return instructions;
    }

    protected boolean isInterrupted = false;
    protected boolean isComplete = false;

    @Override
    public boolean isInterrupted() {
        return isInterrupted;
    }

    @Override
    public boolean isComplete() {
        return isComplete;
    }

    @Override
    public void resume() throws IOException {
        if (isComplete) {
            throw new IllegalStateException("Execution already complete.");
        }

        while (programCounter < instructions.length) {
            breakpointCheck:
            // Prevent adversely affecting execution speed by keeping initial check as light as possible.
            if (!breakpoints.isEmpty() && breakpointAddresses.containsKey(programCounter)) {
                Breakpoint breakpoint = breakpointAddresses.get(programCounter);
                if (!breakpoint.isEnabled()) {
                    break breakpointCheck;
                }

                if (!isInterrupted) {
                    isInterrupted = true;
                    return;
                }
            }
            isInterrupted = false;

            Instruction instruction = instructions[programCounter];
            if (listener != null) {
                listener.nextInstruction(
                        programCounter,
                        program[programCounter],
                        instruction,
                        dataPointer,
                        memory[dataPointer]
                );
            }
            switch (instruction) {
                case INCREMENT_POINTER:
                    incrementPosition();
                    break;
                case DECREMENT_POINTER:
                    decrementPosition();
                    break;
                case INCREMENT_VALUE:
                    incrementValue();
                    break;
                case DECREMENT_VALUE:
                    decrementValue();
                    break;
                case OUTPUT_VALUE:
                    writeToOutputStream(os, getValueFromMemory());
                    break;
                case INPUT_VALUE:
                    setValueToMemory(readFromInputStream(is));
                    break;
                case BEGIN_LOOP:
                    if (isCurrentMemoryValueZero()) {
                        int depth = 0;
                        loop:
                        while (true) {
                            programCounter++;
                            programCounterChanges++;
                            switch (instructions[programCounter]) {
                                case BEGIN_LOOP:
                                    depth++;
                                    continue;
                                case END_LOOP:
                                    if (depth == 0) {
                                        programCounter--;
                                        programCounterChanges++;
                                        break loop;
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
                case END_LOOP:
                    if (!isCurrentMemoryValueZero()) {
                        int depth = 0;
                        loop:
                        while (true) {
                            programCounter--;
                            programCounterChanges++;
                            switch (instructions[programCounter]) {
                                case END_LOOP:
                                    depth++;
                                    continue;
                                case BEGIN_LOOP:
                                    if (depth == 0) {
                                        break loop;
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
    public void evaluate(byte[] program, InputStream is, OutputStream os) throws IOException {
        // Pre-translate program into instructions.
        // This will reduce interpretation time.
        instructions = bytesToInstructions(program);
        this.program = program;
        this.is = is;
        this.os = os;
        resume();
    }

    /**
     * Increment position in the memory.
     */
    protected abstract void incrementPosition();

    /**
     * Decrement position in the memory.
     */
    protected abstract void decrementPosition();

    /**
     * Increment value in the current memory cell.
     */
    protected abstract void incrementValue();

    /**
     * Decrement value in the current memory cell.
     */
    protected abstract void decrementValue();

    /**
     * Returns whether the current memory cell contains zero.
     * Generally used to evaluate conditions for loops.
     * @return {@code true} if current memory cell is zero, {@code false} otherwise.
     */
    protected abstract boolean isCurrentMemoryValueZero();

    /**
     * Gets value from current memory position.
     */
    protected T getValueFromMemory() {
        return memory[dataPointer];
    }

    /**
     * Sets value at current memory position.
     */
    protected void setValueToMemory(T value) {
        memory[dataPointer] = value;
    }

    private void updateBreakpointAddresses() {
        breakpointAddresses = breakpoints.stream()
                .collect(Collectors.toMap(Breakpoint::getAddress, Function.identity()));
    }

    @Override
    public void addBreakpoint(Breakpoint breakpoint) {
        if (breakpoints.contains(breakpoint)) {
            throw new IllegalArgumentException("Breakpoint already exists.");
        }
        breakpoints.add(breakpoint);
        updateBreakpointAddresses();
    }

    @Override
    public void removeBreakpoint(Breakpoint breakpoint) {
        if (!breakpoints.contains(breakpoint)) {
            throw new IllegalArgumentException(String.format("Cannot find breakpoint: %s", breakpoint));
        }
        breakpoints.remove(breakpoint);
        updateBreakpointAddresses();
    }
}
