package net.coobird.labs.brainfuccuccino.machine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class SignedByteBoundedBrainfuckMachine extends AbstractBoundedBrainfuckMachine<Byte> {
    private static final int SIZE = 30000;

    @Override
    protected Byte[] init() {
        Byte[] memory = new Byte[SIZE];
        for (int i = 0; i < SIZE; i++) {
            memory[i] = (byte) 0;
        }
        return memory;
    }

    @Override
    protected void writeToOutputStream(OutputStream os, Byte value) throws IOException {
        os.write(getValueFromMemory());
    }

    @Override
    protected Byte readFromInputStream(InputStream is) throws IOException {
        byte readByte = (byte) is.read();
        return readByte == -1 ? 0 : readByte;
    }

    private void checkBounds(int programCounter) {
        if (programCounter < 0 || programCounter >= SIZE) {
            throw new ProgramRangeOutOfBoundsException(String.format("Out of bounds: <%s>", programCounter));
        }
    }

    @Override
    protected void incrementPosition() {
        checkBounds(++dataPointer);
    }

    @Override
    protected void decrementPosition() {
        checkBounds(--dataPointer);
    }

    @Override
    protected void incrementValue() {
        byte value = memory[dataPointer];
        if (value == Byte.MAX_VALUE) {
            throw new MemoryCellOverflowException(
                    String.format("Value <%s> out of bounds at <%s>", value, dataPointer)
            );
        }
        ++memory[dataPointer];
    }

    @Override
    protected void decrementValue() {
        byte value = memory[dataPointer];
        if (value == Byte.MIN_VALUE) {
            throw new MemoryCellOverflowException(
                    String.format("Value <%s> out of bounds at <%s>", value, dataPointer)
            );
        }
        --memory[dataPointer];
    }

    @Override
    protected boolean isCurrentMemoryValueZero() {
        return memory[dataPointer] == 0;
    }
}
