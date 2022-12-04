package net.coobird.labs.brainfuccuccino;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utils {
    public static String getScriptFromResources(String name) {
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(name);
        if (is == null) {
            throw new IllegalStateException("Resource not found: " + name);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder buffer = new StringBuilder();
            while (reader.ready()) {
                buffer.append(reader.readLine());
            }
            return buffer.toString();

        } catch (IOException e) {
            throw new RuntimeException("Exception thrown while reading resource: " + name, e);
        }
    }
}
