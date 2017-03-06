package db;

public enum Type {
    INT(true), FLOAT(true), STRING(false);

    private static final int INT_NAN_VALUE = 999;
    private static final Number INT_NAN = new Integer(INT_NAN_VALUE);
    private static final Number FLOAT_NAN = Double.NaN;

    private boolean isNumeric;

    Type(boolean isNumeric) {
        this.isNumeric = isNumeric;
    }

    public boolean isNumeric() {
        return this.isNumeric;
    }

    public boolean isNAN(Number n) {
        switch (this) {
            case INT:
                return n == INT_NAN;
            case FLOAT:
                return n == FLOAT_NAN;
            default:
                throw new IllegalArgumentException();
        }
    }

    public Number getNAN() {
        switch (this) {
            case INT:
                return INT_NAN;
            case FLOAT:
                return FLOAT_NAN;
            default:
                throw new IllegalArgumentException();
        }
    }

    public boolean isNOVALUE(Comparable c) {
        return c == null;
    }

    public Comparable getNOVALUE() {
        return null;
    }
}
