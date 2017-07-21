import gui.visualgraph.*;

import static org.junit.Assert.*;

import org.junit.*;

public class GraphUIControllerTest {
    private VisualGraphPanel panel;
    private GraphUIController controller;

    @Before
    public void createPanel() {
        panel = new VisualGraphPanel();
        controller = new GraphUIController(panel);
    }

    @Test(expected = IllegalStateException.class)
    public void refreshShouldNotBePossibleBeforeStart() {
        controller.refresh();
    }

    @Test
    public void shouldCreateGraphOnStart() {
        // TODO: Use example code to start a concrete DFAFramework analysis.
        // Stuff to be tested: right blocks, properly connected edges, right text, right association to DFA block.
    }
}
