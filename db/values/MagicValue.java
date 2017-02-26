package db;

class MagicValue implements Value {
    static final Value NAN = new MagicValue("NAN");
    static final Value NOVALUE = new MagicValue("NOVALUE");

    private final String repr;

    MagicValue(String repr) {
        this.repr = repr;
    }

    @Override
    public String toString() {
        return repr;
    }
}
