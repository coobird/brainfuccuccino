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
