package db.tests;

import db.DatabaseException;
import db.BinaryColumnExpression;
import db.Adder;
import db.Multiplier;
import db.Subtract;
import db.Divide;
import db.Equals;
import db.Greater;
import db.Value;
import db.IntValue;
import db.FloatValue;
import org.junit.Test;
import db.Negate;
import db.Adder;
import db.StringValue;

import java.io.BufferedReader;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by vigneshvasu on 3/5/17.
 */
public class ConditionsTest {
    @Test
    public void testAdder() {
        IntValue x = new IntValue(4);
        IntValue y = new IntValue(5);
        FloatValue a = new FloatValue(2.3);
        FloatValue b = new FloatValue(4.7);
        Adder newAdder = new Adder();
        IntValue test = new IntValue(9);
        assertEquals(test, newAdder.apply(x,y));
        FloatValue test1 = new FloatValue(2.3 + 4.7);
        assertEquals(test1, newAdder.apply(a,b));
        FloatValue test2 = new FloatValue(4 + 2.3);
        assertEquals(test2, newAdder.apply(x,a));
        StringValue newStr = new StringValue("float");
        StringValue newLtr = new StringValue("int");
        StringValue test3 = new StringValue("float" + "int");
        assertEquals(test3, newAdder.apply(newStr,newLtr));
    }

    @Test
    public void testMultiplier(){
        IntValue x = new IntValue(4);
        IntValue y = new IntValue(5);
        FloatValue a = new FloatValue(2.3);
        FloatValue b = new FloatValue(4.7);
        Multiplier newMult = new Multiplier();
        IntValue test = new IntValue(20);
        assertEquals(test, newMult.apply(x,y));
        FloatValue test1 = new FloatValue(2.3 * 4.7);
        assertEquals(test1, newMult.apply(a,b));
    }

    @Test
    public void testSubtract(){
        IntValue x = new IntValue(4);
        IntValue y = new IntValue(5);
        FloatValue a = new FloatValue(2.3);
        FloatValue b = new FloatValue(4.7);
        Subtract newMult = new Subtract();
        IntValue test = new IntValue(-1);
        assertEquals(test, newMult.apply(x,y));
        FloatValue test1 = new FloatValue(2.3 - 4.7);
        assertEquals(test1, newMult.apply(a,b));
    }

    @Test
    public void testDivide(){
        IntValue x = new IntValue(4);
        IntValue y = new IntValue(5);
        FloatValue a = new FloatValue(2.3);
        FloatValue b = new FloatValue(4.7);
        Divide newMult = new Divide();
        IntValue test = new IntValue(4/5);
        assertEquals(test, newMult.apply(x,y));
        FloatValue test1 = new FloatValue(2.3/4.7);
        assertEquals(test1, newMult.apply(a,b));
    }

    @Test
    public void testNegate(){
        IntValue x = new IntValue(7);
        IntValue test = new IntValue(-7);
        Negate newNeg = new Negate();
        x = newNeg.apply(x);
        assertEquals(test, x);
    }

    @Test
    public void testGreater(){
        IntValue x = new IntValue(7);
        IntValue y = new IntValue(10);
        IntValue z = new IntValue(7);
        FloatValue a = new FloatValue(5.3);
        Equals newEq = new Equals();
        assertEquals(true, newEq.apply(x,z));
        Greater newG = new Greater();
        assertEquals(false, newG.apply(a, x));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("db.tests.ConditionsTest");
    }
}
