package controller;

import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import codeprocessor.CodeProcessor;
import codeprocessor.Filter;
import codeprocessor.GraphBuilder;
import dfa.framework.AnalysisLoader;
import dfa.framework.DFAFactory;
import dfa.framework.DFAPrecalcController;
import dfa.framework.LatticeElement;
import dfa.framework.SimpleBlockGraph;
import dfa.framework.Worklist;
import dfa.framework.WorklistManager;

public class PrecalcTest {

    private DFAFactory<? extends LatticeElement> factory;
    private Worklist worklist;
    private SimpleBlockGraph simpleBlockGraph;
    private DFAPrecalcController precalcController;
    private Controller controller;

    /**
     * 
     */
    @Before
    public void setup() {
        this.controller = new Controller();
        String worklist = this.controller.getWorklists().get(0);
        String code = "public class CodeFragment1 {"
                + "// State-of-the-art, modern and highly scalable implementation of Hello World"
                + System.lineSeparator() + "    public void helloWorld(boolean print) {" + "        if (print) {"
                + "            System.out.println(\"Hello World!\");" + "        } else {"
                + "            System.out.println(\"Not Hello World!\");" + "        }" + "    }}";
        CodeProcessor processor = new CodeProcessor(code);
        GraphBuilder graphBuilder = new GraphBuilder(processor.getPath(), processor.getClassName());
        this.simpleBlockGraph = graphBuilder.buildGraph(graphBuilder.getMethods(new Filter()).get(1));
        WorklistManager manager = WorklistManager.getInstance();
        this.worklist = manager.getWorklist(worklist, this.simpleBlockGraph);
        this.precalcController = new DFAPrecalcController();
        AnalysisLoader loader = new AnalysisLoader(System.getProperty("user.dir") + System.getProperty("file.separator")
                + "src" + System.getProperty("file.separator") + "test" + System.getProperty("file.separator")
                + "resources");
        loader.loadAnalyses(Logger.getAnonymousLogger());
        this.factory = loader.getDFAFactory("Constant-Folding");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test01() {
        DFAPrecalculator precalculator = new DFAPrecalculator(null, this.worklist, this.simpleBlockGraph,
                this.precalcController, this.controller);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test02() {
        DFAPrecalculator precalculator = new DFAPrecalculator(this.factory, null, this.simpleBlockGraph,
                this.precalcController, this.controller);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test03() {
        DFAPrecalculator precalculator = new DFAPrecalculator(this.factory, this.worklist, null,
                this.precalcController, this.controller);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test04() {
        DFAPrecalculator precalculator = new DFAPrecalculator(this.factory, this.worklist, this.simpleBlockGraph,
                null, this.controller);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test05() {
        DFAPrecalculator precalculator = new DFAPrecalculator(this.factory, this.worklist, this.simpleBlockGraph,
                this.precalcController, null);
        
        
    }
    

}
