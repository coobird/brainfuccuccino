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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class tracks enabled/disabled {@link Breakpoint}s and returns whether
 * a given address has an active breakpoint through the {@link #isBreakpoint}
 * method to aid brainfuck machines to determine whether to interrupt program
 * execution.
 * <p>
 * Each instance of a brainfuck machine must use a separate instance of this
 * manager.
 */
public final class BreakpointManager {
    private Set<Breakpoint> breakpoints = new HashSet<>();
    private Map<Integer, Breakpoint> breakpointAddresses;
    private boolean isInterrupted = false;

    /**
     * Returns whether an address has an active breakpoint.
     * @param address   The address to check the breakpoint on.
     * @return  {@code true} if an active breakpoint is present,
     *          {@code false} otherwise.
     */
    public boolean isBreakpoint(int address) {
        breakpointCheck:
        // Prevent adversely affecting execution speed by keeping initial check as light as possible.
        if (!breakpoints.isEmpty() && breakpointAddresses.containsKey(address)) {
            Breakpoint breakpoint = breakpointAddresses.get(address);
            if (!breakpoint.isEnabled()) {
                break breakpointCheck;
            }

            if (!isInterrupted) {
                isInterrupted = true;
                return true;
            }
        }
        isInterrupted = false;
        return false;
    }

    /**
     * Returns whether the program execution has been interrupted and stopped.
     * To resume execution, {@link Debuggable#execute()} should be called.
     * @return  {@code true} when interrupted, {@code false} otherwise.
     */
    public boolean isInterrupted() {
        return isInterrupted;
    }

    private void updateBreakpointAddresses() {
        breakpointAddresses = breakpoints.stream()
                .collect(Collectors.toMap(Breakpoint::getAddress, Function.identity()));
    }

    /**
     * Adds a breakpoint.
     * @param breakpoint    A breakpoint.
     * @throws IllegalArgumentException When an address already has a breakpoint.
     */
    public void addBreakpoint(Breakpoint breakpoint) {
        if (breakpoints.contains(breakpoint)) {
            throw new IllegalArgumentException(
                    String.format("Breakpoint already exists: %s", breakpoint)
            );
        }
        breakpoints.add(breakpoint);
        updateBreakpointAddresses();
    }

    /**
     * Removes a breakpoint.
     * @param breakpoint    The breakpoint to be removed from current breakpoints.
     * @throws IllegalArgumentException When the corresponding breakpoint cannot be found.
     */
    public void removeBreakpoint(Breakpoint breakpoint) {
        if (!breakpoints.contains(breakpoint)) {
            throw new IllegalArgumentException(
                    String.format("Cannot find breakpoint: %s", breakpoint)
            );
        }
        breakpoints.remove(breakpoint);
        updateBreakpointAddresses();
    }
}
