import org.junit.Test;
import db.FileIO;
import java.io.BufferedReader;
import java.io.IOException;

public class FileIOTest{
    @Test
    public static void testOne() throws IOException {
        BufferedReader newReader = null;
        newReader = FileIO.readerInput("examples/records");
        for (String i = newReader.readLine(); i != null; i = newReader.readLine()){
            System.out.println(i);
        }
    }

    public static void main(String[] args) throws IOException{
        testOne();
    }
}
