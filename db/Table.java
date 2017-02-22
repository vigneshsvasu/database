import java.util.List;
import java.util.ArrayList;

public class Table {
    private final static Object NOVALUE = new Object();
    private final static Object NAN = new Object();

    private enum Type {
        INT, FLOAT, STRING
    }

    private final String[] names;
    private final Type[] types;
    private final List[] columns;
    private int numRows;

    public Table(String[] columnNames, Type[] columnTypes) {
        names = columnNames;
        types = columnTypes;
        columns = new ArrayList[names.length];
        for (int index = 0; index < columns.length; index++) {
            columns[index] = new ArrayList<Object>();
        }
        numRows = 0;
    }

    public void insert(Object... row) {
        if (row.length != columns.length) {
            throw new IllegalArgumentException("row width not equal to " +
                                               "number of columns");
        }
        for (int index = 0; index < columns.length; index++) {
            Object value = row[index];
            if(value != NAN && value != NOVALUE) {
                switch(types[index]) {
                    case INT:
                        if (!(value instanceof Integer)) {
                            throw new IllegalArgumentException("");
                        }
                        break;
                    case FLOAT:
                        if (!(value instanceof Double)) {
                            throw new IllegalArgumentException("");
                        }
                        break;
                    case STRING:
                        if (!(value instanceof String)) {
                            throw new IllegalArgumentException("");
                        }
                        break;
                }
            }
            columns[index].add(value);
        }
        numRows++;
    }

    public Table join(Table other) {
        List<String> common = new ArrayList<>();
        for (String name : names) {
            for (String otherName : other.names) {
                if (name == otherName) {
                    common.add(name);
                }
            }
        }
        for (String name : common) {
            //
        }
        return null;
    }

    public String toString() {
        String s = "";
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < columns.length; j++) {
                Object obj = columns[j].get(i);
                String repr = obj.toString();
                if (obj == NAN) {
                    repr = "NAN";
                }
                else if (obj == NOVALUE) {
                    repr = "NOVALUE";
                }
                else if (obj instanceof String) {
                    repr = '"' + repr + '"';
                }
                s += repr + " ";
            }
            s += "\n";
        }
        return s;
    }

    public static void main(String[] args) {
        Table tbl = new Table(new String[] {"name", "age"},
                              new Type[] {Type.STRING, Type.FLOAT});
        tbl.insert("Hello", 18.0);
        tbl.insert("World", 10.0);
        tbl.insert("Something", NAN);
        System.out.print(tbl);

        Table other = new Table(new String[] {"name", "status"},
                                new Type[] {Type.STRING, Type.STRING});
        other.insert("Hello", "OK");
        System.out.print(other);

        Table joined = tbl.join(other);
    }
}
