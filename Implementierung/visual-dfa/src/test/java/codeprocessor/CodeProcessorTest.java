package codeprocessor;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * Test for {@code CodeProcessor}
 * 
 * @author Anika Nietzer
 *
 */
public class CodeProcessorTest {

    /**
     * Test to check wrap of a java class signature and a method signature.
     */
    @Test
    public void codeFragment() {
        String codeFragment = "System.out.println(\"juhu\");";
        CodeProcessor test = new CodeProcessor(codeFragment);
        assertEquals("DefaultClass", test.getClassName());
        assertEquals(true, test.wasSuccessful());
    }

    /**
     * Test to check wrap of a java class signature.
     */
    @Test
    public void codeFragmentwithMethod() {
        String codeFragment = "public void Method() {System.out.println(\"juhu\");}";
        CodeProcessor test = new CodeProcessor(codeFragment);
        assertEquals("DefaultClass", test.getClassName());
        assertEquals(true, test.wasSuccessful());
    }

    /**
     * Test to check complete class
     */
    @Test
    public void codeFragmentwithMethodAndClass() {
        String codeFragment = "public class TestClass {public void Method() {System.out.println(\"juhu\");}}";
        CodeProcessor test = new CodeProcessor(codeFragment);
        assertEquals("TestClass", test.getClassName());
        assertEquals(true, test.wasSuccessful());
    }

    /**
     * Test to check code with line separators.
     */
    @Test
    public void codeFragmentWithLineSeparators() {
        String codeFragment = "public class TestClass {" + System.lineSeparator() + "public void Method() {"
                + System.lineSeparator() + "System.out.println(\"juhu\");" + System.lineSeparator() + "}}";
        CodeProcessor test = new CodeProcessor(codeFragment);
        System.out.println(test.getErrorMessage());
        assertEquals("TestClass", test.getClassName());
        assertEquals(true, test.wasSuccessful());
    }

    /**
     * Test to check deletion of comments
     */
    @Test
    public void codeFragmentWithLineSeparatorsAndSeveralComments() {
        String codeFragment = "/**" + System.lineSeparator()
                + "* ich bin ein javadoc Kommentar und in mir gibt es eine public class Test" + System.lineSeparator()
                + "*/" + System.lineSeparator() + "public class TestClass{" + System.lineSeparator()
                + "// ich bin ein one line comment" + System.lineSeparator() + "public void Method() {"
                + System.lineSeparator() + "/*" + System.lineSeparator() + "* ich bin ein several line comment"
                + System.lineSeparator() + "*/" + System.lineSeparator() + "System.out.println(\"juhu\");"
                + System.lineSeparator() + "}}";
        CodeProcessor test = new CodeProcessor(codeFragment);
        System.out.println(test.getErrorMessage());
        assertEquals("TestClass", test.getClassName());
        assertEquals(true, test.wasSuccessful());
    }

    /**
     * Test to check some easy case analysis.
     */
    @Test
    public void codeFragmentWithCases() {
        String code = "// State-of-the-art, modern and highly scalable implementation of Hello World\n"
                + "    public void helloWorld(boolean print) {" + "        if (print) {"
                + "            System.out.println(\"Hello World!\");" + "        } else {"
                + "            System.out.println(\"Not Hello World!\");" + "        }" + "    }";
        CodeProcessor codeProcessor = new CodeProcessor(code);
        assertEquals("", codeProcessor.getErrorMessage());
        assertEquals("DefaultClass", codeProcessor.getClassName());
        assertEquals(true, codeProcessor.wasSuccessful());
    }
}
