package db;

public enum Type {
    INT(true), FLOAT(true), STRING(false);

    private boolean isNumeric;

    private Type(boolean isNumeric) {
        this.isNumeric = isNumeric;
    }

    public boolean isNumeric() {
        return this.isNumeric;
    }
}
