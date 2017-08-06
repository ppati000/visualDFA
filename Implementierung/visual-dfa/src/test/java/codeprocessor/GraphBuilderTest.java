package codeprocessor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dfa.framework.SimpleBlockGraph;

/**
 * @author Anika Nietzer
 * Several Test for the class GraphBuilder
 *
 */
public class GraphBuilderTest {
    
    /**
     * Test for {@code GraphBuilder} with one case
     */
    @Test
    public void codeFragmentWithCases() {
    String code = "public class CodeFragment1 {"
            +"// State-of-the-art, modern and highly scalable implementation of Hello World" 
            +    System.lineSeparator() 
            + "    public void helloWorld(boolean print) {" + "        if (print) {"
            + "            System.out.println(\"Hello World!\");" + "        } else {"
            + "            System.out.println(\"Not Hello World!\");" + "        }" + "    }}";
        CodeProcessor codeProcessor = new CodeProcessor(code);
        assertEquals("", codeProcessor.getErrorMessage());
        assertEquals("CodeFragment1", codeProcessor.getClassName());
        assertEquals(true, codeProcessor.wasSuccessful());
        GraphBuilder builder = new GraphBuilder(codeProcessor.getPathName(), codeProcessor.getClassName());
        SimpleBlockGraph blockGraph = builder.buildGraph(builder.getMethods(new NoFilter()).get(1));
        assertEquals(4, blockGraph.size());
        assertEquals(false, blockGraph.getBlocks().isEmpty());
        assertEquals(1, blockGraph.getBlocks().get(1).getPreds().size());
        assertEquals(1, blockGraph.getBlocks().get(1).getSuccs().size());
    }
    
    
    /**
     * Test with some more cases
     */
    @Test
    public void codeFragmentWithMoreCases() {
    String code = 
            " public class CodeFragment2 {"
            + "    public void helloWorld(boolean print, int x) {" 
            + "        if (print) {"
            + "            System.out.println(\"Hello World!\");"
            + "             while (x < 10) {"
            + "                 x = x + 1;"
            + "                   if (x == 5) { "
            + "                       System.out.println(\"yes\"); "
            + "                   }"
            + "             }" 
            + "         } else {"
            + "            System.out.println(\"Not Hello World!\");" 
            + "         }" 
            + "    }}";
            
        CodeProcessor codeProcessor = new CodeProcessor(code);
        System.out.println(codeProcessor.getErrorMessage());
        assertEquals(true, codeProcessor.wasSuccessful());
        
        assertEquals("", codeProcessor.getErrorMessage());
        assertEquals("CodeFragment2", codeProcessor.getClassName());
        GraphBuilder builder = new GraphBuilder(codeProcessor.getPathName(), codeProcessor.getClassName());
        SimpleBlockGraph blockGraph = builder.buildGraph(builder.getMethods(new NoFilter()).get(1));
        assertEquals(7, blockGraph.size());
        assertEquals(false, blockGraph.getBlocks().isEmpty());
        assertEquals(1, blockGraph.getBlocks().get(1).getPreds().size());
        assertEquals(1, blockGraph.getBlocks().get(1).getSuccs().size());
    }
    
}
