package net.coobird.labs.brainfuccuccino.machine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Instructions {
    INCREMENT_POINTER('>'),
    DECREMENT_POINTER('<'),
    INCREMENT_VALUE('+'),
    DECREMENT_VALUE('-'),
    OUTPUT_VALUE('.'),
    INPUT_VALUE(','),
    BEGIN_LOOP('['),
    END_LOOP(']'),
    NOP('\0');

    private final byte value;

    Instructions(char character) {
        this.value = (byte) character;
    }

    private final static Map<Byte, Instructions> INSTRUCTION_MAPPING;

    static {
        Map<Byte, Instructions> mapping = new HashMap<>();
        for (Instructions instruction : Instructions.values()) {
            mapping.put(instruction.value, instruction);
        }
        INSTRUCTION_MAPPING = Collections.unmodifiableMap(mapping);
    }

    public static Instructions getInstruction(byte value) {
        Instructions instruction = INSTRUCTION_MAPPING.get(value);
        return instruction != null ? instruction : NOP;
    }
}
