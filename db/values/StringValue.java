package db;

class StringValue implements Value {
    private final char[] value;

    StringValue(char[] value) {
        this.value = value;
    }

    StringValue(String value) {
        this(value.toCharArray());
    }

    @Override
    public String toString() {
        return '"' + String.valueOf(value) + '"';
    }
}
