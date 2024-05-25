/**
 * This package provides the
 * <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/scripting/prog_guide/api.html">Java Scripting API (JSR 223)</a>
 * support for Brainfuccuccino.
 * <p/>
 * An example of running Hello World program using the Java Scripting API:
 *
 * <p><blockquote><pre>
 * ScriptEngine bfScriptEngine = new ScriptEngineManager().getEngineByName("brainfuccucino");
 * ScriptContext context = bfScriptEngine.getContext();
 *
 * // Set up a writer for the brainfuck program to output to.
 * Writer writer = new StringWriter();
 * context.setWriter(writer);
 *
 * // Hello World program from https://esolangs.org/wiki/Brainfuck (CC0 public domain)
 * bfScriptEngine.eval(
 *         "++++++++[>++++[>++>+++>+++>+<<<<-]>+>+>->>+[<]<-]>>.>" +
 *         "---.+++++++..+++.>>.<-.<.+++.------.--------.>>+.>++.",
 *         context
 * );
 *
 * // Prints "Hello World!"
 * System.out.println(writer.toString());
 * </pre></blockquote></p>
 */
package net.coobird.labs.brainfuccuccino.engine;

