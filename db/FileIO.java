package db;

import java.io.IOException;

// Input
import java.io.FileReader;
import java.io.BufferedReader;

// Output
import java.io.FileWriter;
import java.io.BufferedWriter;

public class FileIO {
    private static final String TABLE_FILE_EXTENSION = "tbl";

    // https://docs.oracle.com/javase/7/docs/api/java/io/FileReader.html
    // https://docs.oracle.com/javase/7/docs/api/java/io/BufferedReader.html
    public static BufferedReader read(String partialPath) throws IOException {
        String path = partialPath + "." + TABLE_FILE_EXTENSION;
        BufferedReader reader = new BufferedReader(new FileReader(path));
        return reader;
    }

    // https://docs.oracle.com/javase/7/docs/api/java/io/FileWriter.html
    // https://docs.oracle.com/javase/7/docs/api/java/io/BufferedWriter.html
    public static void write(String partialPath, String content) throws IOException {
        String path = partialPath + "." + TABLE_FILE_EXTENSION;
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(content, 0, content.length());
        writer.close();
    }
}
