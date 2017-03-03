package db.tests;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;

import db.FileIO;
import db.Table;
import db.Type;
import static db.Parser.InvalidSyntaxException;

import db.Value;
import db.IntValue;
import db.FloatValue;
import db.StringValue;

public class FileIOTest{
    @Test
    public void testRead() throws IOException, InvalidSyntaxException {
        BufferedReader reader = FileIO.read("examples/t7");
        Table table = db.Parser.parseTable(reader);

        assertEquals(table.columnCount(), 4);
        assertEquals(table.rowCount(), 3);
    }

    @Test
    public void testWrite() throws IOException {
        Table table = new Table(new String[] {"name", "count", "balance"},
                                new Type[] {Type.STRING, Type.INT, Type.FLOAT});
        table.insert(new Value[] {new StringValue("First"), new IntValue(5),
                                  new FloatValue(-2.5)});

        assertEquals(1, table.rowCount());
        assertEquals(3, table.columnCount());

        FileIO.write("tmp", table.toString());

        File tableFile = new File("tmp.tbl");
        assertTrue(tableFile.exists());
        tableFile.delete();
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("db.tests.FileIOTest");
    }
}
