package db;

import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;

import java.io.IOException;

public class Database {
    private Map<String, Table> tables;

    public Database() {
        tables = new HashMap<>();
    }

    private Table getTable(String name) {
        if (!tables.containsKey(name)) {
            // TODO: throw exception
        }
        return tables.get(name);
    }

    private void loadTable(String path) throws IOException, Parser.InvalidSyntaxException {
        String tableName = Parser.extractTableName(path);
        Table table = Parser.parseTable(FileIO.read(path));
        if (tables.containsKey(tableName)) {
            return;  // TODO: throw exception
        }
        tables.put(tableName, table);
    }

    private void storeTable(String path) throws IOException {
        String tableName = Parser.extractTableName(path);
        Table table = getTable(tableName);
        FileIO.write(path, table.toString() + "\n");
    }

    private Table select(String columnExprs, String tableNames, String conditions) {
        // TODO: fix this
        Table aggregate = null, current = null;

        for (String tableName : tableNames.split(",")) {
            tableName = tableName.trim();
            if (!tables.containsKey(tableName)) {
                return null;  // TODO: throw exception
            }

            current = tables.get(tableName);

            if (aggregate == null) {
                aggregate = current;
            }
            else {
                aggregate = aggregate.join(current);
            }
        }

        return aggregate;
    }

    public String transact(String query) {
        Matcher match = Parser.parseQuery(query);

        if (match != null) {
            switch (match.group("command")) {
                case "load":
                    String path = match.group(2);
                    try {
                        loadTable(path);
                    }
                    catch (IOException e) {
                        return String.format("ERROR: Failed to read from file \"%s.tbl\"", path);
                    }
                    catch (Parser.InvalidSyntaxException e) {
                        return e.toString();
                    }
                    break;

                case "store":
                    path = match.group(2);
                    try {
                        storeTable(path);
                    }
                    catch (IOException e) {
                        return String.format("ERROR: Failed to write to file \"%s\"", path);
                    }
                    break;

                case "select":
                    Table result = select(match.group("columns"),
                        match.group("tables"), match.group("conditions"));
                    if (result == null) {
                        return "ERROR: No such table";
                    }
                    return result.toString();

                case "print":
                    result = getTable(match.group(2));
                    if (result == null) {
                        return "ERROR: No such table";
                    }
                    return result.toString();
            }
        }
        else {
            return "ERROR: No such command";
        }

        return "";
    }
}
