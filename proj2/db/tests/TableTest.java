package db.tests;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.IOException;

import static db.Parser.parseTable;
import db.DatabaseException;
import db.Table;
import db.Type;
import db.Value;
import db.IntValue;
import db.FloatValue;
import db.StringValue;
import db.MagicValue;
import db.FileIO;

public class TableTest {
    @Test
    public void testRowIteration() throws IOException, DatabaseException {
        Table table = parseTable(FileIO.read("examples/teams.tbl"));

        int rows = 0;
        for (Value[] row : table) {
            assertEquals(row.length, table.columnCount());
            assertArrayEquals(table.getRow(rows), row);
            rows++;
        }

        assertEquals(rows, table.rowCount());
    }

    @Test
    public void testColumnMetadataRetrieval() {
        String[] names = new String[] {"last_name", "first_name", "uid", "avg_score"};
        Type[] types = new Type[] {Type.STRING, Type.STRING, Type.INT, Type.FLOAT};
        Table table = new Table(names, types);

        assertEquals(names.length, table.columnCount());
        for (int index = 0; index < names.length; index++) {
            assertEquals(names[index], table.getName(index));
            assertEquals(types[index], table.getType(index));
        }
    }

    @Test
    public void testInsert() {
        String[] columnNames = new String[] {"name", "count", "balance"};
        Type[] columnTypes = new Type[] {Type.STRING, Type.INT, Type.FLOAT};
        Table table = new Table(columnNames, columnTypes);
        assertEquals(3, table.columnCount());

        table.insert(new Value[] {new StringValue("First"),
                                  new IntValue(2), new FloatValue(-1.5)});
        assertEquals(1, table.rowCount());

        String expected = "name string,count int,balance float\n"
                        + "'First',2,-1.500";
        assertEquals(expected, table.toString());

        table.insert(new Value[] {MagicValue.NOVALUE, MagicValue.NAN,
                                  new FloatValue(5)});
        assertEquals(2, table.rowCount());
        assertEquals(3, table.columnCount());

        expected = "name string,count int,balance float\n"
                        + "'First',2,-1.500\n"
                        + "NOVALUE,NAN,5.000";
        assertEquals(expected, table.toString());
    }

    @Test
    public void testJoin() throws IOException, DatabaseException {
        // An example from Lab 5
        Table t7 = parseTable(db.FileIO.read("examples/t7.tbl"));
        Table t8 = parseTable(db.FileIO.read("examples/t8.tbl"));
        Table t9 = t7.join(t8);

        assertEquals(2, t9.rowCount());
        assertEquals(5, t9.columnCount());

        int[][] expected = {
            {4, 1, 7, 7, 7},
            {9, 1, 1, 9, 11}
        };

        for (int rowIndex = 0; rowIndex < t9.rowCount(); rowIndex++) {
            Value[] row = t9.getRow(rowIndex);
            for (int columnIndex = 0; columnIndex < t9.columnCount(); columnIndex++) {
                IntValue value = (IntValue) row[columnIndex];
                assertEquals(expected[rowIndex][columnIndex], value.getValue());
            }
        }

        // No columns in common: the Cartesian product
    }
}
