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

    private static Table constructEmptyTable(String[] columnMetadata)
                         throws InvalidSyntaxException {
        int numColumns = columnMetadata.length;
        String[] columnNames = new String[numColumns];
        Type[] columnTypes = new Type[numColumns];

        for (int index = 0; index < numColumns; index++) {
            String metadata = columnMetadata[index];
            Matcher match = COLUMN_METADATA_PATTERN.matcher(metadata);

            if(match.matches()) {
                columnNames[index] = match.group(1);
                columnTypes[index] = Type.valueOf(match.group(2).toUpperCase());
            }
            else {
                String message = String.format("bad column: \"{0}\"", metadata);
                throw new InvalidSyntaxException(message);
            }
        }

        return new Table(columnNames, columnTypes);
    }

    private static void populateRow(Table table, String row) {
        //
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
