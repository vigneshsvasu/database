package db;

import java.util.Map;
import java.util.HashMap;

public class Database {
    private Map<String, Table> tables;

    public Database() {
        tables = new HashMap<>();
    }

    public String transact(String query) {
        return "YOUR CODE HERE";
    }
}
