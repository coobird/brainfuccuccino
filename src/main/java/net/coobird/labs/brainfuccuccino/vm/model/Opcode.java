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

package net.coobird.labs.brainfuccuccino.vm.model;

/**
 * Opcodes for the {@link net.coobird.labs.brainfuccuccino.vm.BrainfuckVirtualMachine}.
 */
public enum Opcode {
    /**
     * Adds the operand value from the current memory cell.
     */
    ADD,
    /**
     * Subtracts the operand value from the current memory cell.
     */
    SUB,
    /**
     * Jump to operand location when the current memory cell value is zero.
     */
    JMZ,
    /**
     * Jump to operand location when the current memory cell value is non-zero.
     */
    JMN,
    /**
     * Move memory cell pointer forward by operand locations.
     */
    MADD,
    /**
     * Move memory cell pointer backward by operand locations.
     */
    MSUB,
    /**
     * Read a value from the input port and stores in the current memory cell.
     * An {@link Instruction} with this opcode will ignore the operand.
     */
    READ,
    /**
     * Write a value from the current memory cell to the output port.
     * An {@link Instruction} with this opcode will ignore the operand.
     */
    WRITE,
}
