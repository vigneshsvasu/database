package db.values;

public class MagicValue implements Value {
    private static final Value NAN = new MagicValue("NAN");
    private static final Value NOVALUE = new MagicValue("NOVALUE");

    private final String repr;

    public MagicValue(String repr) {
        this.repr = repr;
    }

    @Override
    public String toString() {
        return repr;
    }

    public boolean isNAN(Value other) {
        return other == NAN;
    }

    public boolean isNOVALUE(Value other) {
        return other == NOVALUE;
    }
}
