package net.coobird.labs.brainfuccuccino.engine;

import net.coobird.labs.brainfuccuccino.Constants;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// https://docs.oracle.com/javase/8/docs/api/javax/script/package-summary.html

public class BrainfuckScriptEngineFactory implements ScriptEngineFactory {

    @Override
    public String getEngineName() {
        return String.format("%s Scripting Engine", Constants.ENGINE_NAME);
    }

    @Override
    public String getEngineVersion() {
        return Constants.ENGINE_VERSION;
    }

    @Override
    public List<String> getExtensions() {
        return Collections.unmodifiableList(Arrays.asList("b", "bf"));
    }

    // https://stackoverflow.com/questions/24233073/what-is-the-correct-mime-type-for-esoteric-languages
    @Override
    public List<String> getMimeTypes() {
        return Collections.unmodifiableList(
                Arrays.asList("text/X-brainfuck", "application/X-brainfuck")
        );
    }

    @Override
    public List<String> getNames() {
        return Collections.singletonList(Constants.ENGINE_NAME);
    }

    @Override
    public String getLanguageName() {
        return "Brainfuck";
    }

    @Override
    public String getLanguageVersion() {
        return "1.0";
    }

    @Override
    public Object getParameter(String key) {
        // For meaning of keys, see the javadocs of the interface.
        switch (key) {
            case "ScriptEngine.ENGINE":
            case "ScriptEngine.NAME":
                return getEngineName();
            case "ScriptEngine.ENGINE_VERSION":
                return getEngineVersion();
            case "ScriptEngine.LANGUAGE":
                return getLanguageName();
            case "ScriptEngine.LANGUAGE_VERSION":
                return getLanguageVersion();
            case "THREADING":
                return null; // No threading guarantees yet.
            default:
                return null;
        }
    }

    @Override
    public String getMethodCallSyntax(String obj, String m, String... args) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getOutputStatement(String toDisplay) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getProgram(String... statements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ScriptEngine getScriptEngine() {
        return new BrainfuckScriptEngine();
    }
}
