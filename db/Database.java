package db;

import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;

public class Database {
    private Map<String, Table> tables;

    public Database() {
        tables = new HashMap<>();
    }

    public String transact(String query) {
        Matcher match = Parser.parseQuery(query);
        if (match == null) {
            System.out.println("<no match>");
        }
        else {
            System.out.println("Command: " + match.group("command"));
        }
        return "YOUR CODE HERE";
    }
}
