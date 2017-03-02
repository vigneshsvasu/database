# Database API

* Class `Parser`
  * `public static Matcher parseQuery(String query)` -- Match the given `query` to a command pattern, and return the match. If the `query` matches no pattern, return `null`.
  * `public static Table constructEmptyTable(String[] columnMetadata)` -- Given an array of `<name> <type>` pairs as strings, construct an empty table with those column names and types
  * `public static void populateRow(Table table, String row)` -- Parse the comma-separated values given by `row` and insert them into the table
  * `public static Table parseTable(BufferedReader reader)` -- Construct a populated table from the contents of the `reader`
