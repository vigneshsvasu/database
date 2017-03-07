package db;

public enum Type {
    INT(true), FLOAT(true), STRING(false);

    private final Integer INT_NaN = new Integer(Integer.MAX_VALUE);  // Not cached
    private final Double FLOAT_NaN = new Double(Double.MAX_VALUE);
    private final Comparable NOVALUE = null;

    public static final String NaN_REPR = "NaN";
    public static final String NOVALUE_REPR = "NOVALUE";

    public final boolean isNumeric;

    Type(boolean isNumeric) {
        this.isNumeric = isNumeric;
    }

    public boolean isNaN(Number n) {
        switch (this) {
            case INT:
                return n == INT_NaN;
            case FLOAT:
                return n == FLOAT_NaN;
            default:
                throw new IllegalArgumentException();
        }
    }

    public Number getNaN() {
        switch (this) {
            case INT:
                return INT_NaN;
            case FLOAT:
                return FLOAT_NaN;
            default:
                throw new IllegalArgumentException();
        }
    }

    public boolean isNOVALUE(Comparable value) {
        return value == NOVALUE;
    }

    public Comparable getNOVALUE() {
        return NOVALUE;
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

    /*
    public String repr() {
    }
    */

    /*
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
    */
}
