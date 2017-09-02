package codeprocessor;

import static org.junit.Assert.*;
import java.io.File;
import org.junit.*;

import controller.Controller;

@SuppressWarnings("javadoc")
public class CodeProcessorTest {


    @Test
    public void onlyMethodContent() {
        String codeFragment = "System.out.println(\"juhu\");";
        CodeProcessor test = new CodeProcessor(codeFragment);
        assertEquals("DefaultClass", test.getClassName());
        assertEquals(true, test.wasSuccessful());
    }

    @Test
    public void onlyMethod() {
        String codeFragment = "public void Method() {System.out.println(\"juhu\");}";
        CodeProcessor test = new CodeProcessor(codeFragment);
        assertEquals("DefaultClass", test.getClassName());
        assertEquals(true, test.wasSuccessful());
    }

    @Test
    public void completePublicClass() {
        String codeFragment = "public class TestClass {public void Method() {System.out.println(\"juhu\");}}";
        CodeProcessor test = new CodeProcessor(codeFragment);
        assertEquals("TestClass", test.getClassName());
        assertEquals(true, test.wasSuccessful());
    }

    @Test
    public void completeClass() {
        String codeFragment = "class TestClass {public void Method() {System.out.println(\"juhu\");}}";
        CodeProcessor test = new CodeProcessor(codeFragment);
        assertEquals("TestClass", test.getClassName());
        assertEquals(true, test.wasSuccessful());
    }

    @Test
    public void classWithComments() {
        String codeFragment = "/**" + System.lineSeparator()
                + "* ich bin ein javadoc Kommentar und in mir gibt es eine public class Test" + System.lineSeparator()
                + "*/" + System.lineSeparator() + "public class TestClass{" + System.lineSeparator()
                + "// ich bin ein one line comment" + System.lineSeparator() + "public void Method() {"
                + System.lineSeparator() + "/*" + System.lineSeparator() + "* ich bin ein several line comment"
                + System.lineSeparator() + "*/" + System.lineSeparator() + "System.out.println(\"juhu\");"
                + System.lineSeparator() + "}}";
        CodeProcessor test = new CodeProcessor(codeFragment);
        assertEquals("TestClass", test.getClassName());
        assertEquals(true, test.wasSuccessful());
    }

    @Test
    public void easyCases() {
        String code = "public class" + "\t" + "TestCode2 {" + "    public void helloWorld(boolean print) {"
                + "        if (print) {" + "            System.out.println(\"Hello World!\");" + "        } else {"
                + "            System.out.println(\"Not Hello World!\");" + "        }" + "   } }";
        CodeProcessor codeProcessor = new CodeProcessor(code);
        assertEquals("", codeProcessor.getErrorMessage());
        assertEquals("TestCode2", codeProcessor.getClassName());
        assertEquals(true, codeProcessor.wasSuccessful());
    }

    @Test
    public void wrongClassDescription() {
        String codeFragment = "publicX class TestClass {public void Method() {System.out.println(\"juhu\");}}";
        CodeProcessor test = new CodeProcessor(codeFragment);
        assertEquals("TestClass", test.getClassName());
        assertEquals(false, test.wasSuccessful());
        assertNotEquals("", test.getErrorMessage());
    }

    @Test
    public void classWithPackage() {
        String codeFragment = "package controller; public class TestClass {public void Method() {System.out.println(\"juhu\");}}";
        CodeProcessor test = new CodeProcessor(codeFragment);
        assertEquals("TestClass", test.getClassName());
        assertEquals(true, test.wasSuccessful());
        assertEquals("", test.getErrorMessage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void noCode() {
        new CodeProcessor(null);
    }

    @Test
    public void test09() {
        deleteFolder(new File(Controller.getProgramOutputPath()));
        String codeFragment = "package controller; public class TestClass {public void Method() {System.out.println(\"juhu\");}}";
        CodeProcessor test = new CodeProcessor(codeFragment);
        assertEquals("TestClass", test.getClassName());
        assertEquals(true, test.wasSuccessful());
        assertEquals("", test.getErrorMessage());
    }

    private void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
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

    @Test(expected = IllegalArgumentException.class)
    public void invalidJavaSyntax1() {
        String codeFragment = "package test class TestClass";
        new CodeProcessor(codeFragment);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidJavaSyntax2() {
        String codeFragment = "package test class TestClass;";
        new CodeProcessor(codeFragment);
    }

    @Test(expected = IllegalStateException.class)
    public void invalidJavaSyntax3() {
        String codeFragment = "class TestClass";
        new CodeProcessor(codeFragment);
    }

    @Test
    public void genericClass() {
        String codeFragment = "public class TestClass<String> {public void Method() {System.out.println(\"juhu\");}}";
        CodeProcessor test = new CodeProcessor(codeFragment);
        assertEquals("TestClass", test.getClassName());
        assertEquals(true, test.wasSuccessful());
        assertEquals("", test.getErrorMessage());
    }
}
