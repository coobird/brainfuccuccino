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

import net.coobird.labs.brainfuccuccino.machine.ProgramRangeOutOfBoundsException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Functionality tests for different flavors of Brainfuck machines include in Brainfuccuccino.
 */
public class BrainfuccuccinoFlavorTest {

    @ParameterizedTest
    @EnumSource(Flavor.class)
    public void printHelloWorld(Flavor flavor) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Brainfuccuccino.customize()
                .flavor(flavor)
                .attach(baos)
                .evaluate(Utils.getScriptFromResources("hello_world.bf"));

        assertEquals("Hello World!\n", baos.toString());
    }

    @ParameterizedTest
    @EnumSource(Flavor.class)
    public void printHelloWorldSignedBytes(Flavor flavor) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Brainfuccuccino.customize()
                .flavor(flavor)
                .attach(baos)
                .evaluate(Utils.getScriptFromResources("signed_hello_world.bf"));

        assertEquals("Hello World!\n", baos.toString());
    }

    @ParameterizedTest
    @EnumSource(Flavor.class)
    public void loop(Flavor flavor) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Brainfuccuccino.customize()
                .flavor(flavor)
                .attach(baos)
                .evaluate(Utils.getScriptFromResources("loop.bf"));

        assertEquals("*", baos.toString());
    }

    @ParameterizedTest
    @EnumSource(Flavor.class)
    public void nestedLoop(Flavor flavor) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Brainfuccuccino.customize()
                .flavor(flavor)
                .attach(baos)
                .evaluate(Utils.getScriptFromResources("nested_loop.bf"));

        assertEquals("*", baos.toString());
    }

    @ParameterizedTest
    @EnumSource(Flavor.class)
    public void dots(Flavor flavor) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Brainfuccuccino.customize()
                .flavor(flavor)
                .attach(baos)
                .evaluate(Utils.getScriptFromResources("dots.bf"));

        StringBuilder expectedBuilder = new StringBuilder();
        for (int lines = 0; lines < 10; lines++) {
            for (int width = 0; width < 16; width++) {
                expectedBuilder.append('*');
            }
            expectedBuilder.append('\n');
        }

        assertEquals(expectedBuilder.toString(), baos.toString());
    }

    @ParameterizedTest
    @EnumSource(Flavor.class)
    public void catInput(Flavor flavor) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Brainfuccuccino.customize()
                .flavor(flavor)
                .attach(new ByteArrayInputStream("Hello World!".getBytes(StandardCharsets.US_ASCII)))
                .attach(baos)
                .evaluate(Utils.getScriptFromResources("cat.bf"));

        assertEquals("Hello World!", baos.toString());
    }

    @ParameterizedTest
    @EnumSource(Flavor.class)
    public void catInputUtf8(Flavor flavor) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Brainfuccuccino.customize()
                .flavor(flavor)
                .attach(new ByteArrayInputStream("こんにちは世界！".getBytes(StandardCharsets.UTF_8)))
                .attach(baos)
                .evaluate(Utils.getScriptFromResources("cat.bf"));

        assertEquals("こんにちは世界！", baos.toString());
    }

    @ParameterizedTest
    @EnumSource(Flavor.class)
    public void matchingBeginLoopIsMissing(Flavor flavor) {
        assertThrows(
                ProgramRangeOutOfBoundsException.class,
                () -> Brainfuccuccino.customize()
                        .flavor(flavor)
                        .evaluate("+]")
        );
    }
    @ParameterizedTest
    @EnumSource(Flavor.class)
    public void matchingEndLoopIsMissing(Flavor flavor) throws IOException {
        assertThrows(
                ProgramRangeOutOfBoundsException.class,
                () -> Brainfuccuccino.customize()
                        .flavor(flavor)
                        .evaluate("[+")
        );
    }
}
