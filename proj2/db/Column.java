package db;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public interface Column<T extends Comparable<T>> extends Iterable<T> {
    Type getType();
    int length();
    T get(int index);

    public static abstract class NonuniformColumn<T extends Comparable<T>> implements Column<T> {
        private final Type type;
        private final List<T> values;

        public NonuniformColumn(Type type) {
            this.type = type;
            values = new ArrayList<>();
        }

        public NonuniformColumn(Type type, T[] values) {
            this(type);
            for (T value : values) {
                this.values.add(value);
            }
        }

        public void append(T item) {
            values.add(item);
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public int length() {
            return values.size();
        }

        @Override
        public T get(int index) {
            return values.get(index);
        }

        @Override
        public Iterator<T> iterator() {
            return values.iterator();
        }

        public static class IntColumn extends NonuniformColumn<Integer> {
            private final Integer NAN = new Integer(-1);

            public IntColumn() {
                super(Type.INT);
            }

            public IntColumn(Integer[] values) {
                super(Type.INT, values);
            }
        }

        public static class FloatColumn extends NonuniformColumn<Double> {
            public FloatColumn() {
                super(Type.FLOAT);
            }
        }

        public static class StringColumn extends NonuniformColumn<String> {
            public StringColumn() {
                super(Type.STRING);
            }
        }
    }

    public static class UniformColumn<T extends Comparable<T>> implements Column<T> {
        private final Type type;
        private final T value;
        private final int length;

        public UniformColumn(Type type, T value, int length) {
            this.type = type;
            this.value = value;
            this.length = length;
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public int length() {
            return length;
        }

        @Override
        public T get(int index) {
            if (!(0 <= index && index < length)) {
                throw new IndexOutOfBoundsException();
            }
            return value;
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                private int index = 0;

                @Override
                public boolean hasNext() {
                    return 0 <= index && index < length;
                }

                @Override
                public T next() {
                    index++;
                    return value;
                }
            };
        }
    }
}
