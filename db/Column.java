package db;

import java.util.List;
import java.util.ArrayList;

class Column {
    private String name;
    private Type type;
    private List<Value> values;

    Column(String name, Type type) {
        this.name = name;
        this.type = type;
        this.values = new ArrayList<>();
    }
}
