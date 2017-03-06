package db;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public interface Column<T extends Comparable<T>> extends Iterable<T> {
    Type getType();
    int length();
    T get(int index);

    class TableColumn<T extends Comparable<T>> implements Column<T> {
        private final String name;
        private final Type type;
        private final List<T> values;

        public TableColumn(String name, Type type) {
            this.name = name;
            this.type = type;
            values = new ArrayList<>();
        }

        public TableColumn(String name, Type type, T[] values) {
            this(name, type);
            for (T value : values) {
                this.values.add(value);
            }
        }

        public void append(T item) {
            values.add(item);
        }

        public String getName() {
            return name;
        }

        public TableColumn copy() {
            TableColumn copy = new TableColumn(name, type);
            for (T item : values) {
                copy.append(item);
            }
            return copy;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof TableColumn &&
                   ((TableColumn) other).getName().equals(name);
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
    }

    class TemporaryColumn<T extends Comparable<T>> implements Column<T> {
        private final Type type;
        private final T value;
        private final int length;

        public TemporaryColumn(Type type, T value, int length) {
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
