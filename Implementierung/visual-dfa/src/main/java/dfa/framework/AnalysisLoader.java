package dfa.framework;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class AnalysisLoader {
    
    private String packageName;
    private String searchPath;
    
    private List<String> analysisNames;
    private Map<String, DFAFactory> analyses;
    
    public AnalysisLoader(String packageName, String searchPath) {
        if (packageName == null) {
            throw new IllegalArgumentException("packageName must not be null");
        }
        
        if (searchPath == null) {
            throw new IllegalArgumentException("searchPath must not be null");
        }
        
        this.packageName = packageName;
        this.searchPath = searchPath;
    }
    
    public String getPackageName(){
        return packageName;
    }
    
    public String getSearchPath() {
        return searchPath;
    }
    
    public void loadAnalyses(Logger logger) {
        // TODO implement
//        throw new UnsupportedOperationException("not yet implemented");
    }
    
    public List<String> getAnalysesNames() {
        if (analysisNames == null) {
            throw new IllegalStateException("no analyses have been loaded");
        }
        
        return Collections.unmodifiableList(analysisNames);
    }
    
    public DFAFactory<LatticeElement> getDFAFactory(String analysisName) {
        if (analysisNames == null) {
            throw new IllegalStateException("no analyses have been loaded");
        }
        
        if (! analysisNames.contains(analysisName)) {
            return null;
        }
        
        return analyses.get(analysisName);
    }

}
