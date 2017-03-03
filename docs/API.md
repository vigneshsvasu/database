# Database API

* Class `Table`
  * `public Table(String[] names, Type[] types)`: Construct a `Table` instance with the given column `names` and `types`
    * Precondition: `names` and `types` have the same length, and the number of columns is at least one
  * `public Iterator<Value[]> iterator()`: Get an `Iterator<Value[]>` over the rows of the `Table`
  * `public int columnCount()`: Get the number of columns
  * `public int rowCount()`: Get the number of rows
  * `public String toString()`: Get a `String` representation of the table
  * `public Table join(Table other)`: Perform a nondestructive natural join with another table, and return the result

* Class `Parser`
  * `public static Matcher parseQuery(String query)` -- Match the given `query` to a command pattern, and return the match. If the `query` matches no pattern, return `null`.
  * `public static Table constructEmptyTable(String[] columnMetadata)` -- Given an array of `<name> <type>` pairs as strings, construct an empty table with those column names and types
  * `public static void populateRow(Table table, String row)` -- Parse the comma-separated values given by `row` and insert them into the table
  * `public static Table parseTable(BufferedReader reader)` -- Construct a populated table from the contents of the `reader`
