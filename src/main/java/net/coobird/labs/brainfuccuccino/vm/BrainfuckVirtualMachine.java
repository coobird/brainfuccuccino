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

package net.coobird.labs.brainfuccuccino.vm;

import net.coobird.labs.brainfuccuccino.vm.model.Instruction;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class BrainfuckVirtualMachine {
    private static final int SIZE = 30000;
    private int programCounter = 0;
    private int dataPointer = 0;
    private final byte[] memory = new byte[SIZE];

    private final Instruction[] instructions;
    private final InputStream is;
    private final OutputStream os;

    public BrainfuckVirtualMachine(List<Instruction> instructions, InputStream is, OutputStream os) {
        this(instructions.toArray(new Instruction[0]), is, os);
    }

    public BrainfuckVirtualMachine(Instruction[] instructions, InputStream is, OutputStream os) {
        this.instructions = instructions;
        this.is = is;
        this.os = os;
    }

    /*
     * instructions
     * add N  - add N to the current memory cell
     * sub N  - subtract N from the current memory cell
     * jmz A  - jump to address A if current memory cell is zero
     * jmn A  - jump to address A if current memory cell is nonzero
     * madd N - increment memory address pointer by N
     * msub N - decrement memory address pointer by N
     * read   - read from input port into current memory cell
     * write  - write value of current memory cell to output port
     */

    public void execute() throws IOException {
        while (programCounter < instructions.length) {
            Instruction instruction = instructions[programCounter];
            int operand = instruction.getOperand();
            switch (instruction.getOpcode()) {
                case MADD:
                    dataPointer += operand;
                    programCounter++;
                    break;
                case MSUB:
                    dataPointer -= operand;
                    programCounter++;
                    break;
                case ADD:
                    memory[dataPointer] += operand;
                    programCounter++;
                    break;
                case SUB:
                    memory[dataPointer] -= operand;
                    programCounter++;
                    break;
                case READ:
                    byte inData = (byte) is.read();
                    if (inData == -1) {
                        inData = 0;
                    }
                    memory[dataPointer] = inData;
                    programCounter++;
                    break;
                case WRITE:
                    byte outData = memory[dataPointer];
                    os.write(outData);
                    programCounter++;
                    break;
                case JMN:
                    if (memory[dataPointer] != 0) {
                        programCounter = operand;
                        break;
                    }
                    programCounter++;
                    break;
                case JMZ:
                    if (memory[dataPointer] == 0) {
                        programCounter = operand;
                        break;
                    }
                    programCounter++;
                    break;
                default:
                    throw new IllegalStateException("Unknown instruction: " + instruction);
            }
        }
    }
}
