package db;

enum Type {
    INT(true), FLOAT(true), STRING(false);

    private boolean isNumeric;

    private Type(boolean isNumeric) {
        this.isNumeric = isNumeric;
    }

    boolean isNumeric() {
        return this.isNumeric;
    }
}
