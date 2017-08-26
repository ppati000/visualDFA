package dfa.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The {@code WorklistManager} keeps track of all the available {@code Worklist}s and can create empty
 * {@code Worklist}-instances given the name of a {@code Worklist}.
 * 
 * @author Sebastian Rauch
 *
 */
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

    /**
     * Returns a {@code WorklistManager}.
     * 
     * @return a {@code WorklistManager}
     */
    public static WorklistManager getInstance() {
        if (singleInstance == null) {
            singleInstance = new WorklistManager();
        }

        return singleInstance;
    }

    /**
     * Returns a {@code List} of all the names of the available {@code Worklist}s.
     * 
     * @return a {@code List} of all the names of the available {@code Worklist}s
     */
    public List<String> getWorklistNames() {
        return worklistNames;
    }

    /**
     * Returns an empty {@code Worklist} for the given name and a {@code SimpleBlockGraph}.
     * 
     * @param worklistName
     *        the name of the {@code Worklist} to be returned
     * @param blockGraph
     *        the {@code SimpleBlockGraph} the {@code Worklist} should be used for
     * @return an empty {@code Worklist}
     */
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
