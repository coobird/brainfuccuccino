package net.coobird.labs.brainfuccuccino;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Functionality tests for different flavors of Brainfuck machines include in Brainfuccuccino.
 */
public class BrainfuccuccinoFlavorTest {

    @ParameterizedTest
    @EnumSource(Flavor.class)
    public void printHelloWorld(Flavor flavor) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Brainfuccuccino.customize()
                .flavor(flavor)
                .attach(baos)
                .evaluate(Utils.getScriptFromResources("hello_world.bf"));

        assertEquals("Hello World!\n", baos.toString());
    }

    @ParameterizedTest
    @EnumSource(Flavor.class)
    public void printHelloWorldSignedBytes(Flavor flavor) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Brainfuccuccino.customize()
                .flavor(flavor)
                .attach(baos)
                .evaluate(Utils.getScriptFromResources("signed_hello_world.bf"));

        assertEquals("Hello World!\n", baos.toString());
    }

    @ParameterizedTest
    @EnumSource(Flavor.class)
    public void loop(Flavor flavor) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Brainfuccuccino.customize()
                .flavor(flavor)
                .attach(baos)
                .evaluate(Utils.getScriptFromResources("loop.bf"));

        assertEquals("*", baos.toString());
    }

    @ParameterizedTest
    @EnumSource(Flavor.class)
    public void nestedLoop(Flavor flavor) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Brainfuccuccino.customize()
                .flavor(flavor)
                .attach(baos)
                .evaluate(Utils.getScriptFromResources("nested_loop.bf"));

        assertEquals("*", baos.toString());
    }

    @ParameterizedTest
    @EnumSource(Flavor.class)
    public void dots(Flavor flavor) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Brainfuccuccino.customize()
                .flavor(flavor)
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

    @ParameterizedTest
    @EnumSource(Flavor.class)
    public void catInput(Flavor flavor) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Brainfuccuccino.customize()
                .flavor(flavor)
                .attach(new ByteArrayInputStream("Hello World!".getBytes(StandardCharsets.US_ASCII)))
                .attach(baos)
                .evaluate(Utils.getScriptFromResources("cat.bf"));

        assertEquals("Hello World!", baos.toString());
    }

    @ParameterizedTest
    @EnumSource(Flavor.class)
    public void catInputUtf8(Flavor flavor) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Brainfuccuccino.customize()
                .flavor(flavor)
                .attach(new ByteArrayInputStream("こんにちは世界！".getBytes(StandardCharsets.UTF_8)))
                .attach(baos)
                .evaluate(Utils.getScriptFromResources("cat.bf"));

        assertEquals("こんにちは世界！", baos.toString());
    }
}
