package net.coobird.labs.brainfuccuccino.machine;

public interface ValueOverflowChecker<T> {

    T getMaximumValue();
    T getMinimumValue();

    default void checkLowerBound(T value, int dataPointer) {
        if (value == getMinimumValue()) {
            throw new MemoryCellOverflowException(
                    String.format("Value <%s> out of bounds at <%s>", value, dataPointer)
            );
        }
    }

    default void checkUpperBound(T value, int dataPointer) {
        if (value == getMaximumValue()) {
            throw new MemoryCellOverflowException(
                    String.format("Value <%s> out of bounds at <%s>", value, dataPointer)
            );
        }
    }
}
