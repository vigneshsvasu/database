package db;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

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

    public Value[] get(int index){
        Value[] newArray = new Value[columns.length];
        for(int i = 0; i < columns.length; i++){
            Column col = columns[i];
            newArray[i] = col.values.get(index);
        }
        return newArray;
    }

    public String toString() {
        String returnString = " ";
        for (int i = 0; i < columns[0].values.size(); i++){
            for (int j = 0; j < columns.length; j++){
                if (j == columns.length - 1){
                    returnString += columns[i].values.get(j);
                }
            returnString = returnString + columns[i].values.get(j) + ",";
        }
        returnString = returnString + "\n";
        }
        return returnString;
    }
}
