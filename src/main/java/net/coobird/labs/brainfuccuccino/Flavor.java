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

package net.coobird.labs.brainfuccuccino;

public enum Flavor {
    /**
     * Uses a classic brainfuck interpreter implementation.
     * @see net.coobird.labs.brainfuccuccino.machine.impl.ClassicBrainfuckMachine
     */
    REGULAR,
    /**
     * Uses an interpreter that supports both negative and positive numbers with value bounds checks.
     * @see net.coobird.labs.brainfuccuccino.machine.impl.SignedByteBrainfuckMachine
     */
    CAFE_AU_LAIT,
    /**
     * Uses an implementation that executes compute-heavy programs much faster.
     * @see net.coobird.labs.brainfuccuccino.vm.BrainfuckVirtualMachine
     */
    INSTANT,
}
