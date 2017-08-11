package dfa.analyses;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import soot.jimple.internal.JimpleLocal;

public abstract class LocalMapElementJoinHelper<V, E extends LocalMapElement<V>> {
    
    public E performJoin(Set<E> elements) {
        if (elements.isEmpty()) {
            throw new IllegalArgumentException("there must be at least one element to join");
        }
        
        if (! doLocalsMatch(elements)) {
            throw new IllegalArgumentException("loclas not matching");
        }
        
        Iterator<E> it = elements.iterator();
        E refElement = it.next();
        Map<JimpleLocal, V> refMap = refElement.getLocalMap();
        Set<Entry<JimpleLocal, V>> entries = refMap.entrySet();
        
        @SuppressWarnings("unchecked")
        E result = (E) refElement.clone();  // assume clone() returns the same type
        for (Entry<JimpleLocal, V> entry : entries) {
            JimpleLocal local = entry.getKey();
            V localJoinResult = doValueJoin(elements, local);
            result.setValue(local, localJoinResult);
        }
        
        return result;
    }
    
    public abstract V doValueJoin(Set<E> elements, JimpleLocal local);
    
    
    private boolean doLocalsMatch(Set<E> elements) {
        Iterator<? extends LocalMapElement<V>> it = elements.iterator();
        LocalMapElement<V> refElement = it.next();
        Map<JimpleLocal, V> refMap = refElement.getLocalMap();
        while (it.hasNext()) {
            LocalMapElement<V> compElement = it.next();
            Map<JimpleLocal, V> compMap = compElement.getLocalMap();
            
            for (Map.Entry<JimpleLocal, V> entry : refMap.entrySet()) {
                if (!compMap.containsKey(entry.getKey())) {
                    return false;
                }
            }

            for (Map.Entry<JimpleLocal, V> entry : compMap.entrySet()) {
                if (!refMap.containsKey(entry.getKey())) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
}
