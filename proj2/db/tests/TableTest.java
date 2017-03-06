package db.tests;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.IOException;

import static db.Parser.parseTable;
import db.DatabaseException;
import db.Table;
import db.Type;
import db.FileIO;

public class TableTest {
    @Test
    public void testRowIteration() throws IOException, DatabaseException {
        Table table = parseTable(FileIO.read("examples/teams.tbl"));

        int rows = 0;
        for (Comparable[] row : table) {
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
    public void testInsert() throws DatabaseException {
        String[] columnNames = new String[] {"name", "count", "balance"};
        Type[] columnTypes = new Type[] {Type.STRING, Type.INT, Type.FLOAT};
        Table table = new Table(columnNames, columnTypes);
        assertEquals(3, table.columnCount());

        table.insert(new Comparable[] {"First", 2, -1.5});
        assertEquals(1, table.rowCount());

        String expected = "name string,count int,balance float\n"
                        + "'First',2,-1.500";
        assertEquals(expected, table.toString());

        table.insert(new Comparable[] {Type.STRING.getNOVALUE(), Type.INT.getNAN(),
                                  5.0});
        assertEquals(2, table.rowCount());
        assertEquals(3, table.columnCount());

        expected = "name string,count int,balance float\n"
                        + "'First',2,-1.500\n"
                        + "NOVALUE,NaN,5.000";
        assertEquals(expected, table.toString());
    }

    @Test
    public void testJoin() throws IOException, DatabaseException {
        // An example from Lab 5
        Table t7 = parseTable(FileIO.read("examples/t7.tbl"));
        Table t8 = parseTable(FileIO.read("examples/t8.tbl"));
        Table t9 = t7.join(t8);

        assertEquals(2, t9.rowCount());
        assertEquals(5, t9.columnCount());

        String[] names = new String[] {"z", "w", "x", "y", "b"};
        for (int index = 0; index < t9.columnCount(); index++) {
            assertEquals(names[index], t9.getName(index));
        }

        int[][] expected = {
            {4, 1, 7, 7, 7},
            {9, 1, 1, 9, 11}
        };

        for (int rowIndex = 0; rowIndex < t9.rowCount(); rowIndex++) {
            Comparable[] row = t9.getRow(rowIndex);
            for (int columnIndex = 0; columnIndex < t9.columnCount(); columnIndex++) {
                int value = (Integer) row[columnIndex];
                assertEquals(expected[rowIndex][columnIndex], value);
            }
        }

        // No columns in common
        Table t4 = parseTable(FileIO.read("examples/t4.tbl"));
        Table cartesianProduct = t4.join(t7);
        assertEquals(t4.columnCount() + t7.columnCount(),
                     cartesianProduct.columnCount());
        assertEquals(t4.rowCount() * t7.rowCount(),
                     cartesianProduct.rowCount());

        // No values in common columns
        Table dummy = new Table(new String[] {"a", "x"},
                                new Type[] {Type.INT, Type.INT});
        dummy.insert(new Comparable[] {9, Type.INT.getNAN()});
        Table noCommonValues = dummy.join(t4);
        assertEquals(3, noCommonValues.columnCount());
        assertEquals(0, noCommonValues.rowCount());
    }
}
