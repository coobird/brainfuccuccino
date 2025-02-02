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

package net.coobird.labs.brainfuccuccino.machine.debug;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface implemented by brainfuck machines that support debugging.
 * <p>
 * The lifecycle for debuggable brainfuck machines is:
 * <ol>
 *     <li>Load the program with the {@link #load(byte[], InputStream, OutputStream)} method, then</li>
 *     <li>Run the {@link #execute()} method to run the program.</li>
 * </ol>
 * When the {@code execute()} method returns, it indicates that the program
 * finished running or it was interrupted by a breakpoint.
 * The two situations can be checked using the {@link #isComplete()} and
 * {@link #isInterrupted()} methods, respectively.
 *
 */
public interface Debuggable {
    /**
     * Loads the brainfuck machine with the program to execute, along with the input and output.
     * @param program   The brainfuck program to execute.
     * @param is    An {@link InputStream} for inputs.
     * @param os    An {@link OutputStream} for outputs.
     */
    void load(byte[] program, InputStream is, OutputStream os);

    /**
     * Starts or resumes execution of the brainfuck machine.
     * @throws IOException  When an exception is thrown during execution.
     */
    void execute() throws IOException;

    /**
     * Indicates whether execution is currently interrupted by a breakpoint.
     * @return {@code true} if interrupted, {@code false} otherwise.
     */
    boolean isInterrupted();

    /**
     * Indicates whether program execution has completed.
     * @return {@code true} if completed, {@code false} otherwise.
     */
    boolean isComplete();
    
    /**
     * Adds a breakpoint.
     * @param breakpoint    A breakpoint.
     * @throws IllegalArgumentException When an address already has a breakpoint.
     */
    void addBreakpoint(Breakpoint breakpoint);

    /**
     * Removes a breakpoint.
     * @param breakpoint    The breakpoint to be removed from current breakpoints.
     * @throws IllegalArgumentException When the corresponding breakpoint cannot be found.
     */
    void removeBreakpoint(Breakpoint breakpoint);
}