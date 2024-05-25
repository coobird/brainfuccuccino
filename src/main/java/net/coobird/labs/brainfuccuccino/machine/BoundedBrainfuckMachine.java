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

package net.coobird.labs.brainfuccuccino.machine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// brainfuck interpreter with bounds checks on the memory cell values.
// memory cell spec: 8-bit signed byte with overflow protection.
// memory cell count: 30,000
// end-of-stream will write a 0 to the current memory cell on read.
public class BoundedBrainfuckMachine implements BrainfuckMachine {
    private static final int SIZE = 30000;
    private int programCounter = 0;
    private int dataPointer = 0;
    private final byte[] memory = new byte[SIZE];

    private boolean isDebug = false;

    private void printState(byte[] program) {
        if (isDebug) {
            System.out.printf("pc: %d   program[pc]: %c   dp: %d   memory[dp]: %d\n", programCounter, program[programCounter], dataPointer, memory[dataPointer]);
        }
    }

    public void evaluate(byte[] program, InputStream is, OutputStream os) throws IOException {
        while (programCounter < program.length) {
            printState(program);
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
                            if (program[programCounter] == '[') {
                                depth++;
                                continue;
                            } else if (program[programCounter] == ']') {
                                if (depth == 0) {
                                    programCounter--;
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
            }
            programCounter++;
        }
    }

    private void checkBounds(int programCounter) {
        if (programCounter < 0 || programCounter >= SIZE) {
            throw new ProgramRangeOutOfBoundsException(String.format("Out of bounds: <%s>", programCounter));
        }
    }

    private void incrementPosition() {
        checkBounds(++dataPointer);
    }

    private void decrementPosition() {
        checkBounds(--dataPointer);
    }

    private void incrementValue() {
        byte value = memory[dataPointer];
        if (value == Byte.MAX_VALUE) {
            throw new MemoryCellOverflowException(
                    String.format("Value <%s> overflow at <%s>", value, dataPointer)
            );
        }
        ++memory[dataPointer];
    }

    private void decrementValue() {
        byte value = memory[dataPointer];
        if (value == Byte.MIN_VALUE) {
            throw new MemoryCellOverflowException(
                    String.format("Value <%s> underflow at <%s>", value, dataPointer)
            );
        }
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
}
