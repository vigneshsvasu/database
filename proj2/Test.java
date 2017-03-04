public class Test {
    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main(
            "db.tests.FileIOTest",
            "db.tests.ParserTest",
            "db.tests.TableTest");
    }
}
