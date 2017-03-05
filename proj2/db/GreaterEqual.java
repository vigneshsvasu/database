package db;

/**
 * Created by vigneshvasu on 3/5/17.
 */
public class GreaterEqual implements Condition{
    public boolean apply(Value left, Value right){
        return false;
    }
    public boolean apply(FloatValue left, FloatValue right){
        double leftVal = left.getValue();
        double rightVal = right.getValue();
        return leftVal >= rightVal;
    }
    public boolean apply(IntValue left, IntValue right){
        int leftVal = left.getValue();
        int rightVal = right.getValue();
        return leftVal >= rightVal;
    }
    public boolean apply(FloatValue left, IntValue right){
        double newVal = left.getValue();
        double other = (double) right.getValue();
        return newVal >= other;
    }
    public boolean apply(IntValue left, FloatValue right){
        double newVal = right.getValue();
        double other = (double) left.getValue();
        return newVal <= other;
    }
    public boolean apply(StringValue left, StringValue right){
        String lef = left.getValue();
        String rig = right.getValue();
        int val = lef.compareTo(rig);
        if (val > -1){
            return true;
        }
        return false;
    }
}
