package net.coobird.labs.brainfuccuccino.machine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class SignedByteBoundedBrainfuckMachine extends AbstractBoundedBrainfuckMachine<Byte>
        implements ValueOverflowChecker<Byte> {

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
    public Byte getMinimumValue() {
        return Byte.MIN_VALUE;
    }

    @Override
    public Byte getMaximumValue() {
        return Byte.MAX_VALUE;
    }

    @Override
    protected void incrementValue() {
        checkUpperBound(memory[dataPointer], dataPointer);
        ++memory[dataPointer];
    }

    @Override
    protected void decrementValue() {
        checkLowerBound(memory[dataPointer], dataPointer);
        --memory[dataPointer];
    }

    @Override
    protected boolean isCurrentMemoryValueZero() {
        return memory[dataPointer] == 0;
    }
}
