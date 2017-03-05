package db;

public interface BinaryColumnExpression {
    Value apply(Value left, Value right);
}
