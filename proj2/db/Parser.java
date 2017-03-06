package db;

import java.io.BufferedReader;
import java.io.IOException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import static db.DatabaseException.InvalidSyntaxException;

public class Parser {
    public static final String COLUMN_DELIMETER = ",";
    public static final String ROW_DELIMETER = "\n";
    public static final String ALL_COLUMNS = "*";

    private static final String NAME = "([a-zA-Z]\\w*)";
    private static final String TYPE = "(int|float|string)";
    private static final String FILE_PATH = "(?<path>(.+\\/)*" + NAME + ")";
    private static final String OPERATORS = "(\\+|-|\\*|/)";
    private static final String COMPARATORS = "(==|!=|<|<=|>|>=)";

    // Command patterns
    private static final Pattern LOAD_CMD = makeCommand("load", FILE_PATH);
    private static final Pattern STORE_CMD = makeCommand("store", FILE_PATH);
    private static final Pattern CREATE_CMD = makeCommand("create",
        "table\\s+" + NAME + "(?:\\s+\\((?<columns>.+)\\)|\\s+as\\s+(?<select>.+?))");
    private static final Pattern DROP_CMD = makeCommand("drop", "table\\s+" + NAME);
    private static final Pattern SELECT_CMD = makeCommand("select",
        "(?<columns>.+)\\s+from\\s+(?<tables>.+?)"
        + "(?:\\s+where\\s+(?<conditions>.+?))?");
    private static final Pattern INSERT_CMD = makeCommand("insert",
        "into\\s+" + NAME + "\\s+values\\s+\\((?<literals>.+)\\)");
    private static final Pattern PRINT_CMD = makeCommand("print", NAME);
    private static final Pattern[] COMMANDS = {LOAD_CMD, STORE_CMD, CREATE_CMD,
        DROP_CMD, SELECT_CMD, INSERT_CMD, PRINT_CMD};

    // Common patterns
    private static final Pattern COLUMN_METADATA_PATTERN = makeRegex(
        NAME + "\\s+" + TYPE);
    private static final Pattern COLUMN_EXPR_ALIAS_PATTERN = makeRegex(NAME
        + "\\s*" + OPERATORS + "\\s*(.+)\\s+as\\s+" + NAME);
    private static final Pattern CONDITION_PATTERN = makeRegex(NAME + "\\s*"
        + COMPARATORS + "\\s*(.+)");

    private static Pattern makeRegex(String baseExpr) {
        return Pattern.compile("^\\s*" + baseExpr + "\\s*$");
    }

    private static Pattern makeCommand(String name, String rest) {
        return makeRegex("(?<command>" + name + ")\\s+" + rest);
    }

    public static Matcher parseQuery(String query) {
        Matcher match = null;
        for (Pattern cmd : COMMANDS) {
            match = cmd.matcher(query);
            if (match.matches()) {
                return match;
            }
        }
        return null;
    }

    public static Matcher parseColumnExpression(String columnExpr) {
        Matcher match = COLUMN_EXPR_ALIAS_PATTERN.matcher(columnExpr);
        if (match.matches()) {
            return match;
        } else {
            return null;
        }
    }

    public static Matcher parseConditionExpression(String conditionExpr) {
        Matcher match = CONDITION_PATTERN.matcher(conditionExpr);
        if (match.matches()) {
            return match;
        } else {
            return null;
        }
    }

    public static String extractTableName(String path) {
        String[] components = path.split("/");
        return components[components.length - 1];
    }

    public static Table constructEmptyTable(String[] columnMetadata)
                        throws InvalidSyntaxException {
        int numColumns = columnMetadata.length;
        String[] columnNames = new String[numColumns];
        Type[] columnTypes = new Type[numColumns];

        for (int index = 0; index < numColumns; index++) {
            String metadata = columnMetadata[index];
            Matcher match = COLUMN_METADATA_PATTERN.matcher(metadata);

            if (match.matches()) {
                columnNames[index] = match.group(1);
                columnTypes[index] = Type.valueOf(match.group(2).toUpperCase());
            } else {
                String message = String.format("bad column metadata: \"%s\"",
                                               metadata);
                throw new InvalidSyntaxException(message);
            }
        }

        return new Table(columnNames, columnTypes);
    }

    public static Table constructEmptyTable(String columnMetadata)
                        throws InvalidSyntaxException {
        return constructEmptyTable(columnMetadata.split(COLUMN_DELIMETER));
    }

    private static String unquote(String strRepr) throws InvalidSyntaxException {
        if (strRepr.length() < 2 || strRepr.charAt(0) != '\'' ||
                strRepr.charAt(strRepr.length() - 1) != '\'') {
            throw new InvalidSyntaxException("string literal must be single-quoted");
        }
        return strRepr.substring(1, strRepr.length() - 1);
    }

    public static Comparable parseValue(String repr, Type type)
                              throws InvalidSyntaxException {
        if (repr.equals(Type.NOVALUE_REPR)) {
            return type.getNOVALUE();
        } else if (repr.equals(Type.NAN_REPR)) {
            return type.getNAN();
        }

        switch (type) {
            case INT:
                return Integer.parseInt(repr);
            case FLOAT:
                return Double.parseDouble(repr);
            case STRING:
                return unquote(repr);
            default:
                throw new InvalidSyntaxException("no such type");
        }
    }

    public static void populateRow(Table table, String row)
                       throws DatabaseException {
        String[] symbols = row.split(COLUMN_DELIMETER);
        int numColumns = table.columnCount();
        if (symbols.length != numColumns) {
            String message = String.format("number of values in row \"%s\" "
                + "does not match number of columns in table", row);
            throw new InvalidSyntaxException(message);
        }

        Comparable[] values = new Comparable[numColumns];
        for (int index = 0; index < numColumns; index++) {
            String symbol = symbols[index].trim();
            Type type = table.getType(index);
            try {
                values[index] = parseValue(symbol, type);
            } catch (NumberFormatException exc) {
                throw new DatabaseException(String.format(
                    "malformed numeric literal \"%s\"", symbol));
            }
        }

        table.insert(values);
    }

    public static Table parseTable(BufferedReader reader)
                        throws IOException, DatabaseException {
        String line = reader.readLine();
        if (line == null) {
            throw new InvalidSyntaxException("empty table file");
        }
        Table table = constructEmptyTable(line);
        while ((line = reader.readLine()) != null) {
            populateRow(table, line);
        }
        return table;
    }
}
