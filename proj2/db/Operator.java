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
}
