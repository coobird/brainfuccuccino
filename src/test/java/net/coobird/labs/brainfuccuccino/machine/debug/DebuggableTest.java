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

import net.coobird.labs.brainfuccuccino.Utils;
import net.coobird.labs.brainfuccuccino.machine.impl.ClassicBrainfuckMachine;
import net.coobird.labs.brainfuccuccino.machine.impl.SignedByteBrainfuckMachine;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DebuggableTest {
    public static Stream<Arguments> debuggableMachines() {
        return Stream.of(
                Arguments.of(new SignedByteBrainfuckMachine()),
                Arguments.of(new ClassicBrainfuckMachine())
        );
    }

    @ParameterizedTest
    @MethodSource("debuggableMachines")
    public void breakpointTest(Debuggable machine) throws IOException {
        machine.addBreakpoint(new Breakpoint(2, true));
        machine.addBreakpoint(new Breakpoint(6, true));

        machine.load("+>++>+++".getBytes(), null, null);
        machine.execute();

        machine.execute();
        assertTrue(machine.isInterrupted());
        assertFalse(machine.isComplete());

        machine.execute();
        assertFalse(machine.isInterrupted());
        assertTrue(machine.isComplete());
    }

    @ParameterizedTest
    @MethodSource("debuggableMachines")
    public void breakpointOnAndOffTest(Debuggable machine) throws IOException {
        Breakpoint breakpoint = new Breakpoint(48, true);
        machine.addBreakpoint(breakpoint);

        machine.load(Utils.getScriptFromResources("loop.bf").getBytes(), null, new ByteArrayOutputStream());
        machine.execute();
        assertTrue(machine.isInterrupted());
        assertFalse(machine.isComplete());

        breakpoint.disable();
        machine.execute();
        assertFalse(machine.isInterrupted());
        assertTrue(machine.isComplete());
    }

    @ParameterizedTest
    @MethodSource("debuggableMachines")
    public void breakpointAddAndRemoveTest(Debuggable machine) throws IOException {
        Breakpoint breakpoint = new Breakpoint(48, true);
        machine.addBreakpoint(breakpoint);

        machine.load(Utils.getScriptFromResources("loop.bf").getBytes(), null, new ByteArrayOutputStream());
        machine.execute();
        assertTrue(machine.isInterrupted());
        assertFalse(machine.isComplete());

        machine.removeBreakpoint(breakpoint);
        machine.execute();
        assertFalse(machine.isInterrupted());
        assertTrue(machine.isComplete());
    }

    @ParameterizedTest
    @MethodSource("debuggableMachines")
    public void addRemoveBreakpointTest(Debuggable machine) throws IOException {
        Breakpoint breakpoint = new Breakpoint(2, true);
        machine.addBreakpoint(breakpoint);
        machine.removeBreakpoint(breakpoint);

        machine.load(Utils.getScriptFromResources("loop.bf").getBytes(), null, new ByteArrayOutputStream());
        machine.execute();
        assertFalse(machine.isInterrupted());
        assertTrue(machine.isComplete());
    }

    @ParameterizedTest
    @MethodSource("debuggableMachines")
    public void addDuplicateBreakpoint(Debuggable machine) throws IOException {
        Breakpoint breakpoint = new Breakpoint(1, true);
        machine.addBreakpoint(breakpoint);
        assertThrows(
                IllegalArgumentException.class,
                () -> machine.addBreakpoint(breakpoint)
        );
    }

    @ParameterizedTest
    @MethodSource("debuggableMachines")
    public void addDuplicateBreakpointAddress(Debuggable machine) throws IOException {
        machine.addBreakpoint(new Breakpoint(1, true));
        assertThrows(
                IllegalArgumentException.class,
                () -> machine.addBreakpoint(new Breakpoint(1, false))
        );
    }

    @ParameterizedTest
    @MethodSource("debuggableMachines")
    public void removeNonexistentBreakpoint(Debuggable machine) throws IOException {
        Breakpoint breakpoint = new Breakpoint(1, true);
        assertThrows(
                IllegalArgumentException.class,
                () -> machine.removeBreakpoint(breakpoint)
        );
    }
}
