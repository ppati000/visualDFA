package gui.visualgraph;

import dfa.framework.DFAExecution;

public class GraphBatchExportThread extends Thread {
    private DFAExecution dfa;
    private double scale;
    private boolean includeLineSteps;
    private GraphExportCallback callback;
    private GraphExporter graphExporter;

    public GraphBatchExportThread(GraphExporter graphExporter, DFAExecution dfa, double scale, boolean includeLineSteps, GraphExportCallback callback) {
        this.dfa = dfa;
        this.scale = scale;
        this.includeLineSteps = includeLineSteps;
        this.callback = callback;
        this.graphExporter = graphExporter;
    }

    public void run() {
        graphExporter.batchExportAsync(dfa, scale, includeLineSteps, callback);
    }
}
