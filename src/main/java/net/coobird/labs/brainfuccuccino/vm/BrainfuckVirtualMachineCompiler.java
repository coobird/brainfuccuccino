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
import net.coobird.labs.brainfuccuccino.vm.model.Opcode;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class BrainfuckVirtualMachineCompiler {
    private static final int UNKNOWN_ADDRESS = -1;

    public List<Instruction> compile(String program) {
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
                    int matchingOpening = returnAddressStack.pop();
                    instructions.add(new Instruction(Opcode.JMN, matchingOpening));
                    Instruction matchingInstruction = instructions.get(matchingOpening);
                    matchingInstruction.setOperand(address);
                    address++;
                    break;
                default:
                    break;
            }
        }
        return instructions;
    }
}
