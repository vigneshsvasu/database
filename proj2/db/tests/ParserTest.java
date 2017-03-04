package db.tests;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;

import db.Parser;
import db.Table;
import db.Type;
import db.Value;
import db.FileIO;
import db.DatabaseException;

public class ParserTest {
    @Test
    public void testEmptyTableConstruction() throws IOException, DatabaseException {
        Table table = Parser.parseTable(FileIO.read("examples/teams.tbl"));

        assertNotEquals(table, null);
        assertTrue(table instanceof Table);

        assertEquals(table.columnCount(), 6);
        assertEquals(table.rowCount(), 6);

        String[] teamNames = new String[] {"Mets", "Steelers", "Patriots",
                                           "Cloud9", "EnVyUs", "Golden Bears"};
        String[] years = new String[] {"1962", "1933", "1960", "2012", "2007", "1886"};

        assertEquals(table.getType(1), Type.STRING);  // City
        assertEquals(table.getType(3), Type.INT);  // Year established

        int index = 0;
        for (Value[] row : table) {
            assertEquals(row[0].toString(), '\'' + teamNames[index] + '\'');
            assertEquals(row[3].toString(), years[index]);
            index++;
        }
    }

    public static void main(String args[]) {
        org.junit.runner.JUnitCore.main("db.tests.ParserTest");
    }
}
