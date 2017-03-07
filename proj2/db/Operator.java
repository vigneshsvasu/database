package db;

import static db.Column.TableColumn;

public interface Operator<T> {
    T apply(T left, T right, Type resultType) throws DatabaseException;

    public static interface NumericalOperator extends Operator<Number> {
        Integer applyInt(Number left, Number right);
        Double applyFloat(Number left, Number right);

        @Override
        default public Number apply(Number left, Number right, Type resultType)
                throws DatabaseException {
            switch (resultType) {
                case INT:
                    return applyInt(left, right);
                case FLOAT:
                    return applyFloat(left, right);
                default:
                    throw new DatabaseException("illegal result type");
            }
        }

        public static class AdditionOperator implements NumericalOperator {
            @Override
            public Integer applyInt(Number left, Number right) {
                return left.intValue() + right.intValue();
            }

            @Override
            public Double applyFloat(Number left, Number right) {
                return left.doubleValue() + right.doubleValue();
            }
        }

        public static class SubtractionOperator implements NumericalOperator {
            @Override
            public Integer applyInt(Number left, Number right) {
                return left.intValue() - right.intValue();
            }

            @Override
            public Double applyFloat(Number left, Number right) {
                return left.doubleValue() - right.doubleValue();
            }
        }

        public static class MultiplicationOperator implements NumericalOperator {
            @Override
            public Integer applyInt(Number left, Number right) {
                return left.intValue() * right.intValue();
            }

            @Override
            public Double applyFloat(Number left, Number right) {
                return left.doubleValue() * right.doubleValue();
            }
        }

        public static class DivisionOperator implements NumericalOperator {
            @Override
            public Integer applyInt(Number left, Number right) {
                return left.intValue() / right.intValue();
            }

            @Override
            public Double applyFloat(Number left, Number right) {
                return left.doubleValue() / right.doubleValue();
            }

            @Override
            public Number apply(Number left, Number right, Type resultType) {
                if (right.doubleValue() <= 2*Double.MIN_VALUE) {
                    return resultType.getNaN();
                }
                return apply(left, right, resultType);
            }
        }
    }

    public static class ConcatenationOperator implements Operator<String> {
        @Override
        public String apply(String left, String right, Type resultType)
                throws DatabaseException {
            if (resultType != Type.STRING) {
                throw new DatabaseException("mismatched types");
            }
            return left + right;
        }
    }

    @SuppressWarnings("unchecked")
    default TableColumn apply(Column<T> left, Column<T> right, String resultName)
            throws DatabaseException {
        if (left.length() != right.length()) {
            throw new DatabaseException("columns of different lengths");
        }

        Type leftType = left.getType(), rightType = right.getType();
        Type resultType = null;
        if (leftType == Type.STRING && rightType == Type.STRING) {
            resultType = Type.STRING;
        } else if (leftType.isNumeric && rightType.isNumeric) {
            if (leftType == Type.FLOAT || rightType == Type.FLOAT) {
                resultType = Type.FLOAT;
            } else {
                resultType = Type.INT;
            }
        } else {
            throw new DatabaseException("comparison between numeric and non-numeric types");
        }

        TableColumn result = new TableColumn(resultName, resultType);
        for (int index = 0; index < left.length(); index++) {
            T leftValue = left.get(index), rightValue = right.get(index);

            boolean leftIsNOVALUE = leftType.isNOVALUE((Comparable) leftValue);
            boolean rightIsNOVALUE = rightType.isNOVALUE((Comparable) rightValue);

            if (leftIsNOVALUE && rightIsNOVALUE) {
                result.append((T) resultType.getNOVALUE());
            } else {
                if (leftIsNOVALUE) {
                    leftValue = (T) leftType.getNOVALUE();
                }
                if (rightIsNOVALUE) {
                    rightValue = (T) rightType.getNOVALUE();
                }
                result.append(apply(leftValue, rightValue, resultType));
            }
        }

        return result;
    }

    /*
    private static TableColumn<String> concatenateStringColumns(
            Column<String> leftOperand, Column<String> rightOperand, String resultName)
            throws DatabaseException {
        TableColumn<String> result = new TableColumn<>(resultName, Type.STRING);
        for (int index = 0; index < leftOperand.length(); index++) {
            String leftValue = leftOperand.get(index), rightValue = rightOperand.get(index);
            boolean leftNOVALUE = Type.STRING.isNOVALUE(leftValue),
                rightNOVALUE = Type.STRING.isNOVALUE(rightValue);

            if (leftNOVALUE && rightNOVALUE) {
                result.append((String) Type.STRING.getNOVALUE());
            } else {
                if (leftNOVALUE) {
                    leftValue = (String) Type.STRING.zeroValue();
                }
                if (rightNOVALUE) {
                    rightValue = (String) Type.STRING.zeroValue();
                }
                result.append(leftValue + rightValue);
            }
        }
        return result;
    }

    private static <T extends Number & Comparable<T>> TableColumn<T> addNumericColumns(
            Column<T> leftOperand, Column<T> rightOperand, String resultName)
            throws DatabaseException {
        Type leftType = leftOperand.getType(), rightType = rightOperand.getType();
        Type resultType;
        TableColumn result;
        if (leftType == Type.FLOAT || rightType == Type.FLOAT) {
            resultType = Type.FLOAT;
            result = new TableColumn<Double>(resultName, resultType);
        } else {
            resultType = Type.INT;
            result = new TableColumn<Integer>(resultName, resultType);
        }

        for (int index = 0; index < leftOperand.length(); index++) {
            T leftValue = leftOperand.get(index), rightValue = rightOperand.get(index);
            if (leftType.isNAN(leftValue) || rightType.isNAN(rightValue)) {
                result.append(resultType.getNAN());
            } else {
                boolean leftNOVALUE = leftType.isNOVALUE(leftValue),
                    rightNOVALUE = rightType.isNOVALUE(rightValue);
                if (leftNOVALUE && rightNOVALUE) {
                    result.append(resultType.getNOVALUE());
                } else {
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
        }

        return result;
    }

    private static <T extends Number & Comparable<T>> TableColumn<T> subtractNumericColumns(
            Column<T> leftOperand, Column<T> rightOperand, String resultName)
            throws DatabaseException {
        Type leftType = leftOperand.getType(), rightType = rightOperand.getType();
        Type resultType;
        TableColumn result;
        if (leftType == Type.FLOAT || rightType == Type.FLOAT) {
            resultType = Type.FLOAT;
            result = new TableColumn<Double>(resultName, resultType);
        } else {
            resultType = Type.INT;
            result = new TableColumn<Integer>(resultName, resultType);
        }

        for (int index = 0; index < leftOperand.length(); index++) {
            T leftValue = leftOperand.get(index), rightValue = rightOperand.get(index);
            if (leftType.isNAN(leftValue) || rightType.isNAN(rightValue)) {
                result.append(resultType.getNAN());
            } else {
                boolean leftNOVALUE = leftType.isNOVALUE(leftValue),
                    rightNOVALUE = rightType.isNOVALUE(rightValue);
                if (leftNOVALUE && rightNOVALUE) {
                    result.append(resultType.getNOVALUE());
                } else {
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
        }

        return result;
    }

    private static <T extends Number & Comparable<T>> TableColumn<T> multiplyNumericColumns(
            Column<T> leftOperand, Column<T> rightOperand, String resultName)
            throws DatabaseException {
        Type leftType = leftOperand.getType(), rightType = rightOperand.getType();
        Type resultType;
        TableColumn result;
        if (leftType == Type.FLOAT || rightType == Type.FLOAT) {
            resultType = Type.FLOAT;
            result = new TableColumn<Double>(resultName, resultType);
        } else {
            resultType = Type.INT;
            result = new TableColumn<Integer>(resultName, resultType);
        }

        for (int index = 0; index < leftOperand.length(); index++) {
            T leftValue = leftOperand.get(index), rightValue = rightOperand.get(index);
            if (leftType.isNAN(leftValue) || rightType.isNAN(rightValue)) {
                result.append(resultType.getNAN());
            } else {
                boolean leftNOVALUE = leftType.isNOVALUE(leftValue),
                    rightNOVALUE = rightType.isNOVALUE(rightValue);
                if (leftNOVALUE && rightNOVALUE) {
                    result.append(resultType.getNOVALUE());
                } else {
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
        }

        return result;
    }

    private static <T extends Number & Comparable<T>> TableColumn<T> divideNumericColumns(
            Column<T> leftOperand, Column<T> rightOperand, String resultName)
            throws DatabaseException {
        Type leftType = leftOperand.getType(), rightType = rightOperand.getType();
        Type resultType;
        TableColumn result;
        if (leftType == Type.FLOAT || rightType == Type.FLOAT) {
            resultType = Type.FLOAT;
            result = new TableColumn<Double>(resultName, resultType);
        } else {
            resultType = Type.INT;
            result = new TableColumn<Integer>(resultName, resultType);
        }

        for (int index = 0; index < leftOperand.length(); index++) {
            T leftValue = leftOperand.get(index), rightValue = rightOperand.get(index);
            if (leftType.isNAN(leftValue) || rightType.isNAN(rightValue)) {
                result.append(resultType.getNAN());
            } else {
                boolean leftNOVALUE = leftType.isNOVALUE(leftValue),
                    rightNOVALUE = rightType.isNOVALUE(rightValue);
                if (leftNOVALUE && rightNOVALUE) {
                    result.append(resultType.getNOVALUE());
                } else {
                    if (leftType.isNOVALUE(leftValue)) {
                        leftValue = (T) leftType.zeroValue();
                    }
                    if (rightType.isNOVALUE(rightValue)) {
                        rightValue = (T) rightType.zeroValue();
                    }
                    if (rightValue.intValue() == 0 || rightValue.doubleValue() == 0.0) {
                        result.append(resultType.getNAN());
                    } else if (resultType == Type.FLOAT) {
                        result.append(leftValue.doubleValue() / rightValue.doubleValue());
                    } else {
                        result.append(leftValue.intValue() / rightValue.intValue());
                    }
                }
            }
        }

        return result;
    }

    public static TableColumn add(Column left, Column right, String resultName)
            throws DatabaseException {
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

    public static TableColumn subtract(Column left, Column right, String resultName)
            throws DatabaseException {
        Type leftType = left.getType(), rightType = right.getType();
        if (!(leftType.isNumeric() && rightType.isNumeric())) {
            throw new DatabaseException("incompatible types");
        }
        return subtractNumericColumns(left, right, resultName);
    }

    public static TableColumn multiply(Column left, Column right, String resultName)
            throws DatabaseException {
        Type leftType = left.getType(), rightType = right.getType();
        if (!(leftType.isNumeric() && rightType.isNumeric())) {
            throw new DatabaseException("incompatible types");
        }
        return multiplyNumericColumns(left, right, resultName);
    }

    public static TableColumn divide(Column left, Column right, String resultName)
            throws DatabaseException {
        Type leftType = left.getType(), rightType = right.getType();
        if (!(leftType.isNumeric() && rightType.isNumeric())) {
            throw new DatabaseException("incompatible types");
        }
        return divideNumericColumns(left, right, resultName);
    }
    */
}
