package db;

public class IntValue implements Value<Integer> {
    private final int value;

    public IntValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public boolean equals(Object other) {
        return ((IntValue) other).value == value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
