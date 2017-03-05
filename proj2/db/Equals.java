package db;

/**
 * Created by vigneshvasu on 3/5/17.
 */
public class Equals implements Condition {
    public boolean apply(Value left, Value right){
        return false;
    }
    public boolean apply(FloatValue left, FloatValue right){
        return left.equals(right);
    }
    public boolean apply(IntValue left, IntValue right){
        return left.equals(right);
    }
    public boolean apply(FloatValue left, IntValue right){
        double newVal = left.getValue();
        double other = (double) right.getValue();
        return newVal == other;
    }
    public boolean apply(IntValue left, FloatValue right){
        double newVal = right.getValue();
        double other = (double) left.getValue();
        return newVal == other;
    }
    public boolean apply(StringValue left, StringValue right){
        String lef = left.getValue();
        String rig = right.getValue();
        return lef.equals(rig);
    }
}
