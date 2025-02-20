# Brainfuccuccino - brainfuck scripting engine for Java

Brainfuccuccino is a Java scripting engine which allows [brainfuck][1] programs to be embedded and run in Java applications.
It conforms to the [Java Scripting API][2] (JSR 223).

## Usage

### Using Brainfuccuccino via the Java Scripting API

One key feature of Brainfuccuccino is that it conforms to the [Java Scripting API][2] (JSR 223).
This allows using the `ScriptEngine` interface to execute brainfuck programs:

```java
ScriptEngine bfScriptEngine = new ScriptEngineManager().getEngineByName("brainfuccucino");
ScriptContext context = bfScriptEngine.getContext();

// Set up a writer for the brainfuck program to output to.
Writer writer = new StringWriter();
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

### Using Brainfuccuccino directly

Another straightforward option to run brainfuck programs is to call Brainfuccuccino directly.

The following is a full example of `cat` or a program that echoes back standard input to standard output.

```java
import java.io.IOException;

public class Cat {
    public static void main(String[] args) throws IOException {
        // Exit by sending an end-of-transmission (Ctrl-D) or terminating the application.
        Brainfuccuccino.brew(",[.,]");
    }
}
```

`Brainfuccuccino.brew` attaches `System.in` and `System.out` to the input and output (respectively) of the brainfuck program automatically.
This allows quick experimentation with brainfuck programs that involve I/O. 


[1]: https://en.wikipedia.org/wiki/Brainfuck
[2]: https://docs.oracle.com/javase/8/docs/technotes/guides/scripting/prog_guide/api.html

## Requirements

Java 8 or higher.

## Maven

Brainfuccuccino is not yet available on Maven Central.
Currently, it is only available as SNAPSHOT artifacts on OSSRH repository provided by Sonatype.

_NOTE: The SNAPSHOT artifacts will likely not be provided once the first release artifact is available._

It takes two modifications in your application's `pom.xml` to use the SNAPSHOT artifacts:

First, add the following to get SNAPSHOT artifacts from the OSSRH repository:

```xml
<repositories>
  <repository>
    <id>ossrh-snapshot</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    <releases>
      <enabled>false</enabled>
    </releases>  
    <snapshots>
      <enabled>true</enabled>
      <updatePolicy>daily</updatePolicy>
    </snapshots>
  </repository>
</repositories>
```

Second, add the following the `<dependencies>` section of the POM:

```xml
<dependency>
  <groupId>net.coobird.labs.brainfuccuccino</groupId>
  <artifactId>brainfuccuccino</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## Disclaimer

_Brainfuccuccino is very early in its development.
The APIs are subject to change at any time._

## License

Brainfuccuccino is released under the MIT License.
