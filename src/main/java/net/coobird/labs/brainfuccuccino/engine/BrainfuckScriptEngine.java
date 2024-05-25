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

package net.coobird.labs.brainfuccuccino.engine;

import net.coobird.labs.brainfuccuccino.Brainfuccuccino;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;

public class BrainfuckScriptEngine extends AbstractScriptEngine {
    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        try {
            Reader contextReader = context.getReader();
            Writer contextWriter = context.getWriter();

            InputStream is = new InputStream() {
                @Override
                public int read() throws IOException {
                    int b = contextReader.read();
                    return b == -1 ? 0 : b;
                }
            };
            OutputStream os = new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    contextWriter.write(b);
                }
            };

            Brainfuccuccino.customize()
                    .attach(is)
                    .attach(os)
                    .evaluate(script);

            contextWriter.flush();

        } catch (IOException e) {
            throw new ScriptException(e);
        }

        return 0;
    }

    @Override
    public Object eval(Reader reader, ScriptContext context) throws ScriptException {
        StringBuilder programBuffer = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                programBuffer.append(line);
            }
        } catch (IOException e) {
            throw new ScriptException(e);
        }

        return eval(programBuffer.toString(), context);
    }

    @Override
    public Bindings createBindings() {
        // Bindings won't accept anything.
        return new SimpleBindings(Collections.emptyMap());
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return new BrainfuckScriptEngineFactory();
    }
}
