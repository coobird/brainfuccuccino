package net.coobird.labs.brainfuccuccino;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BrainfuccuccinoPerformanceTest {
    @Test
    public void dots() throws IOException {
        String program = Utils.getScriptFromResources("dots.bf");
        for (Flavor flavor : Flavor.values()) {
            long startTime = System.currentTimeMillis();
            for (int iteration = 0; iteration < 100000; iteration++) {
                Brainfuccuccino.customize()
                        .flavor(flavor)
                        .attach(new ByteArrayOutputStream())
                        .evaluate(program);
            }
            long duration = System.currentTimeMillis() - startTime;
            System.out.println(String.format("flavor: %s   duration: %s", flavor, duration));
        }
    }
}
