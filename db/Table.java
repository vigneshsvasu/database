package db;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import db.values.Value;

public class Table {
    private class Column implements Iterable {
        private final String name;
        private final Type type;
        private final List<Value> values;

        private Column(String name, Type type) {
            this.name = name;
            this.type = type;
            this.values = new ArrayList<>();
        }

        private void append(Value value) {
            values.add(value);
        }

        @Override
        public Iterator iterator() {
            return values.iterator();
        }

        private Column addTo(Column other) {
            // TOOD
            return null;
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

    public void insert(Value[] row) {
        assert row.length == columns.length;
        for (int index = 0; index < row.length; index++) {
            columns[index].append(row[index]);
        }
    }

    public void insert(String[] row) {
        assert row.length == columns.length;
        Value[] rowAsValues = new Value[row.length];
        for (int index = 0; index < row.length; index++) {
            Type type = columns[index].type;
            // TODO
        }
    }
}
