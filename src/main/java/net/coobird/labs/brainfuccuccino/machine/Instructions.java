package net.coobird.labs.brainfuccuccino.machine;

public enum Instructions {
    INCREMENT_POINTER,
    DECREMENT_POINTER,
    INCREMENT_VALUE,
    DECREMENT_VALUE,
    OUTPUT_VALUE,
    INPUT_VALUE,
    BEGIN_LOOP,
    END_LOOP,
    NOP;

    public static Instructions getInstruction(byte value) {
        switch (value) {
            case '>':
                return INCREMENT_POINTER;
            case '<':
                return DECREMENT_POINTER;
            case '+':
                return INCREMENT_VALUE;
            case '-':
                return DECREMENT_VALUE;
            case '.':
                return OUTPUT_VALUE;
            case ',':
                return INPUT_VALUE;
            case '[':
                return BEGIN_LOOP;
            case ']':
                return END_LOOP;
            default:
                return NOP;
        }
    }
}
