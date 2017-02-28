package db;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import db.values.Value;
import db.values.IntValue;
import db.values.FloatValue;
import db.values.StringValue;
import db.values.MagicValue;

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
            columns[index].values.add(row[index]);
        }
    }

    public void insert(String[] row) {
        assert row.length == columns.length;
        Value[] rowAsValues = new Value[row.length];
        for (int index = 0; index < row.length; index++) {
            String rowValue = row[index];
            if (MagicValue.NAN.equals(rowValue)) {
                rowAsValues[index] = MagicValue.NAN;
            }
            else if (MagicValue.NOVALUE.equals(rowValue)) {
                rowAsValues[index] = MagicValue.NOVALUE;
            }
            else {
                switch (columns[index].type) {
                    case INT:
                        rowAsValues[index] = new IntValue(Integer.parseInt(rowValue));
                        break;
                    case FLOAT:
                        rowAsValues[index] = new FloatValue(Double.parseDouble(rowValue));
                        break;
                    case STRING:
                        char end = rowValue.charAt(rowValue.length() - 1);
                        assert rowValue.charAt(0) == '"' && end == '"';
                        rowAsValues[index] = new StringValue(rowValue.substring(1, rowValue.length() - 1));
                        break;
                }
            }
        }
        insert(rowAsValues);
    }

    public Value[] get(int index){
        Value[] newArray = new Value[columns.length];
        for(int i = 0; i < columns.length; i++){
            Column col = columns[i];
            newArray[i] = col.values.get(index);
        }
        return newArray;
    }

    public String toString(){
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
