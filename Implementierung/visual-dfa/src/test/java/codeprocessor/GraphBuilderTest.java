package codeprocessor;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import dfa.framework.SimpleBlockGraph;

/**
 * @author Anika Nietzer Several Test for the class GraphBuilder
 *
 */
public class GraphBuilderTest {
    
    CodeProcessor codeProcessor1; //easy fragment
    CodeProcessor codeProcessor2; //fragment with more cases
    CodeProcessor codeProcessor3; //same name as fragment one but different code

    /**
     * 
     */
    @Before
    public void before() {
        //@formatter:off
        String code = "public class CodeFragment1 {"
                +"// State-of-the-art, modern and highly scalable implementation of Hello World" 
                +    System.lineSeparator() 
                + "    public void helloWorld(boolean print) {" 
                + "        if (print) {"
                + "            System.out.println(\"Hello World!\");" 
                + "        } else {"
                + "            System.out.println(\"Not Hello World!\");" 
                + "        }" 
                + "    }}";
        //@formatter:on
        codeProcessor1 = new CodeProcessor(code);
        //@formatter:off
        String code2 = 
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
        //@formatter:on
        codeProcessor2 = new CodeProcessor(code2);
        //@formatter:off
        String code3 = 
                " public class CodeFragment3 {"
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
                + "            if (print) {"
                + "                x = 0; "
                + "             } else {"
                + "                 System.out.println(\"Not Hello World!\");" 
                + "             } "
                + "         }" 
                + "    }}";
        //@formatter:on
        codeProcessor3 = new CodeProcessor(code3);
    }

    /**
     * Test for {@code GraphBuilder} with one case
     */
    @Test
    public void test01() {
        GraphBuilder builder = new GraphBuilder(codeProcessor1.getPath(), codeProcessor1.getClassName());
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
    public void test02() {
        GraphBuilder builder = new GraphBuilder(codeProcessor2.getPath(), codeProcessor2.getClassName());
        SimpleBlockGraph blockGraph = builder.buildGraph(builder.getMethods(new NoFilter()).get(1));
        assertEquals(7, blockGraph.size());
        assertEquals(false, blockGraph.getBlocks().isEmpty());
        assertEquals(1, blockGraph.getBlocks().get(1).getPreds().size());
        assertEquals(1, blockGraph.getBlocks().get(1).getSuccs().size());
    }

    /**
     * test with same class names
     */
    @Test
    public void test03() {
        GraphBuilder builder = new GraphBuilder(codeProcessor2.getPath(), codeProcessor2.getClassName());
        SimpleBlockGraph blockGraph = builder.buildGraph(builder.getMethods(new NoFilter()).get(1));
        assertEquals(7, blockGraph.size());
        assertEquals(false, blockGraph.getBlocks().isEmpty());
        assertEquals(1, blockGraph.getBlocks().get(1).getPreds().size());
        assertEquals(1, blockGraph.getBlocks().get(1).getSuccs().size());
        
        GraphBuilder builder2 = new GraphBuilder(codeProcessor3.getPath(), codeProcessor3.getClassName());
        SimpleBlockGraph blockGraph2 = builder2.buildGraph(builder2.getMethods(new NoFilter()).get(1));
        assertEquals(9, blockGraph2.size());
        assertEquals(false, blockGraph2.getBlocks().isEmpty());
        assertEquals(1, blockGraph2.getBlocks().get(1).getPreds().size());
        assertEquals(1, blockGraph2.getBlocks().get(1).getSuccs().size());
    }
    
    
    /**
     * Test with some more cases
     */
    @Test
    public void test04() {
        GraphBuilder builder = new GraphBuilder(codeProcessor2.getPath(), codeProcessor2.getClassName());
        SimpleBlockGraph blockGraph = builder.buildGraph(builder.getMethods(new StandardFilter()).get(1));
        assertEquals(7, blockGraph.size());
        assertEquals(false, blockGraph.getBlocks().isEmpty());
        assertEquals(1, blockGraph.getBlocks().get(1).getPreds().size());
        assertEquals(1, blockGraph.getBlocks().get(1).getSuccs().size());
    }
    
    /**
     * Test with some more cases
     */
    @Test
    public void test05() {
        GraphBuilder builder = new GraphBuilder(codeProcessor2.getPath(), codeProcessor2.getClassName());
        SimpleBlockGraph blockGraph = builder.buildGraph(builder.getMethods(new StandardFilter()).get(1));
        assertEquals(7, blockGraph.size());
        assertEquals(false, blockGraph.getBlocks().isEmpty());
        assertEquals(1, blockGraph.getBlocks().get(1).getPreds().size());
        assertEquals(1, blockGraph.getBlocks().get(1).getSuccs().size());
    }

}
