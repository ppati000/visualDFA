package codeprocessor;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import dfa.framework.SimpleBlockGraph;

@SuppressWarnings("javadoc")
public class GraphBuilderTest {

    CodeProcessor codeProcessor1; // easy fragment
    CodeProcessor codeProcessor2; // fragment with more cases
    CodeProcessor codeProcessor3; // same name as fragment one but different
                                  // code

    @Before
    public void setup() {
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

    @Test
    public void buildGraphTwoCases() {
        GraphBuilder builder = new GraphBuilder(codeProcessor1.getPath(), codeProcessor1.getClassName());
        SimpleBlockGraph blockGraph = builder.buildGraph(builder.getMethods(new Filter()).get(1));
        assertEquals(4, blockGraph.size());
        assertEquals(false, blockGraph.getBlocks().isEmpty());
        assertEquals(1, blockGraph.getBlocks().get(1).getPreds().size());
        assertEquals(1, blockGraph.getBlocks().get(1).getSuccs().size());
    }

    @Test
    public void buildGraphFourCases() {
        GraphBuilder builder = new GraphBuilder(codeProcessor2.getPath(), codeProcessor2.getClassName());
        SimpleBlockGraph blockGraph = builder.buildGraph(builder.getMethods(new Filter()).get(1));
        assertEquals(7, blockGraph.size());
        assertEquals(false, blockGraph.getBlocks().isEmpty());
        assertEquals(1, blockGraph.getBlocks().get(1).getPreds().size());
        assertEquals(1, blockGraph.getBlocks().get(1).getSuccs().size());
    }

    @Test
    public void buildGraphTwice() {
        GraphBuilder builder = new GraphBuilder(codeProcessor2.getPath(), codeProcessor2.getClassName());
        SimpleBlockGraph blockGraph = builder.buildGraph(builder.getMethods(new Filter()).get(1));
        assertEquals(7, blockGraph.size());
        assertEquals(false, blockGraph.getBlocks().isEmpty());
        assertEquals(1, blockGraph.getBlocks().get(1).getPreds().size());
        assertEquals(1, blockGraph.getBlocks().get(1).getSuccs().size());

        GraphBuilder builder2 = new GraphBuilder(codeProcessor3.getPath(), codeProcessor3.getClassName());
        SimpleBlockGraph blockGraph2 = builder2.buildGraph(builder2.getMethods(new Filter()).get(1));
        assertEquals(9, blockGraph2.size());
        assertEquals(false, blockGraph2.getBlocks().isEmpty());
        assertEquals(1, blockGraph2.getBlocks().get(1).getPreds().size());
        assertEquals(1, blockGraph2.getBlocks().get(1).getSuccs().size());
    }

}