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

package net.coobird.labs.brainfuccuccino.machine;

import net.coobird.labs.brainfuccuccino.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SignedByteBoundedBrainfuckMachineTest {

    @ParameterizedTest
    @ValueSource(strings = {"+.", ">+.", ">>+."})
    public void whenMemoryPositionWithinSizeThenCorrectValueInMemoryCell(String program) throws IOException {
        BrainfuckMachine machine = new SignedByteBoundedBrainfuckMachine(3);
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        machine.evaluate(program.getBytes(), null, os);

        byte[] actualOutput = os.toByteArray();
        assertEquals(1, actualOutput.length);
        assertEquals((byte) 1, actualOutput[0]);
    }

    @ParameterizedTest
    @ValueSource(strings = {"<", ">>>"})
    public void whenMemoryOutOfRangeThenExceptionThrown(String program) {
        BrainfuckMachine machine = new SignedByteBoundedBrainfuckMachine(3);
        assertThrows(
                MemoryRangeOutOfBoundsException.class,
                () -> machine.evaluate(program.getBytes(), null, null)
        );
    }

    @Test
    public void introspectionTest() throws IOException {
        SignedByteBoundedBrainfuckMachine machine = new SignedByteBoundedBrainfuckMachine(3);
        machine.evaluate("+>++>+++".getBytes(), null, null);

        Introspectable.MachineState<Byte> state = machine.getState();
        assertEquals(8, state.getProgramCounter());
        assertEquals(2, state.getDataPointer());
        assertArrayEquals(new Byte[] {1, 2, 3}, state.getMemory());
    }

    @DisabledIfSystemProperty(named = "java.awt.headless", matches = "true")
    @Test
    public void inspectionByGUI() throws Exception {
        final JFrame frame = new JFrame();
        final JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setLineWrap(true);
        frame.setLayout(new BorderLayout());
        frame.add(textArea, BorderLayout.CENTER);
        frame.setSize(400, 300);
        frame.setLocationByPlatform(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        final SignedByteBoundedBrainfuckMachine machine = new SignedByteBoundedBrainfuckMachine(10);

        AtomicBoolean isRunning = new AtomicBoolean(true);
        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            long lastCall = 0;
            long lastInstructionsExecuted = 0;
            long lastProgramCounterChanges = 0;
            AtomicLong maxInstructionsPerSec = new AtomicLong(0);
            AtomicLong maxProgramCounterChangesPerSec = new AtomicLong(0);

            while (isRunning.get()) {
                long now = System.currentTimeMillis();
                if (now - lastCall > 50) {
                    final Introspectable.MachineState<Byte> state = machine.getState();
                    final ExecutionStatistics statistics = machine.getStatistics();
                    final double elapsedTime = (double)(now - lastCall);

                    /*
                     * These rates will be inaccurate for running programs that
                     * execution within 50 milliseconds.
                     */
                    double instructionsPerSec =
                            (statistics.getInstructionsExecuted() - lastInstructionsExecuted) * (1000 / elapsedTime);
                    double programCounterChangesPerSec =
                            (statistics.getProgramCounterChanges() - lastProgramCounterChanges) * (1000 / elapsedTime);

                    // Store the double as bits in the integer, as AtomicDouble is not provided.
                    maxInstructionsPerSec.set(
                            Double.doubleToLongBits(
                                    Math.max(
                                            Double.longBitsToDouble(maxInstructionsPerSec.get()),
                                            instructionsPerSec
                                    )
                            )
                    );
                    maxProgramCounterChangesPerSec.set(
                            Double.doubleToLongBits(
                                    Math.max(
                                            Double.longBitsToDouble(maxProgramCounterChangesPerSec.get()),
                                            programCounterChangesPerSec
                                    )
                            )
                    );

                    SwingUtilities.invokeLater(() -> {
                        String s = String.format("currentTimestamp: %s%n", now);
                        s += String.format("instructionsExecuted: %,d%n", statistics.getInstructionsExecuted());
                        s += String.format("instructionsSkipped: %,d%n", statistics.getInstructionsSkipped());
                        s += String.format("programCounterChanges: %,d%n", statistics.getProgramCounterChanges());
                        s += String.format("memory: %s%n", Arrays.toString(state.getMemory()));
                        s += "\n";
                        s += String.format("instructionsPerSec: %,.1f%n", instructionsPerSec);
                        s += String.format("programCounterChangesPerSec: %,.1f%n", programCounterChangesPerSec);
                        s += String.format(
                                "max(instructionsPerSec): %,.1f%n",
                                Double.longBitsToDouble(maxInstructionsPerSec.get())
                        );
                        s += String.format(
                                "max(programCounterChangesPerSec): %,.1f%n",
                                Double.longBitsToDouble(maxProgramCounterChangesPerSec.get())
                        );
                        s += String.format("elapsedTime: %,.3f sec%n", (now - startTime) / 1000.0);
                        textArea.setText(s);
                    });
                    lastInstructionsExecuted = statistics.getInstructionsExecuted();
                    lastProgramCounterChanges = statistics.getProgramCounterChanges();
                    lastCall = now;
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        machine.evaluate(Utils.getScriptFromResources("dots.bf").getBytes(), null, System.out);

        // Just to enable seeing the JFrame
        Thread.sleep(5000);
        isRunning.set(false);
    }
}