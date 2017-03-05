package db;

public class Adder implements BinaryColumnExpression {
    public Value apply (Value left, Value right) {
        Value newVal;
        if (left.getClass() == FloatValue.class && right.getClass() == FloatValue.class){
            newVal = new FloatValue(((float)left.getValue()) + ((float)right.getValue()));
        } else if (left.getClass() == IntValue.class && right.getClass() == IntValue.class){
            newVal = new IntValue(((int)left.getValue()) + ((int)right.getValue()));
        } else if (left.getClass() == IntValue.class && right.getClass() == FloatValue.class){
            newVal = new FloatValue(((float)left.getValue()) + ((float)right.getValue()));
        } else if (left.getClass() == FloatValue.class && right.getClass() == IntValue.class){
            newVal = new FloatValue(((float)left.getValue()) + ((float)right.getValue()));
        } else if (left.getClass() == StringValue.class && right.getClass() == StringValue.class){
            newVal = new StringValue(((String)left.toString()) + ((String)right.toString()));
        } else {
            newVal = null;
        }
        return newVal;
    }
}
