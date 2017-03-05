package db;

public class FloatValue implements Value<Double> {
    private static final double PRECISION = 1e-3;
    private static final double TOLERANCE = 1e-12;

    private final double value;

    public FloatValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%.3f", value);
    }

    @Override
    public boolean equals(Object other) {
        return Math.abs(((FloatValue) other).value - value) < TOLERANCE;
    }

    public Double getValue() {
        return value;
    }
}
