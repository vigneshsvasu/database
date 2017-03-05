package db;

/** The superclass of all custom database exceptions.
 *
 *  All other custom exceptions reside in `DatabaseException` as public static
 *  inner subclasses. This configuration has some benefits:
 *
 *    - We can catch `DatabaseException`s in general, and not worry about
 *      legitimate logical errors being trapped and hidden.
 *    - We can distinguish between exceptions deliberately thrown by ourselves,
 *      and those thrown by the Java runtime.
 */
public class DatabaseException extends Exception {
    private static final String MESSAGE_FORMAT = "ERROR: %s";
    private static final String DEFAULT_MESSAGE = "general database exception";

    public DatabaseException() {
        super(DEFAULT_MESSAGE);
    }

    public DatabaseException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return String.format(MESSAGE_FORMAT, super.getMessage());
    }

    public static class InvalidSyntaxException extends DatabaseException {
        private static final int NO_LINE_NUMBER = -1;
        private final int lineNumber;

        public InvalidSyntaxException(String message) {
            super(message);
            lineNumber = NO_LINE_NUMBER;
        }

        public InvalidSyntaxException(int lineNumber, String message) {
            super(String.format("line %d of the current buffer: %s",
                  lineNumber, message));
            this.lineNumber = lineNumber;
        }
    }

    public static class IncompatibleTypeException extends DatabaseException {
        private final String tableName;

        public IncompatibleTypeException(String tableName){
            super(String.format("Operand is incompatible with column type.", tableName));
            this.tableName = tableName;
        }
    }

    public static class NoSuchTableException extends DatabaseException {
        private final String tableName;

        public NoSuchTableException(String tableName) {
            super(String.format("no such table \"%s\"", tableName));
            this.tableName = tableName;
        }
    }

    public static class TableAlreadyExistsException extends DatabaseException {
        private final String tableName;

        public TableAlreadyExistsException(String tableName) {
            super(String.format("table \"%s\" already exists", tableName));
            this.tableName = tableName;
        }
    }
}
