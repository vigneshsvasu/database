package db;

class IntValue implements Value {
    private final int value;

    IntValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
