package dfa.framework;

import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public enum TaintAnalysisTag implements Tag {
    TAINT_TAG("taint-nalysis-tag:taint"), CLEAN_TAG("taint-analysis-tag:clean"), SENSITIVE_TAG("taint-nalysis-tag:sensitive");
    
    private final String name;
    
    private TaintAnalysisTag(String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public byte[] getValue() throws AttributeValueException {
        return null;
    }
    
}