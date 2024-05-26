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

package net.coobird.labs.brainfuccuccino;

import net.coobird.labs.brainfuccuccino.vm.BrainfuckVirtualMachine;
import net.coobird.labs.brainfuccuccino.vm.BrainfuckVirtualMachineCompiler;
import net.coobird.labs.brainfuccuccino.vm.model.Instruction;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A performance test for the different "flavors" of the Brainfuck machines include in Brainfuccuccino.
 */
public class BrainfuccuccinoPerformanceTest {
    @Test
    public void dots() throws IOException {
        String program = Utils.getScriptFromResources("dots.bf");
        for (Flavor flavor : Flavor.values()) {
            long startTime = System.currentTimeMillis();
            for (int iteration = 0; iteration < 100000; iteration++) {
                Brainfuccuccino.customize()
                        .flavor(flavor)
                        .attach(new ByteArrayOutputStream())
                        .evaluate(program);
            }
            long duration = System.currentTimeMillis() - startTime;
            System.out.println(String.format("flavor: %s   duration: %s", flavor, duration));
        }
    }

    @Test
    public void dotsVirtualMachine() throws IOException {
        String program = Utils.getScriptFromResources("dots.bf");

        long startTime = System.currentTimeMillis();
        Instruction[] instructions = new BrainfuckVirtualMachineCompiler().compile(program).toArray(new Instruction[0]);
        for (int iteration = 0; iteration < 100000; iteration++) {
            new BrainfuckVirtualMachine(
                    instructions,
                    null,
                    new ByteArrayOutputStream()
            ).execute();
        }
        long duration = System.currentTimeMillis() - startTime;
        System.out.println(String.format("bvm duration: %s", duration));
    }
}
