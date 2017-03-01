import org.junit.Test;
import db.Table;
import db.Type;
import static org.junit.Assert.*;
/**
 * Created by vigneshvasu on 2/26/17.
 */
public class TestTable {
    @Test
    public void testOne(){
        String[] values = new String[4];
        values[0] = "\"Toyota\"";
        values[1] = "NAN";
        values[2] = "NOVALUE";
        values[3] = "7";
        String[] names = new String[] {"Col1", "Col2", "Col3", "Col4"};
        Type[] types = new Type[] {Type.STRING, Type.STRING, Type.INT, Type.INT};
        Table newTable = new Table(names,types);
        newTable.insert(values);
        assertEquals(newTable.toString().substring(0, 7), "Toyota");
    }

}
