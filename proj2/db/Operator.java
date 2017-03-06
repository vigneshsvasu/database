package db;

import static db.Column.TableColumn;

public class Operator {
    private static TableColumn<String> concatenateStringColumns(
            Column<String> leftOperand, Column<String> rightOperand, String resultName) throws DatabaseException {
        TableColumn<String> result = new TableColumn<>(resultName, Type.STRING);
        for (int index = 0; index < leftOperand.length(); index++) {
            String leftValue = leftOperand.get(index), rightValue = rightOperand.get(index);
            if (Type.STRING.isNOVALUE(leftValue)) {
                leftValue = (String) Type.STRING.zeroValue();
            }
            if (Type.STRING.isNOVALUE(rightValue)) {
                rightValue = (String) Type.STRING.zeroValue();
            }
            result.append(leftValue + rightValue);
        }
        return result;
    }

    private static <T extends Number & Comparable<T>> TableColumn<T> addNumericColumns(
            Column<T> leftOperand, Column<T> rightOperand, String resultName) throws DatabaseException {
        Type leftType = leftOperand.getType(), rightType = rightOperand.getType();
        Type resultType;
        TableColumn result;
        if (leftType == Type.FLOAT || rightType == Type.FLOAT) {
            resultType = Type.FLOAT;
            result = new TableColumn<Double>(resultName, resultType);
        }
        else {
            resultType = Type.INT;
            result = new TableColumn<Integer>(resultName, resultType);
        }

        for (int index = 0; index < leftOperand.length(); index++) {
            T leftValue = leftOperand.get(index), rightValue = rightOperand.get(index);
            if (leftType.isNAN(leftValue) || rightType.isNAN(rightValue)) {
                result.append(resultType.getNAN());
            }
            else {
                if (leftType.isNOVALUE(leftValue)) {
                    leftValue = (T) leftType.zeroValue();
                }
                if (rightType.isNOVALUE(rightValue)) {
                    rightValue = (T) rightType.zeroValue();
                }
                if (resultType == Type.FLOAT) {
                    result.append(leftValue.doubleValue() + rightValue.doubleValue());
                } else {
                    result.append(leftValue.intValue() + rightValue.intValue());
                }
            }
        }

        return result;
    }

    private static <T extends Number & Comparable<T>> TableColumn<T> subtractNumericColumns(
            Column<T> leftOperand, Column<T> rightOperand, String resultName) throws DatabaseException {
        Type leftType = leftOperand.getType(), rightType = rightOperand.getType();
        Type resultType;
        TableColumn result;
        if (leftType == Type.FLOAT || rightType == Type.FLOAT) {
            resultType = Type.FLOAT;
            result = new TableColumn<Double>(resultName, resultType);
        }
        else {
            resultType = Type.INT;
            result = new TableColumn<Integer>(resultName, resultType);
        }

        for (int index = 0; index < leftOperand.length(); index++) {
            T leftValue = leftOperand.get(index), rightValue = rightOperand.get(index);
            if (leftType.isNAN(leftValue) || rightType.isNAN(rightValue)) {
                result.append(resultType.getNAN());
            }
            else {
                if (leftType.isNOVALUE(leftValue)) {
                    leftValue = (T) leftType.zeroValue();
                }
                if (rightType.isNOVALUE(rightValue)) {
                    rightValue = (T) rightType.zeroValue();
                }
                if (resultType == Type.FLOAT) {
                    result.append(leftValue.doubleValue() - rightValue.doubleValue());
                } else {
                    result.append(leftValue.intValue() - rightValue.intValue());
                }
            }
        }

        return result;
    }

    private static <T extends Number & Comparable<T>> TableColumn<T> multiplyNumericColumns(
            Column<T> leftOperand, Column<T> rightOperand, String resultName) throws DatabaseException {
        Type leftType = leftOperand.getType(), rightType = rightOperand.getType();
        Type resultType;
        TableColumn result;
        if (leftType == Type.FLOAT || rightType == Type.FLOAT) {
            resultType = Type.FLOAT;
            result = new TableColumn<Double>(resultName, resultType);
        }
        else {
            resultType = Type.INT;
            result = new TableColumn<Integer>(resultName, resultType);
        }

        for (int index = 0; index < leftOperand.length(); index++) {
            T leftValue = leftOperand.get(index), rightValue = rightOperand.get(index);
            if (leftType.isNAN(leftValue) || rightType.isNAN(rightValue)) {
                result.append(resultType.getNAN());
            }
            else {
                if (leftType.isNOVALUE(leftValue)) {
                    leftValue = (T) leftType.zeroValue();
                }
                if (rightType.isNOVALUE(rightValue)) {
                    rightValue = (T) rightType.zeroValue();
                }
                if (resultType == Type.FLOAT) {
                    result.append(leftValue.doubleValue() * rightValue.doubleValue());
                } else {
                    result.append(leftValue.intValue() * rightValue.intValue());
                }
            }
        }

        return result;
    }

    public static TableColumn add(Column left, Column right, String resultName) throws DatabaseException {
        if (left.getType().isNumeric() != right.getType().isNumeric()) {
            throw new DatabaseException("incompatible types");
        }

        assert left.length() == right.length();

        if (left.getType() == Type.STRING && right.getType() == Type.STRING) {
            return concatenateStringColumns(left, right, resultName);
        } else {
            return addNumericColumns(left, right, resultName);
        }
    }

    public static TableColumn subtract(Column left, Column right, String resultName) throws DatabaseException {
        Type leftType = left.getType(), rightType = right.getType();
        if (!(leftType.isNumeric() && rightType.isNumeric())) {
            throw new DatabaseException("incompatible types");
        }
        return subtractNumericColumns(left, right, resultName);
    }

    public static TableColumn multiply(Column left, Column right, String resultName) throws DatabaseException {
        Type leftType = left.getType(), rightType = right.getType();
        if (!(leftType.isNumeric() && rightType.isNumeric())) {
            throw new DatabaseException("incompatible types");
        }
        return multiplyNumericColumns(left, right, resultName);
    }
}
