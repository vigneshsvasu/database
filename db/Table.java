/** Table
 */

import java.util.List;
import java.util.ArrayList;

import java.util.Map;
import java.util.HashMap;

public class Table {
    private final static Object NOVALUE = new Object();
    private final static Object NAN = new Object();

    private enum Type {
        INT, FLOAT, STRING;
    }

    private class Column {
        private final String name;
        private final Type type;
        private final List<Object> values;

        private Column(String name, Type type) {
            this.name = name;
            this.type = type;
            values = new ArrayList<>();
        }

        private Object get(int index) {
            return values.get(index);
        }

        private int rows() {
            return values.size();
        }

        private void append(Object item) {
            values.add(item);
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

    public void insert(Object... row) {
        assert row.length == columns.length;

        for (int index = 0; index < columns.length; index++) {
            Column column = columns[index];
            Object value = row[index];

            if (value != NOVALUE && (value != NAN &&
                                     column.type != Type.STRING)) {
                switch (column.type) {
                    case INT:
                        assert value instanceof Integer;
                        break;
                    case FLOAT:
                        assert value instanceof Double;
                        break;
                    case STRING:
                        assert value instanceof String;
                        break;
                    default:
                        assert false;  // Should never reach here
                }
            }

            column.append(value);
        }
    }

    public String toString() {
        StringBuilder str = new StringBuilder(1024);

        for (Column column : columns) {
            str.append(column.name);
            str.append(' ');
        }
        str.append('\n');

        assert columns.length > 0;
        int rows = columns[0].rows();
        for (int index = 0; index < rows; index++) {
            for (Column column : columns) {
                Object value = column.get(index);
                String repr = "NOVALUE";
                if (value == NAN) {
                    repr = "NAN";
                }
                else {
                    repr = value.toString();
                    if (value instanceof String) {
                        repr = '"' + repr + '"';
                    }
                }
                str.append(repr);
                str.append(' ');
            }
            str.append('\n');
        }

        return str.toString();
    }

    public Table join(Table other) {
        List<Integer> indices = new ArrayList<>();
        List<Integer> otherIndices = new ArrayList<>();

        for (int i = 0; i < columns.length; i++) {
            for (int j = 0; j < other.columns.length; j++) {
                Column column = columns[i], otherColumn = other.columns[j];
                if (column.name.equals(otherColumn.name)) {
                    indices.add(i);
                    otherIndices.add(j);
                }
            }
        }

        System.out.println(indices + " " + otherIndices);

        return null;
    }

    public static void main(String[] args) {
        Table tbl = new Table(new String[] {"name", "age"},
                              new Type[] {Type.STRING, Type.FLOAT});
        tbl.insert("Hello", 18.0);
        tbl.insert("World", 10.0);
        tbl.insert("Something", NAN);
        System.out.println(tbl);

        Table t7 = new Table(new String[] {"X", "Y", "Z", "W"},
                             new Type[] {Type.INT, Type.INT, Type.INT, Type.INT});
        t7.insert(1, 7, 2, 10);
        t7.insert(7, 7, 4, 1);
        t7.insert(1, 9, 9, 1);
        System.out.println(t7);

        Table t8 = new Table(new String[] {"W", "B", "Z"},
                             new Type[] {Type.INT, Type.INT, Type.INT});
        t8.insert(1, 7, 4);
        t8.insert(7, 7, 3);
        t8.insert(1, 9, 6);
        t8.insert(1, 11, 9);
        System.out.println(t8);

        Table t9 = t7.join(t8);
        System.out.println(t9);
    }
}
