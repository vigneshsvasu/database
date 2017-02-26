package db;

class FloatValue implements Value {
    private static final double PRECISION = 1e-3;

    private final double value;

    FloatValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        //
    }
}
