package db.values;

public class FloatValue implements Value {
    private static final double PRECISION = 1e-3;

    private final double value;

    public FloatValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "";
    }

    public double getValue() {
        return value;
    }
}
