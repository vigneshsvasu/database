package db;

/**
 * Created by vigneshvasu on 3/5/17.
 */
public interface Condition {
    boolean apply(Value left, Value right);
}
