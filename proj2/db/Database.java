package db;

import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;

import java.io.IOException;
import java.io.FileNotFoundException;

import static db.DatabaseException.NoSuchTableException;
import static db.DatabaseException.TableAlreadyExistsException;

public class Database {
    private final Map<String, Table> tables;

    public Database() {
        tables = new HashMap<>();
    }

    private Table getTable(String name) throws NoSuchTableException {
        if (!tables.containsKey(name)) {
            throw new NoSuchTableException(name);
        }
        return tables.get(name);
    }

    private void putTable(String name, Table table)
                 throws TableAlreadyExistsException {
        tables.put(name, table);
    }

    private Table select(String query) throws DatabaseException {
        Matcher match = Parser.parseQuery(query);
        if (!(match != null && match.group("command").equals("select"))) {
            throw new DatabaseException("malformed query");
        }
        return select(match);
    }

    private Table select(Matcher match) throws DatabaseException {
        // Join tables
        String[] tableNames = match.group("tables").split("\\s*,\\s*");
        Table table = getTable(tableNames[0]);
        for (int index = 1; index < tableNames.length; index++) {
            table = table.join(getTable(tableNames[index]));
        }

        // Select columns
        String columns = match.group("columns");
        String[] columnExprs;
        if (columns.equals(Parser.ALL_COLUMNS)) {
            columnExprs = new String[table.columnCount()];
            for (int index = 0; index < columnExprs.length; index++) {
                columnExprs[index] = table.getName(index);
            }
        } else {
            columnExprs = match.group("columns").split("\\s*,\\s*");
        }
        table = table.select(columnExprs);

        // Filter with conditions
        String conditions = match.group("conditions");
        if (conditions != null) {
            String[] conditionExprs = conditions.split("\\s+and\\s+");
            table.filter(conditionExprs);
        }

        return table;
    }

    private String loadTable(Matcher match) throws DatabaseException {
        String pathWithoutExt = match.group("path");
        String path = FileIO.addExtension(pathWithoutExt);
        String name = Parser.extractTableName(pathWithoutExt);

        Table table = null;
        try {
            table = Parser.parseTable(FileIO.read(path));
        } catch (FileNotFoundException exc) {
            throw new DatabaseException(
                String.format("failed to read file \"%s\"", path));
        } catch (IOException exc) {
            throw new DatabaseException(exc.getMessage());
        }

        putTable(name, table);
        return "";
    }

    private String storeTable(Matcher match) throws DatabaseException {
        String pathWithoutExt = match.group("path");
        String path = FileIO.addExtension(pathWithoutExt);
        String name = Parser.extractTableName(pathWithoutExt);

        Table table = getTable(name);
        try {
            FileIO.write(path, table.toString().trim() + "\n");
        } catch (IOException exc) {
            throw new DatabaseException(exc.getMessage());
        }

        return "";
    }

    private String createTable(Matcher match) throws DatabaseException {
        String name = match.group(2), columns = match.group("columns");
        Table table;
        if (columns != null) {
            table = Parser.constructEmptyTable(columns);
        } else {
            String subquery = match.group("select");
            table = select(subquery);
        }
        putTable(name, table);
        return "";
    }

    private String dropTable(Matcher match) throws NoSuchTableException {
        String tableName = match.group(2);
        if (!tables.containsKey(tableName)) {
            throw new NoSuchTableException(tableName);
        }
        tables.remove(tableName);
        return "";
    }

    private String selectTable(Matcher match) throws DatabaseException {
        Table table = select(match);
        return table.toString();
    }

    private String insertIntoTable(Matcher match) throws DatabaseException {
        Table table = getTable(match.group(2));
        Parser.populateRow(table, match.group("literals"));
        return "";
    }

    private String printTable(Matcher match) throws NoSuchTableException {
        Table table = getTable(match.group(2));
        return table.toString();
    }

    public String transact(String query) {
        Matcher match = Parser.parseQuery(query);

        try {
            if (match == null) {
                throw new DatabaseException("malformed query");
            }

            // Dispatch by action
            switch (match.group("command")) {
                case "load":
                    return loadTable(match);
                case "store":
                    return storeTable(match);
                case "create":
                    return createTable(match);
                case "drop":
                    return dropTable(match);
                case "select":
                    return selectTable(match);
                case "insert":
                    return insertIntoTable(match);
                case "print":
                    return printTable(match);
                default:
                    throw new DatabaseException("no such command");
            }
        } catch (DatabaseException exc) {
            return exc.getMessage();
        }
    }
}
