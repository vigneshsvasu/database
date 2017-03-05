package db;

/**
 * Created by vigneshvasu on 3/5/17.
 */
public class Negate implements UnaryColumnExpression {
    public Value apply(Value x){
        return null;
    }
    public FloatValue apply(FloatValue x){
        FloatValue changedVal;
        double val = x.getValue();
        val = -val;
        changedVal = new FloatValue(val);
        return changedVal;
    }
    public IntValue apply(IntValue x){
        IntValue changedVal;
        int val = x.getValue();
        val = -val;
        changedVal = new IntValue(val);
        return changedVal;
    }
}
