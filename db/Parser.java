package db;

import java.io.BufferedReader;
import java.io.IOException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Parser {
    private static final String COLUMN_DELIMETER = ",";
    private static final String NAME = "[a-zA-Z]\\w*";
    private static final Pattern COLUMN_DATA = Pattern.compile("(" + NAME +
                                               ")\\s+(int|float|string)");

    private enum Command {
        //
    }

    /** Construct a Table from an input
     *
     *  Reference:
     *  https://docs.oracle.com/javase/tutorial/essential/io/file.html#textfiles
     */
    public static Table parseTable(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        String[] columnData = line.split(COLUMN_DELIMETER);
        String[] columnNames = new String[columnData.length];
        Type[] columnTypes = new Type[columnData.length];
        for (String data : columnData) {
            Matcher match = COLUMN_DATA.matcher(data);
            if (matches) {
            }
            else {
                // throw new InvalidTableSyntaxException(String.format("bad column \"{0}\"", data));
            }
        }

        return null;
    }
}
