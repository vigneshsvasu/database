package db;

import java.io.BufferedReader;
import java.io.IOException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Parser {
    private static final String COLUMN_DELIMETER = ",";
    private static final String NAME = "[a-zA-Z]\\w*";

    private static final Pattern COLUMN_METADATA_PATTERN = Pattern.compile(
                                 "(" + NAME + ")\\s+(int|float|string)");

    private static class InvalidSyntaxException extends Exception {
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
            String metadata = columnMetadata[index];
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
            String symbol = symbols[index];
            if (MagicValue.NAN.equals(symbol)) {
                values[index] = MagicValue.NAN;
            } else if (MagicValue.NOVALUE.equals(symbol)) {
                values[index] = MagicValue.NOVALUE;
            } else {
                switch (table.getType(index)) {
                    case INT:
                        values[index] = new IntValue(Integer.parseInt(symbol));
                        break;
                    case FLOAT:
                        values[index] = new FloatValue(Double.parseDouble(symbol));
                        break;
                    case STRING:
                        char start = symbol.charAt(0);
                        char end = symbol.charAt(symbol.length() - 1);
                        if (!(start == '\'' && end == '\'')) {
                            String message = String.format(
                                "string value \"{0}\" not quoted", symbol);
                            throw new InvalidSyntaxException(message);
                        }
                        values[index] = new StringValue(symbol.substring(1,
                            symbol.length() - 1));
                        break;
                    default:
                        throw new InvalidSyntaxException("no such type");
                }
            }
        }

        table.insert(values);
    }

    /** Construct a Table from an input
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
