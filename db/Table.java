package db;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringJoiner;

import db.Value;
import db.IntValue;
import db.FloatValue;
import db.StringValue;
import db.MagicValue;

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

    public Table(String[] names, Type[] types) {
        assert names.length == types.length;
        columns = new Column[names.length];

        for (int index = 0; index < columns.length; index++) {
            columns[index] = new Column(names[index], types[index]);
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
}
