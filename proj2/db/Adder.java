package db;

public class Adder implements BinaryColumnExpression {
    public Value apply(Value left, Value right){
        return null;
    }
    public Value apply (FloatValue left, FloatValue right) {
        Value newVal;
        newVal = new FloatValue(left.getValue() + right.getValue());
        return newVal;
    }
    public Value apply(IntValue left, IntValue right){
        Value newVal;
        newVal = new IntValue(((int)left.getValue()) + ((int)right.getValue()));
        return newVal;
        }
    public Value apply(IntValue left, FloatValue right){
        Value newVal;
        newVal = new FloatValue(((float)left.getValue()) + right.getValue());
        return newVal;
    }
    public Value apply(FloatValue left, IntValue right){
        Value newVal;
        newVal = new FloatValue(left.getValue() + ((float)right.getValue()));
        return newVal;
    }
    public Value apply(StringValue left, StringValue right){
        Value newVal;
        newVal = new StringValue(left.getValue() + right.getValue());
        return newVal;
    }

}
