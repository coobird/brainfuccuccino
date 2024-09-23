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

/**
 * This package provides the
 * <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/scripting/prog_guide/api.html">Java Scripting API (JSR 223)</a>
 * support for Brainfuccuccino.
 * <p>
 * An example of running Hello World program using the Java Scripting API:
 *
 * <blockquote><pre>
 * ScriptEngine bfScriptEngine = new ScriptEngineManager().getEngineByName("brainfuccucino");
 * ScriptContext context = bfScriptEngine.getContext();
 *
 * // Set up a writer for the brainfuck program to output to.
 * Writer writer = new StringWriter();
 * context.setWriter(writer);
 *
 * // Hello World program from https://esolangs.org/wiki/Brainfuck (CC0 public domain)
 * bfScriptEngine.eval(
 *         "++++++++[&gt;++++[&gt;++&gt;+++&gt;+++&gt;+&lt;&lt;&lt;&lt;-]&gt;+&gt;+&gt;-&gt;&gt;+[&lt;]&lt;-]&gt;&gt;.&gt;" +
 *         "---.+++++++..+++.&gt;&gt;.&lt;-.&lt;.+++.------.--------.&gt;&gt;+.&gt;++.",
 *         context
 * );
 *
 * // Prints "Hello World!"
 * System.out.println(writer.toString());
 * </pre></blockquote>
 */
package net.coobird.labs.brainfuccuccino.engine;

