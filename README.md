# Brainfuccuccino - brainfuck scripting engine for Java

Brainfuccuccino is a Java scripting engine which allows [brainfuck][1] programs to be embedded and run in Java applications.
It conforms to the [Java Scripting API][2] (JSR 223).

# Usage

## Using Brainfuccuccino via the Java Scripting API

One key feature of Brainfuccuccino is that it conforms to the [Java Scripting API][2] (JSR 223).
This allows using the `ScriptEngine` interface to execute brainfuck programs:

```java
Writer writer = new StringWriter();
ScriptEngine bfScriptEngine = new ScriptEngineManager().getEngineByName("brainfuccucino");
ScriptContext context = bfScriptEngine.getContext();
context.setWriter(writer);

// Hello World program from https://esolangs.org/wiki/Brainfuck (CC0 public domain)
bfScriptEngine.eval(
        "++++++++[>++++[>++>+++>+++>+<<<<-]>+>+>->>+[<]<-]>>.>" +
        "---.+++++++..+++.>>.<-.<.+++.------.--------.>>+.>++.",
        context
);

// Prints "Hello World!"
System.out.println(writer.toString());
```

## Using Brainfuccuccino directly

Another straightforward option to run brainfuck programs is to call Brainfuccuccino directly.

The following is a `cat` or a program that echoes back standard input to standard output.

```java
Brainfuccuccino.brew(",[.,]");
```

`Brainfuccuccino.brew` attaches `System.in` and `System.out` to the input and output (respectively) of the brainfuck program automatically.
This allows quick experimentation with brainfuck programs that involve I/O. 


[1]: https://en.wikipedia.org/wiki/Brainfuck
[2]: https://docs.oracle.com/javase/8/docs/technotes/guides/scripting/prog_guide/api.html
