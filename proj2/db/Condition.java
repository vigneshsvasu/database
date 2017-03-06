package db;

public interface Condition<T extends Comparable<T>> {
    boolean apply(T left, T right);

    class EqualToCondition<T extends Comparable<T>> implements Condition<T> {
        @Override
        public boolean apply(T left, T right) {
            return left.compareTo(right) == 0;
        }
    }

    class NotEqualToCondition<T extends Comparable<T>> implements Condition<T> {
        @Override
        public boolean apply(T left, T right) {
            return left.compareTo(right) != 0;
        }
    }

    class LessThanCondition<T extends Comparable<T>> implements Condition<T> {
        @Override
        public boolean apply(T left, T right) {
            return left.compareTo(right) < 0;
        }
    }

    class LessThanOrEqualToCondition<T extends Comparable<T>> implements Condition<T> {
        @Override
        public boolean apply(T left, T right) {
            return left.compareTo(right) <= 0;
        }
    }

    class GreaterThanCondition<T extends Comparable<T>> implements Condition<T> {
        @Override
        public boolean apply(T left, T right) {
            return left.compareTo(right) > 0;
        }
    }

    class GreaterThanOrEqualToCondition<T extends Comparable<T>> implements Condition<T> {
        @Override
        public boolean apply(T left, T right) {
            return left.compareTo(right) >= 0;
        }
    }
}
