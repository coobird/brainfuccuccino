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

import net.coobird.labs.brainfuccuccino.machine.state.Introspectable;
import net.coobird.labs.brainfuccuccino.machine.state.MachineMetrics;
import net.coobird.labs.brainfuccuccino.machine.state.MachineState;
import net.coobird.labs.brainfuccuccino.vm.model.Instruction;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * A brainfuck virtual machine for Brainfuccuccino.
 * <p>
 * This virtual machine consists of eight opcodes defined in {@link net.coobird.labs.brainfuccuccino.vm.model.Opcode}.
 * Unlike a regular brainfuck machine, it accepts operands to improve code density.
 * This allows the virtual machine to execute faster by reducing necessary state changes.
 * <p>
 * Additionally, the virtual machine does not provide a direct analogue to brainfuck's {@code [} and {@code ]} instructions.
 * Rather, it provides classical jump instructions ({@link net.coobird.labs.brainfuccuccino.vm.model.Opcode#JMZ} and {@link net.coobird.labs.brainfuccuccino.vm.model.Opcode#JMN}) to move the program counter to specific locations in the program memory.
 * Therefore, a compiler must determine jump locations ahead of time.
 * <p>
 * The machine's memory cells are {@code byte}s and consist of an array of 30,000 elements.
 * <p>
 * For input and output, a byte of data will be exchanged via {@link InputStream} and {@link OutputStream}, respectively.
 */
public class BrainfuckVirtualMachine implements Introspectable<Byte> {
    private static final int SIZE = 30000;
    private int programCounter = 0;
    private int dataPointer = 0;
    private final byte[] memory = new byte[SIZE];

    private long instructionsExecuted = 0;
    private long programCounterChanges = 0;

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
            instructionsExecuted++;
            int operand = instruction.getOperand();
            switch (instruction.getOpcode()) {
                case MADD:
                    dataPointer += operand;
                    programCounter++;
                    programCounterChanges++;
                    break;
                case MSUB:
                    dataPointer -= operand;
                    programCounter++;
                    programCounterChanges++;
                    break;
                case ADD:
                    memory[dataPointer] += operand;
                    programCounter++;
                    programCounterChanges++;
                    break;
                case SUB:
                    memory[dataPointer] -= operand;
                    programCounter++;
                    programCounterChanges++;
                    break;
                case READ:
                    byte inData = (byte) is.read();
                    if (inData == -1) {
                        inData = 0;
                    }
                    memory[dataPointer] = inData;
                    programCounter++;
                    programCounterChanges++;
                    break;
                case WRITE:
                    byte outData = memory[dataPointer];
                    os.write(outData);
                    programCounter++;
                    programCounterChanges++;
                    break;
                case JMN:
                    if (memory[dataPointer] != 0) {
                        programCounter = operand;
                        programCounterChanges++;
                        break;
                    }
                    programCounter++;
                    programCounterChanges++;
                    break;
                case JMZ:
                    if (memory[dataPointer] == 0) {
                        programCounter = operand;
                        programCounterChanges++;
                        break;
                    }
                    programCounter++;
                    programCounterChanges++;
                    break;
                default:
                    throw new IllegalStateException("Unknown instruction: " + instruction);
            }
        }
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
                instructionsExecuted, 0, programCounterChanges
        );
    }
}
