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

import net.coobird.labs.brainfuccuccino.engine.BrainfuckScriptEngine;
import org.junit.jupiter.api.Test;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ScriptEngineTest {

    @Test
    public void findsEngineByName() {
        ScriptEngine bfScriptEngine = new ScriptEngineManager().getEngineByName("brainfuccucino");
        assertNotNull(bfScriptEngine);
        assertEquals(BrainfuckScriptEngine.class, bfScriptEngine.getClass());
    }

    @Test
    public void findsEngineByBfExtension() {
        ScriptEngine bfScriptEngine = new ScriptEngineManager().getEngineByExtension("bf");
        assertNotNull(bfScriptEngine);
        assertEquals(BrainfuckScriptEngine.class, bfScriptEngine.getClass());
    }

    @Test
    public void findsEngineByBExtension() {
        ScriptEngine bfScriptEngine = new ScriptEngineManager().getEngineByExtension("b");
        assertNotNull(bfScriptEngine);
        assertEquals(BrainfuckScriptEngine.class, bfScriptEngine.getClass());
    }

    @Test
    public void findsEngineByTextMimeType() {
        ScriptEngine bfScriptEngine = new ScriptEngineManager().getEngineByMimeType("text/X-brainfuck");
        assertNotNull(bfScriptEngine);
        assertEquals(BrainfuckScriptEngine.class, bfScriptEngine.getClass());
    }

    @Test
    public void findsEngineByApplicationMimeType() {
        ScriptEngine bfScriptEngine = new ScriptEngineManager().getEngineByMimeType("application/X-brainfuck");
        assertNotNull(bfScriptEngine);
        assertEquals(BrainfuckScriptEngine.class, bfScriptEngine.getClass());
    }

    @Test
    public void printHelloWorld() throws ScriptException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (Writer writer = new OutputStreamWriter(baos)) {
            ScriptEngine bfScriptEngine = new ScriptEngineManager().getEngineByName("brainfuccucino");
            ScriptContext context = bfScriptEngine.getContext();
            context.setWriter(writer);

            bfScriptEngine.eval(Utils.getScriptFromResources("hello_world.bf"), context);
            assertEquals("Hello World!\n", new String(baos.toByteArray(), StandardCharsets.US_ASCII));
        }
    }

    @Test
    public void catInput() throws ScriptException, IOException {
        String text = "Hello!";
        ByteArrayInputStream bais = new ByteArrayInputStream(text.getBytes(StandardCharsets.US_ASCII));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (Reader reader = new InputStreamReader(bais); Writer writer = new OutputStreamWriter(baos)) {
            ScriptEngine bfScriptEngine = new ScriptEngineManager().getEngineByName("brainfuccucino");
            ScriptContext context = bfScriptEngine.getContext();
            context.setReader(reader);
            context.setWriter(writer);

            bfScriptEngine.eval(Utils.getScriptFromResources("cat.bf"), context);
            assertEquals(text, new String(baos.toByteArray(), StandardCharsets.US_ASCII));
        }
    }
}
