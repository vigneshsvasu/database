package db;

/**
 * Created by vigneshvasu on 3/5/17.
 */
public interface UnaryColumnExpression {
    Value apply(Value x);
}
