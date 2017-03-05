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

    @Test
    public void testTableCreation() {
        Database db = new Database();

        String message = db.transact("create table example (x int,y string)");
        assertTrue(message.isEmpty());

        message = db.transact("create table example (z float)");
        assertTrue(message.startsWith("ERROR"));

        // TODO: create table with select
    }

    @Test
    public void testTableDeletion() {
        Database db = new Database();

        String message = db.transact("drop table example");
        assertTrue(message.startsWith("ERROR"));

        message = db.transact("create table example (x int)");
        assertTrue(db.transact("drop table example").isEmpty());
        assertTrue(db.transact("drop table example").startsWith("ERROR"));
    }

    @Test
    public void testInsert() {
        Database db = new Database();

        String message = db.transact("create table example (x int,y string)");
        assertTrue(message.isEmpty());
        assertEquals("x int,y string", db.transact("print example"));

        message = db.transact("insert into example values (NAN, 'Hello')");
        assertTrue(message.isEmpty());

        message = db.transact("insert into example (5, 'World')");
        assertTrue(message.startsWith("ERROR"));

        message = db.transact("print example");
        assertEquals("x int,y string\nNAN,'Hello'", message);

        message = db.transact("insert into example values (-5, NOVALUE)");
        assertTrue(message.isEmpty());

        message = db.transact("print example");
        assertEquals("x int,y string\nNAN,'Hello'\n-5,NOVALUE", message);
    }

    @Test
    public void testSelect() {
        // TODO: when select is done
    }

    @Test
    public void testSelectWithJoin() {
        // TODO: when select is done
    }

    @Test
    public void testConditionalSelect() {
        // TODO: when select is done
    }
}
