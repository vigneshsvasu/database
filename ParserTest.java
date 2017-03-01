import static org.junit.Assert.*;
import org.junit.Test;

import java.io.BufferedReader;

// TODO: remove these imporots
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import db.Parser;
import db.Table;
import db.Type;
import db.Value;

public class ParserTest {
    @Test
    public void testEmptyTableConstruction() {
        // TODO: replace with `FileIO`
        Table table = null;
        try {
            FileReader fileReader = new FileReader(new File("examples/teams.tbl"));
            BufferedReader reader = new BufferedReader(fileReader);
            table = Parser.parseTable(reader);
        } catch (IOException e) {
            System.out.println(e);
        } catch (db.Parser.InvalidSyntaxException e) {
            System.out.println(e);
        }

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
        org.junit.runner.JUnitCore.main("ParserTest");
    }
}
