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
