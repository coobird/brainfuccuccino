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

import java.util.Objects;

/**
 * A breakpoint used to interrupt/suspend program execution.
 * <p>
 * When implementing a brainfuck machine that's capable of debugging, this
 * class should be used in conjunction with the {@link BreakpointManager}.
 * <p>
 * A note about breakpoint equality:
 * A breakpoint is considered equivalent when the address it is associated
 * with is the same. Whether it is enabled or not is not considered.
 */
public class Breakpoint {
    private final int address;
    private boolean isEnabled = false;

    /**
     * Instantiates a breakpoint at an address, and specify whether it is
     * enabled or not.
     * @param address   Address to associate with the breakpoint.
     * @param isEnabled Whether to enable the breakpoint.
     */
    public Breakpoint(int address, boolean isEnabled) {
        this.address = address;
        this.isEnabled = isEnabled;
    }

    /**
     * Address for this breakpoint.
     * @return  Address for this breakpoint.
     */
    public int getAddress() {
        return address;
    }

    /**
     * Indicates whether this breakpoint is enabled or not.
     * <p>
     * If enabled, the brainfuck machine should interrupt execution at the
     * address associated with this breakpoint.
     *
     * @return  {@code true} when the breakpoint is enabled, {@code false}
     * otherwise.
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Enables this breakpoint.
     */
    public void enable() {
        this.isEnabled = true;
    }

    /**
     * Disables this breakpoint.
     */
    public void disable() {
        this.isEnabled = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Breakpoint that = (Breakpoint) o;
        return address == that.address;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(address);
    }

    @Override
    public String toString() {
        return "Breakpoint{" +
                "address=" + address +
                ", isEnabled=" + isEnabled +
                '}';
    }
}
