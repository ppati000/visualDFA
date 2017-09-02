package codeprocessor;

import static org.junit.Assert.*;
import java.io.File;
import org.junit.*;

import controller.Controller;

/**
 * Test for {@code CodeProcessor}
 * 
 * @author Anika Nietzer
 *
 */
public class CodeProcessorTest {

    private String programOutputPath;

    @Before
    public void setup() {
        Controller controller = new Controller();
        this.programOutputPath = controller.getProgramOutputPath();

    }

    /**
     * Test to check wrap of a java class signature and a method signature.
     */
    @Test
    public void test01() {
        Controller controller = new Controller();
        String codeFragment = "System.out.println(\"juhu\");";
        CodeProcessor test = new CodeProcessor(codeFragment, programOutputPath);
        assertEquals("DefaultClass", test.getClassName());
        assertEquals(true, test.wasSuccessful());
    }

    /**
     * Test to check wrap of a java class signature.
     */
    @Test
    public void test02() {
        String codeFragment = "public void Method() {System.out.println(\"juhu\");}";
        CodeProcessor test = new CodeProcessor(codeFragment, programOutputPath);
        assertEquals("DefaultClass", test.getClassName());
        assertEquals(true, test.wasSuccessful());
    }

    /**
     * Test to check complete class
     */
    @Test
    public void test03() {
        String codeFragment = "public class TestClass {public void Method() {System.out.println(\"juhu\");}}";
        CodeProcessor test = new CodeProcessor(codeFragment, programOutputPath);
        assertEquals("TestClass", test.getClassName());
        assertEquals(true, test.wasSuccessful());
    }

    /**
     * Test to check complete class
     */
    @Test
    public void test03b() {
        String codeFragment = "class TestClass {public void Method() {System.out.println(\"juhu\");}}";
        CodeProcessor test = new CodeProcessor(codeFragment, programOutputPath);
        assertEquals("TestClass", test.getClassName());
        assertEquals(true, test.wasSuccessful());
    }

    /**
     * Test to check deletion of comments
     */
    @Test
    public void test04() {
        String codeFragment = "/**" + System.lineSeparator()
                + "* ich bin ein javadoc Kommentar und in mir gibt es eine public class Test" + System.lineSeparator()
                + "*/" + System.lineSeparator() + "public class TestClass{" + System.lineSeparator()
                + "// ich bin ein one line comment" + System.lineSeparator() + "public void Method() {"
                + System.lineSeparator() + "/*" + System.lineSeparator() + "* ich bin ein several line comment"
                + System.lineSeparator() + "*/" + System.lineSeparator() + "System.out.println(\"juhu\");"
                + System.lineSeparator() + "}}";
        CodeProcessor test = new CodeProcessor(codeFragment, programOutputPath);
        assertEquals("TestClass", test.getClassName());
        assertEquals(true, test.wasSuccessful());
    }

    /**
     * Test to check some easy case analysis.
     */
    @Test
    public void test05() {
        String code = "public class" + "\t" + "TestCode2 {" + "    public void helloWorld(boolean print) {"
                + "        if (print) {" + "            System.out.println(\"Hello World!\");" + "        } else {"
                + "            System.out.println(\"Not Hello World!\");" + "        }" + "   } }";
        CodeProcessor codeProcessor = new CodeProcessor(code, programOutputPath);
        assertEquals("", codeProcessor.getErrorMessage());
        assertEquals("TestCode2", codeProcessor.getClassName());
        assertEquals(true, codeProcessor.wasSuccessful());
    }

    /**
     * Test to check complete class
     */
    @Test
    public void test06() {
        String codeFragment = "publicX class TestClass {public void Method() {System.out.println(\"juhu\");}}";
        CodeProcessor test = new CodeProcessor(codeFragment, programOutputPath);
        assertEquals("TestClass", test.getClassName());
        assertEquals(false, test.wasSuccessful());
        assertNotEquals("", test.getErrorMessage());
    }

    /**
     * Test to check complete class
     */
    @Test
    public void test07() {
        String codeFragment = "package controller; public class TestClass {public void Method() {System.out.println(\"juhu\");}}";
        CodeProcessor test = new CodeProcessor(codeFragment, programOutputPath);
        assertEquals("TestClass", test.getClassName());
        assertEquals(true, test.wasSuccessful());
        assertEquals("", test.getErrorMessage());
    }

    /**
     * Test to check complete class
     */
    @Test(expected = IllegalArgumentException.class)
    public void test08() {
        new CodeProcessor(null, programOutputPath);
    }

    /**
     * Test to check complete class
     */
    @Test
    public void test09() {
        File dir = new File(System.getProperty("user.home") + System.getProperty("file.separator") + "visualDfa"
                + System.getProperty("file.separator"));
        deleteFolder(dir);
        String codeFragment = "package controller; public class TestClass {public void Method() {System.out.println(\"juhu\");}}";
        CodeProcessor test = new CodeProcessor(codeFragment, programOutputPath);
        assertEquals("TestClass", test.getClassName());
        assertEquals(true, test.wasSuccessful());
        assertEquals("", test.getErrorMessage());
    }

    private void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { // some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    /**
     * Test to check complete class
     */
    @Test(expected = IllegalArgumentException.class)
    public void test10() {
        String codeFragment = "package test class TestClass";
        new CodeProcessor(codeFragment, programOutputPath);
    }

    /**
     * Test to check complete class
     */
    @Test(expected = IllegalArgumentException.class)
    public void test11() {
        String codeFragment = "package test class TestClass;";
        new CodeProcessor(codeFragment, programOutputPath);
    }

    /**
     * Test to check complete class
     */
    @Test(expected = IllegalStateException.class)
    public void test12() {
        String codeFragment = "class TestClass";
        new CodeProcessor(codeFragment, programOutputPath);
    }

    /**
     * Test to check complete class
     */
    @Test
    public void test13() {
        String codeFragment = "public class TestClass<String> {public void Method() {System.out.println(\"juhu\");}}";
        CodeProcessor test = new CodeProcessor(codeFragment, programOutputPath);
        assertEquals("TestClass", test.getClassName());
        assertEquals(true, test.wasSuccessful());
        assertEquals("", test.getErrorMessage());
    }

}
