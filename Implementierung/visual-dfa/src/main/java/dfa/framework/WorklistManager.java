package dfa.framework;

public class WorklistManager {

    private static final String WL_NAME_NAIVE = "naive";
    private static final String WL_NAME_RANDOM = "random";

    private static WorklistManager singleInstance;

    public static WorklistManager getInstance() {
        if (singleInstance == null) {
            singleInstance = new WorklistManager();
        }

        return singleInstance;
    }

    public Worklist getWorklist(String worklistName, SimpleBlockGraph blockGraph) {
        switch (worklistName) {
        case WL_NAME_NAIVE:
            return new NaiveWorklist();
        case WL_NAME_RANDOM:
            return new RandomWorklist();
        default:
            throw new IllegalArgumentException("unknown worklist name: " + worklistName);
        }
    }

}
