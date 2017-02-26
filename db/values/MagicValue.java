package db.values;

public class MagicValue implements Value {
    public static final Value NAN = new MagicValue("NAN");
    public static final Value NOVALUE = new MagicValue("NOVALUE");

    private final String repr;

    public MagicValue(String repr) {
        this.repr = repr;
    }

    @Override
    public String toString() {
        return repr;
    }

    public static boolean isNAN(Value other) {
        return other == NAN;
    }

    public static boolean isNOVALUE(Value other) {
        return other == NOVALUE;
    }

    @Override
    public boolean equals(Object other) {
        return repr.equals(other.toString());
    }
}
