package net.coobird.labs.brainfuccuccino;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BrainfuccuccinoTest {

    @Test
    public void printHelloWorld() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Brainfuccuccino.customize()
                .attach(baos)
                .evaluate(Utils.getScriptFromResources("hello_world.bf"));

        assertEquals("Hello World!\n", baos.toString());
    }

    @Test
    public void printHelloWorldSignedBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Brainfuccuccino.customize()
                .attach(baos)
                .evaluate(Utils.getScriptFromResources("signed_hello_world.bf"));

        assertEquals("Hello World!\n", baos.toString());
    }

    @Test
    public void loop() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Brainfuccuccino.customize()
                .attach(baos)
                .evaluate(Utils.getScriptFromResources("loop.bf"));

        assertEquals("*", baos.toString());
    }

    @Test
    public void nestedLoop() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Brainfuccuccino.customize()
                .attach(baos)
                .evaluate(Utils.getScriptFromResources("nested_loop.bf"));

        assertEquals("*", baos.toString());
    }

    @Test
    public void dots() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Brainfuccuccino.customize()
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

    @Test
    public void catInput() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Brainfuccuccino.customize()
                .attach(new ByteArrayInputStream("Hello World!".getBytes(StandardCharsets.US_ASCII)))
                .attach(baos)
                .evaluate(Utils.getScriptFromResources("cat.bf"));

        assertEquals("Hello World!", baos.toString());
    }

    @Test
    public void catInputUtf8() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Brainfuccuccino.customize()
                .attach(new ByteArrayInputStream("????????????????????????".getBytes(StandardCharsets.UTF_8)))
                .attach(baos)
                .evaluate(Utils.getScriptFromResources("cat.bf"));

        assertEquals("????????????????????????", baos.toString());
    }

    @Test
    public void brewTest() throws IOException {
        Brainfuccuccino.brew(Utils.getScriptFromResources("hello_world.bf"));
    }
}
