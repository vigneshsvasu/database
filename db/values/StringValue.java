package db.values;

public class StringValue implements Value {
    private final char[] value;

    public StringValue(char[] value) {
        this.value = value;
    }

    public StringValue(String value) {
        this(value.toCharArray());
    }

    @Override
    public String toString() {
        return '"' + String.valueOf(value) + '"';
    }
}
