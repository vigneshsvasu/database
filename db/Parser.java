package db;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Parser {
    public static final String COLUMN_DELIMETER = ",";
    public static final String ROW_DELIMETER = "\n";

    private static final String NAME = "([a-zA-Z]\\w*)";
    private static final String TYPE = "(int|float|string)";
    private static final String FILE_PATH = "(?<file-path>.+)";

    // Command patterns
    private static final Pattern LOAD_CMD = makeRegex("load\\s+" + FILE_PATH);
    private static final Pattern STORE_CMD = makeRegex("store\\s+" + FILE_PATH);
    private static final Pattern SELECT_CMD = makeRegex(
        "select\\s+(?<columns>.+)\\s+from\\s+(?<tables>.+?)"
        + "(?:\\s+where\\s+(?<conditions>.+))?");
    private static final Pattern CREATE_CMD = makeRegex(
        "create\\s+table\\s+" + NAME
        + "(?:\\s+\\((?<column-data>.+)\\)|\\s+as\\s+(?<select-clause>.+))");
    private static final Pattern DROP_CMD = makeRegex("drop\\s+table\\s+" + NAME);
    private static final Pattern INSERT_CMD = makeRegex(
        "insert\\s+into\\s+" + NAME + "\\s+values\\s+(?<literals>.+)");
    private static final Pattern PRINT_CMD = makeRegex("print\\s+" + NAME);

    // Common patterns
    private static final Pattern COLUMN_METADATA_PATTERN = makeRegex(
        NAME + "\\s+" + TYPE);

    private static Pattern makeRegex(String baseExpr) {
        return Pattern.compile("^\\s*" + baseExpr + "\\s*$");
    }

    public static class InvalidSyntaxException extends Exception {
        private InvalidSyntaxException(String message) {
            super(message);
        }
    }

    private enum Command {
        //
    }

    // TODO: trim strings

    public void parseQuery(String query) {
    }

    public static Table constructEmptyTable(String[] columnMetadata)
                        throws InvalidSyntaxException {
        int numColumns = columnMetadata.length;
        String[] columnNames = new String[numColumns];
        Type[] columnTypes = new Type[numColumns];

        for (int index = 0; index < numColumns; index++) {
            String metadata = columnMetadata[index].trim();
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

    private static Value parseValue(String repr, Type type)
                         throws InvalidSyntaxException {
        switch (type) {
            case INT:
                return new IntValue(Integer.parseInt(repr));

            case FLOAT:
                return new FloatValue(Double.parseDouble(repr));

            case STRING:
                char start = repr.charAt(0);
                char end = repr.charAt(repr.length() - 1);
                if (!(start == '\'' && end == '\'')) {
                    String message = String.format("string value \"%s\" "
                        + "not quoted", repr);
                    throw new InvalidSyntaxException(message);
                }
                return new StringValue(repr.substring(1, repr.length() - 1));

            default:
                throw new InvalidSyntaxException("no such type");
        }
    }

    public static void populateRow(Table table, String row)
            throws InvalidSyntaxException {
        String[] symbols = row.split(COLUMN_DELIMETER);
        int numColumns = table.columnCount();
        if (symbols.length != numColumns) {
            String message = String.format("number of values in row \"%s\" "
                + "does not match number of columns in table", row);
            throw new InvalidSyntaxException(message);
        }

        Value[] values = new Value[numColumns];
        for (int index = 0; index < numColumns; index++) {
            String symbol = symbols[index].trim();
            if (MagicValue.NAN.equals(symbol)) {
                values[index] = MagicValue.NAN;
            } else if (MagicValue.NOVALUE.equals(symbol)) {
                values[index] = MagicValue.NOVALUE;
            } else {
                values[index] = parseValue(symbol, table.getType(index));
            }
        }

        table.insert(values);
    }

    /** Construct a Table from the contents of a buffered reader.
     *
     *  Reference:
     *  https://docs.oracle.com/javase/tutorial/essential/io/file.html#textfiles
     */
    public static Table parseTable(BufferedReader reader)
                        throws IOException, InvalidSyntaxException {
        String line = reader.readLine();
        if (line == null) {
            throw new InvalidSyntaxException("empty table file");
        }
        String[] columnMetadata = line.split(COLUMN_DELIMETER);
        Table table = constructEmptyTable(columnMetadata);
        while ((line = reader.readLine()) != null) {
            populateRow(table, line);
        }
        return table;
    }

    public static class ParserInternalTest {
        @Test
        public void testSelectClauseMatching() {
            Matcher match = null;

            match = SELECT_CMD.matcher("select x from y");
            assertTrue(match.matches());
            assertEquals("x", match.group(1));
            assertEquals("y", match.group(2));
            assertEquals(null, match.group(3));

            match = SELECT_CMD.matcher("select \t x , y,z  from  w where u>0");
            assertTrue(match.matches());
            assertEquals("x , y,z", match.group("columns"));
            assertEquals("w", match.group("tables"));
            assertEquals("u>0", match.group("conditions"));
        }
    }
}
