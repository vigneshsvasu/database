package db;

public class StringValue implements Value<String> {
    private final char[] value;

    public StringValue(char[] value) {
        this.value = value;
    }

    public StringValue(String value) {
        this(value.toCharArray());
    }

    @Override
    public boolean equals(Object other) {
        return other.toString().equals(toString());
    }

    @Override
    public String toString() {
        return '\'' + String.valueOf(value) + '\'';
    }

    public String getValue(){
        String result = new String(value);
        return result;
    }
}
