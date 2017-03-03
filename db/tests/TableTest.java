package db.tests;

import static org.junit.Assert.*;
import org.junit.Test;

import static db.Parser.parseTable;
import db.Table;
import db.Type;
import db.Value;
import db.IntValue;
import db.FloatValue;
import db.StringValue;

public class TableTest {
    @Test
    public void testInsert() {
        Value[] values = new Value[] {new StringValue("First"),
                                      new IntValue(2), new FloatValue(-1.5)};
        String[] columnNames = new String[] {"Name", "Count", "Balance"};
        Type[] columnTypes = new Type[] {Type.STRING, Type.INT, Type.FLOAT};
        Table table = new Table(columnNames, columnTypes);
        table.insert(values);
    }

    @Test
    public void testJoin() {
        // An example from Lab 5
        Table t7 = null, t8 = null;
        try {
            t7 = parseTable(db.FileIO.read("examples/t7"));
            t8 = parseTable(db.FileIO.read("examples/t8"));
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Table t9 = t7.join(t8);
        System.out.println(t9);
    }

    public static void main(String args[]) {
        org.junit.runner.JUnitCore.main("db.tests.TableTest");
    }
}
