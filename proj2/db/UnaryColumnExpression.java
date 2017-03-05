package db;

/**
 * Created by vigneshvasu on 3/5/17.
 */
public interface UnaryColumnExpression {
    FloatValue apply(FloatValue x);
    IntValue apply(IntValue x);
}
