package db;

import java.io.BufferedReader;
import java.io.IOException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.StringJoiner;

public class Parser {
    private static final String COLUMN_DELIMETER = ",";

    private static final String NAME_GROUP = "([a-zA-Z]\\w*)";
    private static final String FILENAME_GROUP = "(.+)";

    private static final Pattern LOAD_CMD = Pattern.compile("load\\s+" + FILENAME_GROUP);
    private static final Pattern STORE_CMD = Pattern.compile("store\\s+" + FILENAME_GROUP);
    // private static final
    private static final Pattern DROP_CMD = Pattern.compile("drop\\s+table\\s+" + NAME_GROUP);

    // private static final String SELECT_CMD = ""

    // private static final Pattern CREATE_CMD = Pattern.compile("create\\s+table\\s+" + NAME_GROUP + "\\s+(as\\s+*)")

    private static final Pattern COLUMN_METADATA_PATTERN = Pattern.compile(
                                 NAME_GROUP + "\\s+(int|float|string)");

    public static class InvalidSyntaxException extends Exception {
        private InvalidSyntaxException(String message) {
            super(message);
        }
    }

    private enum Command {
        //
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
                String message = String.format("bad column metadata: \"{0}\"",
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
                    String message = String.format("string value \"{0}\" "
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
            String message = String.format("number of values in row \"{0}\" "
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
}
