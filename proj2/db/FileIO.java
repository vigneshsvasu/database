package db;

import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;

public class FileIO {
    private static final String TABLE_FILE_EXTENSION = "tbl";

    public static String addExtension(String partialPath) {
        return partialPath + "." + TABLE_FILE_EXTENSION;
    }

    /** References:
     *    - https://docs.oracle.com/javase/7/docs/api/java/io/FileReader.html
     *    - https://docs.oracle.com/javase/7/docs/api/java/io/BufferedReader.html
     */
    public static BufferedReader read(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        return reader;
    }

    /** References:
     *    - https://docs.oracle.com/javase/7/docs/api/java/io/FileWriter.html
     *    - https://docs.oracle.com/javase/7/docs/api/java/io/BufferedWriter.html
     */
    public static void write(String path, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(content, 0, content.length());
        writer.close();
    }
}
