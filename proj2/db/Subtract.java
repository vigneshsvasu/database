package db;

public class Subtract implements BinaryColumnExpression {
    public Value apply (Value left, Value right) {
        Value newVal;
        if (left.getClass() == FloatValue.class && right.getClass() == FloatValue.class){
            newVal = new FloatValue(((float)left.getValue()) - ((float)right.getValue()));
        } else if (left.getClass() == IntValue.class && right.getClass() == IntValue.class){
            newVal = new IntValue(((int)left.getValue()) - ((int)right.getValue()));
        } else if (left.getClass() == IntValue.class && right.getClass() == FloatValue.class){
            newVal = new FloatValue(((float)left.getValue()) - ((float)right.getValue()));
        } else if (left.getClass() == FloatValue.class && right.getClass() == IntValue.class){
            newVal = new FloatValue(((float)left.getValue()) - ((float)right.getValue()));
        } else {
            newVal = null;
        }
        return newVal;
    }
}
