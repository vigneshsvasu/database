package db;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringJoiner;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;

import static db.Column.TableColumn;
import static db.Column.TemporaryColumn;
import static db.Condition.*;

public class Table implements Iterable<Comparable[]> {
    @SuppressWarnings("unchecked")
    private class RowIterator implements Iterator<Comparable[]> {
        private Iterator<Comparable>[] columnIterators;

        private RowIterator() {
            columnIterators = new Iterator[columnCount()];
            for (int index = 0; index < columnIterators.length; index++) {
                columnIterators[index] = columns[index].iterator();
            }
        }

        @Override
        public boolean hasNext() {
            for (Iterator<Comparable> iter : columnIterators) {
                if (!iter.hasNext()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public Comparable[] next() {
            Comparable[] row = new Comparable[columnIterators.length];
            for (int index = 0; index < columnIterators.length; index++) {
                row[index] = columnIterators[index].next();
            }
            return row;
        }

        @Override
        public void remove() {
            for (int index = 0; index < columnIterators.length; index++) {
                columnIterators[index].remove();
            }
        }
    }

    private final TableColumn[] columns;
    private final Map<String, TableColumn> columnLookupTable;

    public Table(String[] names, Type[] types) {
        assert names.length == types.length;
        columns = new TableColumn[names.length];
        columnLookupTable = new HashMap<>();

        for (int index = 0; index < columns.length; index++) {
            columns[index] = new TableColumn(names[index], types[index]);
            columnLookupTable.put(names[index], columns[index]);
        }
    }

    private Table(TableColumn[] columns) {
        assert columns.length != 0;
        this.columns = columns;
        columnLookupTable = new HashMap<>();
        for (TableColumn column : columns) {
            columnLookupTable.put(column.getName(), column);
        }
    }

    @Override
    public Iterator<Comparable[]> iterator() {
        return new RowIterator();
    }

    public int columnCount() {
        return columns.length;
    }

    public int rowCount() {
        return columns[0].length();
    }

    public String getName(int index) {
        return columns[index].getName();
    }

    public Type getType(int index) {
        return columns[index].getType();
    }

    private int indexOf(String name) {
        for (int index = 0; index < columns.length; index++) {
            if (columns[index].getName().equals(name)) {
                return index;
            }
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    public void insert(Comparable[] row) {
        assert row.length == columns.length;
        for (int index = 0; index < row.length; index++) {
            columns[index].append(row[index]);
        }
    }

    public Comparable[] getRow(int rowIndex) {
        Comparable[] row = new Comparable[columnCount()];
        for (int columnIndex = 0; columnIndex < row.length; columnIndex++) {
            row[columnIndex] = columns[columnIndex].get(rowIndex);
        }
        return row;
    }

    public String toString() {
        StringJoiner rowJoiner = new StringJoiner(Parser.ROW_DELIMETER);

        StringJoiner columnJoiner = new StringJoiner(Parser.COLUMN_DELIMETER);
        for (TableColumn column : columns) {
            columnJoiner.add(String.format("%s %s", column.getName(),
                             column.getType().name().toLowerCase()));
        }
        rowJoiner.add(columnJoiner.toString());

        for (Comparable[] row : this) {
            columnJoiner = new StringJoiner(Parser.COLUMN_DELIMETER);
            assert row.length == columns.length;
            for (int index = 0; index < row.length; index++) {
                columnJoiner.add(columns[index].getType().repr(row[index]));
            }
            rowJoiner.add(columnJoiner.toString());
        }

        return rowJoiner.toString();
    }

    private List<TableColumn> findCommonTableColumns(Table other) {
        List<TableColumn> commonTableColumns = new ArrayList<>();
        for (TableColumn column : columns) {
            for (TableColumn otherTableColumn : other.columns) {
                if (column.getName().equals(otherTableColumn.getName())) {
                    commonTableColumns.add(new TableColumn(column.getName(),
                                           column.getType()));
                }
            }
        }
        return commonTableColumns;
    }

    private Table constructEmptyJoinedTable(Table other,
            List<TableColumn> commonTableColumns) {
        List<TableColumn> newTableColumns = new ArrayList<>(commonTableColumns);

        for (TableColumn column : columns) {
            if (!newTableColumns.contains(column)) {
                newTableColumns.add(new TableColumn(column.getName(),
                                    column.getType()));
            }
        }

        for (TableColumn otherTableColumn : other.columns) {
            if (!newTableColumns.contains(otherTableColumn)) {
                newTableColumns.add(new TableColumn(otherTableColumn.getName(),
                                    otherTableColumn.getType()));
            }
        }

        TableColumn[] newTableColumnsAsArray = new TableColumn[newTableColumns.size()];
        for (int index = 0; index < newTableColumns.size(); index++) {
            newTableColumnsAsArray[index] = newTableColumns.get(index);
        }
        return new Table(newTableColumnsAsArray);
    }

    private boolean shouldJoin(Comparable[] row, Comparable[] otherRow,
            List<Integer> indices, List<Integer> otherIndices) {
        for (int index = 0; index < indices.size(); index++) {
            if (row[indices.get(index)].compareTo(otherRow[otherIndices.get(index)]) != 0) {
                return false;
            }
        }
        return true;
    }

    private void populateJoinedTable(Table result, Table other,
                                     List<TableColumn> commonTableColumns) {
        List<Integer> indices = new ArrayList<>(commonTableColumns.size());
        List<Integer> otherIndices = new ArrayList<>(commonTableColumns.size());
        for (int index = 0; index < commonTableColumns.size(); index++) {
            TableColumn column = commonTableColumns.get(index);
            indices.add(indexOf(column.getName()));
            otherIndices.add(other.indexOf(column.getName()));
        }

        for (Comparable[] row : this) {
            for (Comparable[] otherRow : other) {
                if (shouldJoin(row, otherRow, indices, otherIndices)) {
                    Comparable[] newRow = new Comparable[result.columnCount()];
                    int runningIndex = 0;

                    for (int index : indices) {
                        newRow[runningIndex++] = row[index];
                    }

                    for (int index = 0; index < columns.length; index++) {
                        if (!indices.contains(index)) {
                            newRow[runningIndex++] = row[index];
                        }
                    }

                    for (int index = 0; index < other.columns.length; index++) {
                        if (!otherIndices.contains(index)) {
                            newRow[runningIndex++] = otherRow[index];
                        }
                    }

                    assert runningIndex == newRow.length;
                    result.insert(newRow);
                }
            }
        }
    }

    public Table join(Table other) {
        List<TableColumn> commonTableColumns = findCommonTableColumns(other);
        Table result = constructEmptyJoinedTable(other, commonTableColumns);
        populateJoinedTable(result, other, commonTableColumns);
        return result;
    }

    public Table select(String[] columnExprs) throws DatabaseException {
        List<TableColumn> selectedColumns = new ArrayList<>();

        for (String columnExpr : columnExprs) {
            Matcher match = Parser.parseColumnExpression(columnExpr);

            if (match == null) {
                int index = indexOf(columnExpr);
                if (index == -1) {
                    throw new DatabaseException(String.format(
                        "no column named \"%s\"", columnExpr));
                } else {
                    selectedColumns.add(columns[index].copy());
                }
            } else {
                String leftOperandName = match.group(1);
                if (!columnLookupTable.containsKey(leftOperandName)) {
                    throw new DatabaseException(String.format(
                        "no column named \"%s\"", leftOperandName));
                }
                TableColumn leftOperand = columnLookupTable.get(leftOperandName);

                String rightOperandRepr = match.group(3);
                Column rightOperand;
                if (!columnLookupTable.containsKey(rightOperandRepr)) {
                    Type type = leftOperand.getType();
                    Comparable value;
                    try {
                        value = Parser.parseValue(rightOperandRepr, type);
                    } catch (NumberFormatException exc) {
                        try {
                            type = Type.FLOAT;
                            value = Parser.parseValue(rightOperandRepr, type);
                        } catch (NumberFormatException exc2) {
                            throw new DatabaseException("could not parse value");
                        }
                    }
                    rightOperand = new TemporaryColumn(type, value, leftOperand.length());
                } else {
                    rightOperand = columnLookupTable.get(rightOperandRepr);
                }

                String operator = match.group(2);
                TableColumn result = null;
                String resultName = match.group(4);
                switch (operator) {
                    case "+":
                        result = Operator.add(leftOperand, rightOperand, resultName);
                        break;
                    case "-":
                        result = Operator.subtract(leftOperand, rightOperand, resultName);
                        break;
                    case "*":
                        result = Operator.multiply(leftOperand, rightOperand, resultName);
                        break;
                    case "/":
                        result = Operator.divide(leftOperand, rightOperand, resultName);
                        break;
                    default:
                        throw new DatabaseException(String.format("no operator \"%c\"", operator));
                }

                selectedColumns.add(result);
            }
        }

        TableColumn[] selectedColumnsArray = new TableColumn[selectedColumns.size()];
        for (int index = 0; index < selectedColumnsArray.length; index++) {
            selectedColumnsArray[index] = selectedColumns.get(index);
        }
        return new Table(selectedColumnsArray);
    }

    private <U extends Comparable<U>> Condition<U> makeCond(String operator)
            throws DatabaseException {
        switch (operator) {
            case "==":
                return new EqualToCondition<>();
            case "!=":
                return new NotEqualToCondition<>();
            case "<":
                return new LessThanCondition<>();
            case "<=":
                return new LessThanOrEqualToCondition<>();
            case ">":
                return new GreaterThanCondition<>();
            case ">=":
                return new GreaterThanOrEqualToCondition<>();
            default:
                throw new DatabaseException("no such comparator");
        }
    }

    private Column<Double> coerce(Column<Integer> old) {
        return new Column<Double>() {
            @Override
            public Type getType() {
                return Type.FLOAT;
            }

            @Override
            public int length() {
                return old.length();
            }

            @Override
            public Double get(int index) {
                return (double) old.get(index);
            }
        };
    }

    private boolean[] compare(Column left, Column right, String operator) throws DatabaseException {
        Condition cond = null;
        boolean[] toKeep = new boolean[left.length()];
        Type leftType = left.getType(), rightType = right.getType(), resultType;
        if (leftType == Type.STRING && rightType == Type.STRING) {
            cond = this.<String>makeCond(operator);
            resultType = Type.STRING;
        } else if (leftType == Type.INT && rightType == Type.INT) {
            cond = this.<Integer>makeCond(operator);
            resultType = Type.INT;
        } else if (leftType == Type.FLOAT || rightType == Type.FLOAT) {
            cond = this.<Double>makeCond(operator);
            if (leftType == Type.INT) {
                left = coerce(left);
            } else {
                right = coerce(right);
            }
            resultType = Type.FLOAT;
        } else {
            throw new DatabaseException("incomparable types");
        }

        for (int index = 0; index < left.length(); index++) {
            Comparable leftValue = left.get(index), rightValue = right.get(index);
            if (resultType.isNOVALUE(leftValue) || resultType.isNOVALUE(rightValue)) {
                toKeep[index] = false;
            } else {
                toKeep[index] = cond.apply(leftValue, rightValue);
            }
        }
        return toKeep;
    }

    public void filter(String[] conditionExprs) throws DatabaseException {
        for (String conditionExpr : conditionExprs) {
            Matcher match = Parser.parseConditionExpression(conditionExpr);

            if (match == null) {
                throw new DatabaseException(String.format(
                    "invalid conditional expression \"%s\"", conditionExpr));
            }

            String leftOperandName = match.group(1);
            String operator = match.group(2);
            String rightOperandRepr = match.group(3);

            if (!columnLookupTable.containsKey(leftOperandName)) {
                throw new DatabaseException(String.format(
                    "no such column \"%s\"", leftOperandName));
            }

            TableColumn leftOperand = columnLookupTable.get(leftOperandName);
            Column rightOperand;
            if (!columnLookupTable.containsKey(rightOperandRepr)) {
                Type type = leftOperand.getType();
                Comparable value;
                try {
                    value = Parser.parseValue(rightOperandRepr, type);
                } catch (NumberFormatException exc) {
                    try {
                        value = Parser.parseValue(rightOperandRepr, Type.FLOAT);
                    } catch (NumberFormatException exc2) {
                        throw new DatabaseException("could not parse value");
                    }
                }
                rightOperand = new TemporaryColumn(type, value, leftOperand.length());
            } else {
                rightOperand = columnLookupTable.get(rightOperandRepr);
            }

            boolean[] toKeep = compare(leftOperand, rightOperand, operator);
            RowIterator rowIterator = new RowIterator();
            for (boolean shouldKeep : toKeep) {
                rowIterator.next();
                if (!shouldKeep) {
                    rowIterator.remove();
                }
            }
        }
    }
}
