package db.tests;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.regex.Matcher;

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
    public void testIllegalTableNames() {
        Matcher match = Parser.parseQuery("load dir/_illegal_table");
        assertEquals(null, match);
        match = Parser.parseQuery("store table.tbl");
        assertEquals(null, match);
        match = Parser.parseQuery("insert into 3table values (5, 5)");
        assertEquals(null, match);
    }

    @Test
    public void testLoadCommandMatching() {
        Matcher match = Parser.parseQuery(" load \t  _dir/example_table__ \t");
        assertNotEquals(null, match);
        assertEquals("load", match.group("command"));
        assertEquals("_dir/example_table__", match.group(2));
    }

    @Test
    public void testStoreCommandMatching() {
        Matcher match = Parser.parseQuery("  store\t\t  Example__Table\t  ");
        assertNotEquals(null, match);
        assertEquals("store", match.group(1));
        assertEquals("Example__Table", match.group(2));
    }

    @Test
    public void testSelectCommandMatching() {
        Matcher match;

        match = Parser.parseQuery("select x from y");
        assertNotEquals(null, match);
        assertEquals("select", match.group("command"));
        assertEquals("x", match.group(2));
        assertEquals("y", match.group(3));
        assertEquals(null, match.group(4));  // No conditions

        match = Parser.parseQuery(
            "\tselect \t x , y,z from  w where u>0 and v<  w  ");
        assertNotEquals(null, match);
        assertEquals("select", match.group("command"));
        assertEquals("x , y,z", match.group("columns"));
        assertEquals("w", match.group("tables"));
        assertEquals("u>0 and v<  w", match.group("conditions"));
    }

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
}
