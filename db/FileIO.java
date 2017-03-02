package db;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

public class FileIO{
    private static final String EXT = ".tbl";
    public static BufferedReader readerInput(String fileName)
            throws FileNotFoundException {
        fileName = fileName + EXT;
        File newFile = new File(fileName);
        FileReader fa = new FileReader(newFile);
        BufferedReader newReader = new BufferedReader(fa);
        return newReader;
    }
    public static void readerOutput(String s, String fileName)
            throws FileNotFoundException, IOException {
        File f = new File(fileName);
        OutputStream stream = new FileOutputStream(f);
        final byte[] bytes = s.getBytes();
        stream.write(bytes);
    }
}
