package db;

public class IntValue implements Value {
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

    public Object getValue() {
        return value;
    }
}
