package net.coobird.labs.brainfuccuccino.machine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A brainfuck machine with an abstract memory type.
 * The program is bounded (finite).
 * @param <T>   Type used by the memory cells in the brainfuck machine.
 */
public abstract class AbstractBoundedBrainfuckMachine<T> implements BrainfuckMachine {
    protected int programCounter = 0;
    protected int dataPointer = 0;
    protected final T[] memory = init();

    protected abstract T[] init();

    private boolean isDebug = false;

    private void printState(byte[] program) {
        if (isDebug) {
            System.out.printf("pc: %d   program[pc]: %c   dp: %d   memory[dp]: %d\n", programCounter, program[programCounter], dataPointer, memory[dataPointer]);
        }
    }

    /**
     * Write given value to the given {@link OutputStream}.
     */
    protected abstract void writeToOutputStream(OutputStream os, T value) throws IOException;

    /**
     * Reads a value from the given {@link InputStream}.
     */
    protected abstract T readFromInputStream(InputStream is) throws IOException;

    @Override
    public void evaluate(byte[] program, InputStream is, OutputStream os) throws IOException {
        while (programCounter < program.length) {
            printState(program);
            Instructions instruction = fetchInstruction(program);
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
                        printState(program);
                        loop:
                        while (true) {
                            programCounter++;
                            switch (fetchInstruction(program)) {
                                case BEGIN_LOOP:
                                    depth++;
                                    continue;
                                case END_LOOP:
                                    if (depth == 0) {
                                        programCounter--;
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
                        printState(program);
                        loop:
                        while (true) {
                            programCounter--;
                            switch (fetchInstruction(program)) {
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
            }
            programCounter++;
        }
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
     * Fetch an instruction from the program at the current program counter.
     */
    protected Instructions fetchInstruction(byte[] program) {
        return Instructions.getInstruction(program[programCounter]);
    }

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
}
