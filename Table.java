/** TODO: Views, RowIterator/ColumnIterator
 */

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;

public class Table implements Iterable<Object[]> {
    private enum Type {
        INT(true), FLOAT(true), STRING(false);

        private final boolean isNumeric;

        private Type(boolean isNumeric) {
            this.isNumeric = isNumeric;
        }

        private static final Object NAN = new Object();
        private static final Object NOVALUE = new Object();
    }

    private class Column {
        private final String name;
        private final Type type;
        private final List values;

        private Column(String name, Type type) {
            this.name = name;
            this.type = type;
            values = new ArrayList();
        }
    }

    private final Column[] columns;

    public Table(String[] columnNames, Type[] columnTypes) {
        assert columnNames.length > 0;
        assert columnNames.length == columnTypes.length;
        columns = new Column[columnNames.length];

        for (int index = 0; index < columnNames.length; index++) {
            String name = columnNames[index];
            Type type = columnTypes[index];
            columns[index] = new Column(name, type);
        }
    }

    private static String repr(Object value) {
        if (value == Type.NAN) {
            return "NAN";
        }
        else if (value == Type.NOVALUE) {
            return "NOVALUE";
        }
        else if (value instanceof String) {
            return '\'' + value.toString() + '\'';
        }
        else if (value instanceof Double) {
            return String.format("%.3f", (double) value);
        }
        else {
            return value.toString();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<Object[]> iterator() {
        return new Iterator() {
            private int rowIndex = 0;

            @Override
            public boolean hasNext() {
                return rowIndex < columns[0].values.size();
            }

            @Override
            public Object[] next() {
                Object[] row = new Object[columns.length];
                for (int colIndex = 0; colIndex < columns.length; colIndex++) {
                    row[colIndex] = columns[colIndex].values.get(rowIndex);
                }
                rowIndex++;
                return row;
            }
        };
    }

    @SuppressWarnings("unchecked")
    public void insert(Object[] row) {
        assert row.length == columns.length;

        for (int index = 0; index < row.length; index++) {
            Object obj = row[index];
            Type type = columns[index].type;
            if (!(obj == Type.NAN && type.isNumeric) && obj != Type.NOVALUE) {
                switch (type) {
                    case INT:
                        assert obj instanceof Integer;
                        break;
                    case FLOAT:
                        assert obj instanceof Double;
                        break;
                    case STRING:
                        assert obj instanceof String;
                        break;
                }
            }
            columns[index].values.add(obj);
        }
    }

    @SuppressWarnings("unchecked")
    public Table join(Table other) {
        List<Integer> indices = new ArrayList<>();
        List<Integer> otherIndices = new ArrayList<>();

        for (int index = 0; index < columns.length; index++) {
            for (int otherIndex = 0; otherIndex < other.columns.length;
                    otherIndex++) {
                Column column = columns[index];
                Column otherColumn = other.columns[otherIndex];
                if (column.name.equals(otherColumn.name)) {
                    assert column.type == otherColumn.type;
                    indices.add(index);
                    otherIndices.add(otherIndex);
                }
            }
        }

        assert indices.size() == otherIndices.size();
        int numCols = columns.length + other.columns.length - indices.size();

        int newColIndex = 0;
        String[] names = new String[numCols];
        Type[] types = new Type[numCols];

        for (int colIndex : indices) {
            names[newColIndex] = columns[colIndex].name;
            types[newColIndex] = columns[colIndex].type;
            newColIndex++;
        }
        for (int colIndex = 0; colIndex < columns.length; colIndex++) {
            if (!indices.contains(colIndex)) {
                Column column = columns[colIndex];
                names[newColIndex] = column.name;
                types[newColIndex] = column.type;
                newColIndex++;
            }
        }
        for (int colIndex = 0; colIndex < other.columns.length; colIndex++) {
            if (!otherIndices.contains(colIndex)) {
                Column column = other.columns[colIndex];
                names[newColIndex] = column.name;
                types[newColIndex] = column.type;
                newColIndex++;
            }
        }

        assert newColIndex == numCols;

        Table result = new Table(names, types);

        for (Object[] row : this) {
            for (Object[] otherRow : other) {
                boolean addRow = true;
                for (int index = 0; index < indices.size(); index++) {
                    if (!row[indices.get(index)].equals(
                            otherRow[otherIndices.get(index)])) {
                        addRow = false;
                        break;
                    }
                }
                if (addRow) {
                    Object[] newRow = new Object[numCols];
                    int newRowIndex = 0;
                    for (int index : indices) {
                        newRow[newRowIndex++] = row[index];
                    }
                    for (int index = 0; index < columns.length; index++) {
                        if (!indices.contains(index)) {
                            newRow[newRowIndex++] = row[index];
                        }
                    }
                    for (int index = 0; index < other.columns.length; index++) {
                        if (!otherIndices.contains(index)) {
                            newRow[newRowIndex++] = otherRow[index];
                        }
                    }
                    assert newRowIndex == numCols;
                    result.insert(newRow);
                }
            }
        }

        return result;
    }

    public String toString() {
        StringBuilder str = new StringBuilder(1024);
        int numRows = columns[0].values.size();

        for (int index = 0; index < columns.length; index++) {
            Column column = columns[index];
            str.append(column.name);
            str.append(' ');
            str.append(column.type.name().toLowerCase());
            if (index < columns.length - 1) {
                str.append(',');
            }
        }

        for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
            str.append('\n');
            for (int colIndex = 0; colIndex < columns.length; colIndex++) {
                Column column = columns[colIndex];
                str.append(Table.repr(column.values.get(rowIndex)));
                if (colIndex < columns.length - 1) {
                    str.append(',');
                }
            }
        }
        return str.toString();
    }

    public static void main(String[] args) {
        /*
        Table table = new Table(new String[] {"name", "age", "balance"},
                                new Type[] {Type.STRING, Type.INT, Type.FLOAT});
        table.insert(new Object[] {"Jonathan", 18, 1.2});
        table.insert(new Object[] {"Jonathan", 18, 5.5});
        System.out.println(table);

        Table other = new Table(new String[] {"name", "occupation"},
                                new Type[] {Type.STRING, Type.STRING});
        other.insert(new Object[] {"Jonathan", "Student"});
        System.out.println(other);

        Table tmp = table.join(other);
        System.out.println(tmp);
        */

        Table foobar = new Table(new String[] {"a", "b"}, new Type[] {Type.STRING, Type.INT});
        foobar.insert(new Object[] {Type.NOVALUE, 1});
        foobar.insert(new Object[] {"Hello", Type.NAN});
        // foobar.insert(new Object[] {Type.NAN, 1});
        System.out.println(foobar);

        Table t7 = new Table(new String[] {"X", "Y", "Z", "W"}, new Type[] {
                             Type.INT, Type.INT, Type.INT, Type.INT});
        t7.insert(new Object[] {1, 7, 2, 10});
        t7.insert(new Object[] {7, 7, 4, 1});
        t7.insert(new Object[] {1, 9, 9, 1});

        Table t8 = new Table(new String[] {"W", "B", "Z"}, new Type[] {
                             Type.INT, Type.INT, Type.INT});
        t8.insert(new Object[] {1, 7, 4});
        t8.insert(new Object[] {7, 7, 3});
        t8.insert(new Object[] {1, 9, 6});
        t8.insert(new Object[] {1, 11, 9});

        Table t9 = t7.join(t8);
        System.out.println(t9);
    }
}
