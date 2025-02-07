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

package net.coobird.labs.brainfuccuccino.vm;

import net.coobird.labs.brainfuccuccino.machine.ProgramRangeOutOfBoundsException;
import net.coobird.labs.brainfuccuccino.vm.model.Instruction;
import net.coobird.labs.brainfuccuccino.vm.model.Opcode;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A brainfuck compiler for the {@link BrainfuckVirtualMachine}.
 * <p>
 * Optimized instructions for the {@code BrainfuckVirtualMachine} can be
 * emitted by specifying a positive optimization level when calling the
 * {@link #compile(String, int)} method.
 */
public final class BrainfuckVirtualMachineCompiler {
    private static final int UNKNOWN_ADDRESS = -1;

    private static List<Instruction> verifyInstructions(List<Instruction> instructions) {
        for (Instruction instruction : instructions) {
            if (instruction.getOpcode() == Opcode.JMZ && instruction.getOperand() < 0) {
                throw new ProgramRangeOutOfBoundsException("Couldn't find closing ']'");
            }
        }
        return instructions;
    }

    /**
     * Compiles a brainfuck program for the {@code BrainfuckVirtualMachine}
     * without optimization.
     * @param program   The brainfuck program to compile.
     * @return  The instructions for the {@code BrainfuckVirtualMachine}.
     */
    public List<Instruction> compile(String program) {
        return compile(program, 0);
    }

    /**
     * Compiles a brainfuck program for the {@code BrainfuckVirtualMachine}
     * with specified level of optimization.
     * @param program   The brainfuck program to compile.
     * @param optimizationLevel The optimization level.
     *                          {@code 0} for no optimization, and higher
     *                          optimization for higher values.
     *                          Must be a non-negative value.
     * @return  The instructions for the {@code BrainfuckVirtualMachine}.
     */
    public List<Instruction> compile(String program, int optimizationLevel) {
        if (optimizationLevel == 0) {
            return verifyInstructions(compileWithoutOptimization(program));
        } else if (optimizationLevel > 0) {
            return verifyInstructions(compileWithOptimization(program));
        } else {
            throw new IllegalArgumentException("Optimization level must be a positive value.");
        }
    }

    private static List<Instruction> compileWithoutOptimization(String program) {
        int address = 0;
        // A stack used to find matching loop construct. Uses LinkedList as an implementation of Stack.
        Deque<Integer> returnAddressStack = new LinkedList<>();
        // List of instructions. The address of the instruction is the position in the list.
        List<Instruction> instructions = new ArrayList<>();

        for (char bfInstruction : program.toCharArray()) {
            switch (bfInstruction) {
                case '>':
                    instructions.add(new Instruction(Opcode.MADD, 1));
                    address++;
                    break;
                case '<':
                    instructions.add(new Instruction(Opcode.MSUB, 1));
                    address++;
                    break;
                case '+':
                    instructions.add(new Instruction(Opcode.ADD, 1));
                    address++;
                    break;
                case '-':
                    instructions.add(new Instruction(Opcode.SUB, 1));
                    address++;
                    break;
                case '.':
                    instructions.add(new Instruction(Opcode.WRITE));
                    address++;
                    break;
                case ',':
                    instructions.add(new Instruction(Opcode.READ));
                    address++;
                    break;
                case '[':
                    instructions.add(new Instruction(Opcode.JMZ, UNKNOWN_ADDRESS));
                    returnAddressStack.push(address);
                    address++;
                    break;
                case ']':
                    if (returnAddressStack.isEmpty()) {
                        throw new ProgramRangeOutOfBoundsException("Couldn't find opening '['");
                    }
                    int matchingOpening = returnAddressStack.pop();
                    instructions.add(new Instruction(Opcode.JMN, matchingOpening));
                    Instruction matchingInstruction = instructions.get(matchingOpening);
                    matchingInstruction.setOperand(address);
                    address++;
                    break;
                default:
                    // Any unrecognized character is ignored.
                    break;
            }
        }
        return instructions;
    }

    private static class RepeatedCharactersIterable implements Iterable<String> {
        private final String s;

        private RepeatedCharactersIterable(String s) {
            this.s = s;
        }

        @Override
        public Iterator<String> iterator() {
            return new Iterator<String>() {
                private String nextSplit;
                private int start = 0;
                private final char[] chars = s.toCharArray();

                private void findNext() {
                    if (start == Integer.MAX_VALUE){
                        nextSplit = null;
                        return;
                    }

                    for (int i = start; i < chars.length - 1; i++) {
                        if (chars[i] != chars[i + 1]) {
                            nextSplit = s.substring(start, i + 1);
                            start = i + 1;
                            return;
                        }
                    }

                    nextSplit = s.substring(start);
                    start = Integer.MAX_VALUE;
                }

                @Override
                public boolean hasNext() {
                    findNext();
                    return nextSplit != null;
                }

                @Override
                public String next() {
                    return nextSplit;
                }
            };
        }
    }

    private static List<Instruction> compileWithOptimization(String program) {
        int address = 0;
        // A stack used to find matching loop construct. Uses LinkedList as an implementation of Stack.
        Deque<Integer> returnAddressStack = new LinkedList<>();
        // List of instructions. The address of the instruction is the position in the list.
        List<Instruction> instructions = new ArrayList<>();

        for (String split : new RepeatedCharactersIterable(program)) {
            char bfInstruction = split.charAt(0);
            int length = split.length();

            switch (bfInstruction) {
                case '>':
                    instructions.add(new Instruction(Opcode.MADD, length));
                    address++;
                    break;
                case '<':
                    instructions.add(new Instruction(Opcode.MSUB, length));
                    address++;
                    break;
                case '+':
                    instructions.add(new Instruction(Opcode.ADD, length));
                    address++;
                    break;
                case '-':
                    instructions.add(new Instruction(Opcode.SUB, length));
                    address++;
                    break;
                case '.':
                    for (int i = 0; i < length; i++) {
                        instructions.add(new Instruction(Opcode.WRITE));
                        address++;
                    }
                    break;
                case ',':
                    for (int i = 0; i < length; i++) {
                        instructions.add(new Instruction(Opcode.READ));
                        address++;
                    }
                    break;
                case '[':
                    for (int i = 0; i < length; i++) {
                        instructions.add(new Instruction(Opcode.JMZ, UNKNOWN_ADDRESS));
                        returnAddressStack.push(address);
                        address++;
                    }
                    break;
                case ']':
                    for (int i = 0; i < length; i++) {
                        if (returnAddressStack.isEmpty()) {
                            throw new ProgramRangeOutOfBoundsException("Couldn't find opening '['");
                        }
                        int matchingOpening = returnAddressStack.pop();
                        instructions.add(new Instruction(Opcode.JMN, matchingOpening));
                        Instruction matchingInstruction = instructions.get(matchingOpening);
                        matchingInstruction.setOperand(address);
                        address++;
                    }
                    break;
                default:
                    // Any unrecognized character is ignored.
                    break;
            }
        }
        return instructions;
    }
}
