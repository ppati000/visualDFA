package dfa.analyses;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import soot.PrimType;
import soot.Type;
import soot.jimple.internal.JimpleLocal;

/**
 * @author Sebastian Rauch
 * 
 *         A {@code TaintElement} is a {@code LatticeElement} for Taint-Analysis.
 *
 */
public class TaintElement extends LocalMapElement<TaintElement.Value> {

    /**
     * Determines whether a certain type of Local is accepted (can be contained in) a {@code TaintElement}.
     * 
     * @param local
     *        the {@code JimpleLocal} in question
     * @return {@code true} if the given {@code JimpleLocal} is accepted, {@code false} otherwise
     */
    public static boolean isLocalTypeAccepted(Type type) {
        // for now we only handle primitive types - this may be extended to arbitrary types in the future
        return type instanceof PrimType;
    }

    public TaintElement(Map<JimpleLocal, Value> localMap) {
        super(localMap, LocalMapElement.DEFAULT_COMPARATOR);
    }

    @Override
    public String getStringRepresentation() {
        if (getLocalMap().isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<JimpleLocal, Value>> entryIt = getLocalMap().entrySet().iterator();
        Map.Entry<JimpleLocal, Value> entry = entryIt.next();
        sb.append(entry.getKey().getName()).append(": ").append(entry.getValue());
        
        while (entryIt.hasNext()) {
            entry = entryIt.next();
            sb.append('\n');
            sb.append(entry.getKey().getName()).append(": ").append(entry.getValue());
        }
        
        return sb.toString();
    }

    public static class Value {

        private TaintState taintState;
        
        private boolean violated;
        
        public Value(TaintState taintState, boolean violated) {
            setTaintState(taintState);
            setViolated(violated);
        }
        
        public boolean wasViolated() {
            return violated;
        }
        
        public void setViolated(boolean violated) {
            this.violated = violated;
        }
        
        public TaintState getTaintState() {
            return taintState;
        }
        
        public void setTaintState(TaintState taintState) {
            this.taintState = taintState;
        }
        
        public boolean equals(Object o) {
            if (! (o instanceof Value)) {
                return false;
            }
            
            Value val = (Value) o;
            return getTaintState() == val.getTaintState() && wasViolated() == val.wasViolated();
        }
        
        public String toString() {
            if (wasViolated()) {
                return taintState + " (v)";
            } else {
                return taintState.toString();
            }
        }
    }
    
    
    public enum TaintState {
        // TODO use proper bottom-symbol
        TAINTED("tainted"), CLEAN("clean"), BOTTOM("B");
        
        private String description;
        
        private TaintState(String description) {
            this.description = description;
        }
        
        public String toString() {
            return description;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TaintElement)) {
            return false;
        }

        TaintElement e = (TaintElement) o;
        return getLocalMap().equals(e.getLocalMap());        
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocalMap());
    }

    @Override
    public LocalMapElement<Value> clone() {
        return new TaintElement(getLocalMap());
    }

}
