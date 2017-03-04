package db;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringJoiner;
import java.util.Map;
import java.util.HashMap;

public class Table implements Iterable<Value[]> {
    private class Column implements Iterable<Value> {
        private final String name;
        private final Type type;
        private final List<Value> values;

        private Column(String name, Type type) {
            this.name = name;
            this.type = type;
            values = new ArrayList<>();
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Column)) {
                return false;
            }
            Column otherAsColumn = (Column) other;
            return name.equals(otherAsColumn.name)
                && type.equals(otherAsColumn.type);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public String toString() {
            return String.format("<Column '%s'>", name);
        }

        @Override
        public Iterator<Value> iterator() {
            return values.iterator();
        }

        private Column addTo(Column other) {
            // TOOD
            return null;
        }
    }

    // TODO: implement zip
    private class LiteralColumn implements Iterable<Value> {
        private final Value literal;

        private LiteralColumn(Value literal) {
            this.literal = literal;
        }

        @Override
        public Iterator<Value> iterator() {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private class RowIterator implements Iterator<Value[]> {
        private Iterator<Value>[] columnIterators;

        private RowIterator() {
            columnIterators = new Iterator[columnCount()];
            for (int index = 0; index < columnIterators.length; index++) {
                columnIterators[index] = columns[index].iterator();
            }
        }

        @Override
        public boolean hasNext() {
            for (Iterator<Value> iter : columnIterators) {
                if (!iter.hasNext()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public Value[] next() {
            Value[] row = new Value[columnIterators.length];
            for (int index = 0; index < columnIterators.length; index++) {
                row[index] = columnIterators[index].next();
            }
            return row;
        }
    }

    private final Column[] columns;
    private final Map<String, Column> columnLookupTable;

    public Table(String[] names, Type[] types) {
        assert names.length == types.length;
        columns = new Column[names.length];
        columnLookupTable = new HashMap<>();

        for (int index = 0; index < columns.length; index++) {
            columns[index] = new Column(names[index], types[index]);
            columnLookupTable.put(names[index], columns[index]);
        }
    }

    private Table(Column[] columns) {
        this.columns = columns;
        columnLookupTable = new HashMap<>();
        for (Column column : columns) {
            columnLookupTable.put(column.name, column);
        }
    }

    @Override
    public Iterator<Value[]> iterator() {
        return new RowIterator();
    }

    public int columnCount() {
        return columns.length;
    }

    public int rowCount() {
        return columns[0].values.size();
    }

    public String getName(int index) {
        return columns[index].name;
    }

    public Type getType(int index) {
        return columns[index].type;
    }

    private int indexOf(String name) {
        for (int index = 0; index < columns.length; index++) {
            if (columns[index].name.equals(name)) {
                return index;
            }
        }
        return -1;
    }

    public void insert(Value[] row) {
        assert row.length == columns.length;
        for (int index = 0; index < row.length; index++) {
            columns[index].values.add(row[index]);
        }
    }

    public Value[] get(int rowIndex){
        Value[] row = new Value[columnCount()];
        for (int columnIndex = 0; columnIndex < row.length; columnIndex++) {
            row[columnIndex] = columns[columnIndex].values.get(rowIndex);
        }
        return row;
    }

    public String toString() {
        StringJoiner rowJoiner = new StringJoiner(Parser.ROW_DELIMETER);

        StringJoiner columnJoiner = new StringJoiner(Parser.COLUMN_DELIMETER);
        for (Column column : columns) {
            columnJoiner.add(String.format("%s %s", column.name,
                             column.type.name().toLowerCase()));
        }
        rowJoiner.add(columnJoiner.toString());

        for (Value[] row : this) {
            columnJoiner = new StringJoiner(Parser.COLUMN_DELIMETER);
            for (Value value : row) {
                columnJoiner.add(value.toString());
            }
            rowJoiner.add(columnJoiner.toString());
        }

        return rowJoiner.toString();
    }

    private List<Column> findCommonColumns(Table other) {
        List<Column> commonColumns = new ArrayList<>();
        for (Column column : columns) {
            for (Column otherColumn : other.columns) {
                if (column.name.equals(otherColumn.name)) {
                    if (column.type != otherColumn.type) {
                        // TODO: throw an exception
                    }
                    commonColumns.add(new Column(column.name, column.type));
                }
            }
        }
        return commonColumns;
    }

    private Table constructEmptyJoinedTable(Table other,
            List<Column> commonColumns) {
        List<Column> newColumns = new ArrayList<>(commonColumns);

        for (Column column : columns) {
            if (!newColumns.contains(column)) {
                newColumns.add(new Column(column.name, column.type));
            }
        }

        for (Column otherColumn : other.columns) {
            if (!newColumns.contains(otherColumn)) {
                newColumns.add(new Column(otherColumn.name, otherColumn.type));
            }
        }

        Column[] newColumnsAsArray = new Column[newColumns.size()];
        for (int index = 0; index < newColumns.size(); index++) {
            newColumnsAsArray[index] = newColumns.get(index);
        }
        return new Table(newColumnsAsArray);
    }

    private boolean shouldJoin(Value[] row, Value[] otherRow,
            List<Integer> indices, List<Integer> otherIndices) {
        for (int index = 0; index < indices.size(); index++) {
            if (!row[indices.get(index)].equals(otherRow[otherIndices.get(index)])) {
                return false;
            }
        }
        return true;
    }

    private void populateJoinedTable(Table result, Table other,
                                     List<Column> commonColumns) {
        List<Integer> indices = new ArrayList<>(commonColumns.size());
        List<Integer> otherIndices = new ArrayList<>(commonColumns.size());
        for (int index = 0; index < commonColumns.size(); index++) {
            Column column = commonColumns.get(index);
            indices.add(indexOf(column.name));
            otherIndices.add(other.indexOf(column.name));
        }

        for (Value[] row : this) {
            for (Value[] otherRow : other) {
                if (shouldJoin(row, otherRow, indices, otherIndices)) {
                    Value[] newRow = new Value[result.columnCount()];
                    int runningIndex = 0;

                    for (int index : indices) {
                        newRow[runningIndex++] = row[index];  // TODO: cloning?
                    }

                    for (int index = 0; index < columns.length; index++) {
                        if (!indices.contains(index)) {
                            newRow[runningIndex++] = row[index];
                        }
                    }

                    for (int index = 0; index < other.columns.length; index++) {
                        if (!otherIndices.contains(index)) {
                            newRow[runningIndex++] = otherRow[index];
                        }
                    }

                    assert runningIndex == newRow.length;
                    result.insert(newRow);
                }
            }
        }
    }

    public Table join(Table other) {
        List<Column> commonColumns = findCommonColumns(other);
        Table result = constructEmptyJoinedTable(other, commonColumns);
        populateJoinedTable(result, other, commonColumns);
        return result;
    }
}
