package gui.visualgraph;

import dfa.framework.DFAExecution;

public class GraphBatchExportThread extends Thread {
    private DFAExecution dfa;
    private double scale;
    private boolean includeLineSteps;
    private GraphExportCallback callback;

    public GraphBatchExportThread(DFAExecution dfa, double scale, boolean includeLineSteps, GraphExportCallback callback) {
        this.dfa = dfa;
        this.scale = scale;
        this.includeLineSteps = includeLineSteps;
        this.callback = callback;
    }

    public void run() {
        GraphExporter.batchExportAsync(dfa, scale, includeLineSteps, callback);
    }
}
