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
        Database db = new Database();

        db.transact("load examples/t8");
        db.transact("insert into t8 values (NAN, -1, 2)");
        db.transact("insert into t8 values (NOVALUE, NOVALUE, 2)");

        String message = db.transact("select w + 5 as f, b - 0.5 as g from t8");
        assertEquals("f int,g float\n6,6.500\n12,6.500\n6,8.500\n6,10.500\nNAN,-1.500\n5,-0.500", message);

        db.transact("load examples/fans");
        db.transact("insert into fans values (NOVALUE, NOVALUE, 'Mets')");
        message = db.transact("select Firstname +Lastname as Fullname from fans "
            + "where Fullname >= 'M' and Fullname<='MitasRay'");
        assertEquals("Fullname string\n'MauriceLee'\n'MauriceLee'\n'MitasRay'", message);
    }

    @Test
    public void testSelectWithJoin() {
        Database db = new Database();

        db.transact("load examples/t1");
        db.transact("load examples/t2");

        String message = db.transact("select * from t1, t2");
        assertEquals("x int,y int,z int\n2,5,4\n8,3,9", message);
    }

    @Test
    public void testConditionalSelect() {
        Database db = new Database();

        db.transact("load examples/t8");
        db.transact("insert into t8 values (NOVALUE, NOVALUE, NOVALUE)");
        db.transact("insert into t8 values (NAN, NAN, NAN)");

        String message = db.transact("select w from t8 where w >= NAN");
        assertEquals("w int\nNAN", message);
        message = db.transact("select w from t8 where w<7");
        assertEquals("w int\n1\n1\n1", message);
    }
}
