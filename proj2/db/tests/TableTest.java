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
            rows++;
        }

        assertEquals(rows, table.rowCount());
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
    public void testJoin() {
        // An example from Lab 5
        Table t7 = null, t8 = null;
        try {
            t7 = parseTable(db.FileIO.read("examples/t7.tbl"));
            t8 = parseTable(db.FileIO.read("examples/t8.tbl"));
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Table t9 = t7.join(t8);

        assertEquals(2, t9.rowCount());
        assertEquals(5, t9.columnCount());
    }
}
