package db.tests;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.File;

import db.Database;

/** High-level system-wide tests of database transactions */
public class DatabaseTest {
    @Test
    public void testTableStorageOnDisk() {
        Database db = new Database();

        String message = db.transact("load examples/t1");
        assertTrue(message.isEmpty());
        String repr = "x int,y int\n2,5\n8,3\n13,7";
        assertEquals(repr, db.transact("print t1"));

        message = db.transact("store t1");
        assertTrue(message.isEmpty());
        File tableFile = new File("t1.tbl");
        assertTrue(tableFile.exists());

        db.transact("drop table t1");
        db.transact("load t1");
        assertEquals(repr, db.transact("print t1"));
        tableFile.delete();
    }
}
