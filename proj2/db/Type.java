package db;

import static db.DatabaseException.InvalidSyntaxException;

public enum Type {
    INT(true), FLOAT(true), STRING(false);

    private static final int INT_NAN_VALUE = Integer.MAX_VALUE;  // Not cached
    private static final Integer INT_NAN = new Integer(INT_NAN_VALUE);
    private static final Double FLOAT_NAN = Double.NaN;

    public static final String NOVALUE_REPR = "NOVALUE";
    public static final String NAN_REPR = "NAN";

    private boolean isNumeric;

    Type(boolean isNumeric) {
        this.isNumeric = isNumeric;
    }

    public boolean isNumeric() {
        return this.isNumeric;
    }

    public boolean isNAN(Comparable n) {
        switch (this) {
            case INT:
                return n == INT_NAN;
            case FLOAT:
                return n == FLOAT_NAN;
            default:
                return false;
        }
    }

    public Comparable getNAN() throws InvalidSyntaxException {
        switch (this) {
            case INT:
                return INT_NAN;
            case FLOAT:
                return FLOAT_NAN;
            default:
                throw new InvalidSyntaxException("NAN only available for numeric types");
        }
    }

    public boolean isNOVALUE(Comparable value) {
        return value == null;
    }

    public Comparable getNOVALUE() {
        return null;
    }

    public String repr(Comparable value) {
        if (isNOVALUE(value)) {
            return NOVALUE_REPR;
        } else if (isNAN(value)) {
            return NAN_REPR;
        }

        switch (this) {
            case INT:
                return ((Integer) value).toString();
            case FLOAT:
                return String.format("%.3f", (Double) value);
            case STRING:
                return '\'' + value.toString() + '\'';
            default:
                throw new IllegalArgumentException();
        }
    }

    public Comparable zeroValue() {
        switch (this) {
            case INT:
                return 0;
            case FLOAT:
                return 0.0;
            case STRING:
                return "";
            default:
                throw new IllegalArgumentException();
        }
    }
}
