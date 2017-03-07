package db.tests;

import static org.junit.Assert.*;
import org.junit.Test;

import db.Column;
import static db.Column.TableColumn;
import static db.Column.TemporaryColumn;

import db.Operator;
import static db.Operator.ConcatenationOperator;
import static db.Operator.NumericalOperator;
import static db.Operator.NumericalOperator.AdditionOperator;
import static db.Operator.NumericalOperator.SubtractionOperator;
import static db.Operator.NumericalOperator.MultiplicationOperator;
import static db.Operator.NumericalOperator.DivisionOperator;

import db.Type;

import db.DatabaseException;

public class OperatorTest {
    private static final double TOLERANCE = 2 * Double.MIN_VALUE;

    @Test
    public void testAddition() throws DatabaseException {
        double[] data = new double[] {1, 1.5, 2, -1, -2};

        TableColumn left = new TableColumn<Double>("x", Type.FLOAT);
        for (double x : data) {
            left.append(x);
        }
        Column right = new TemporaryColumn<Double>(Type.FLOAT, -0.5, left.length());

        Column<Double> sum = new AdditionOperator().apply(
            (Column<Number>) left, (Column<Number>) right, "y");

        assertEquals(5, sum.length());
        for (int index = 0; index < sum.length(); index++) {
            assertEquals(data[index] - 0.5, (double) sum.get(index), TOLERANCE);
        }

        right = new TemporaryColumn<Integer>(Type.INT, 1, left.length());
        sum = new AdditionOperator().apply(
            (Column<Number>) left, (Column<Number>) right, "z");

        assertEquals(5, sum.length());
        for (int index = 0; index < sum.length(); index++) {
            assertEquals(data[index] + 1, (double) sum.get(index), TOLERANCE);
        }
    }
}
