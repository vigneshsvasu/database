package db;

public interface Condition<T extends Number & Comparable<T>> {
    boolean apply(T left, T right);

    class EqualToCondition<T extends Number & Comparable<T>> implements Condition<T> {
        @Override
        public boolean apply(T left, T right) {
            return left.compareTo(right) == 0;
        }
    }

    class NotEqualToCondition<T extends Number & Comparable<T>> implements Condition<T> {
        @Override
        public boolean apply(T left, T right) {
            return left.compareTo(right) != 0;
        }
    }

    class LessThanCondition<T extends Number & Comparable<T>> implements Condition<T> {
        @Override
        public boolean apply(T left, T right) {
            return left.compareTo(right) < 0;
        }
    }

    class LessThanOrEqualToCondition<T extends Number & Comparable<T>> implements Condition<T> {
        @Override
        public boolean apply(T left, T right) {
            return left.compareTo(right) <= 0;
        }
    }

    class GreaterThanCondition<T extends Number & Comparable<T>> implements Condition<T> {
        @Override
        public boolean apply(T left, T right) {
            return left.compareTo(right) > 0;
        }
    }

    class GreaterThanOrEqualToCondition<T extends Number & Comparable<T>> implements Condition<T> {
        @Override
        public boolean apply(T left, T right) {
            return left.compareTo(right) >= 0;
        }
    }
}
