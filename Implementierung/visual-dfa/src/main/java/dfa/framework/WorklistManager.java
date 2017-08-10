package dfa.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorklistManager { 

    private static final String WL_NAME_NAIVE = "naive";
    private static final String WL_NAME_RANDOM = "random";

    private static WorklistManager singleInstance;
    
    private List<String> worklistNames;
    
    private WorklistManager() {
        List<String> wlNames = new ArrayList<>();
        wlNames.add(WL_NAME_NAIVE);
        wlNames.add(WL_NAME_RANDOM);
        worklistNames = Collections.unmodifiableList(wlNames);
    }

    public static WorklistManager getInstance() {
        if (singleInstance == null) {
            singleInstance = new WorklistManager();
        }

        return singleInstance;
    }
    
    public List<String> getWorklistNames() {
        return worklistNames;
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
