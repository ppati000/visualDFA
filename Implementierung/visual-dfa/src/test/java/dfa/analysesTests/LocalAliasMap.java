package dfa.analysesTests;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soot.Local;
import soot.jimple.internal.JimpleLocal;

public class LocalAliasMap<V> {

    private Set<Local> locals;
    
    private Map<String, String> aliasMap = new HashMap<String, String>();
    
    public LocalAliasMap(Collection<Local> locals) {
        this.locals = new HashSet<Local>(locals);
    }
    
    public void setAlias(String origName, String alias) {
        boolean containsOrigName = false;
        for (Local l : locals) {
            if (l.getName().equals(origName)) {
                containsOrigName = true; 
                break;
            }
        }
        
        if (containsOrigName) {
            aliasMap.put(alias, origName);
        }
    }
    
    public String getOriginalName(String alias) {
        return aliasMap.get(alias);
    }
    
    public V getValueByAliasOrOriginalName(String name, Map<JimpleLocal, V> map) {
        JimpleLocal local = getLocalByAliasOrName(name);
        return map.get(local);
    }
    
    /* just assume all Locals are JimpleLocals */
    public JimpleLocal getLocalByAliasOrName(String name) {
        Local byOrigName = getLocalByOrigName(name);
        
        if (byOrigName != null) {
            return (JimpleLocal) byOrigName;
        }
        
        String translatedName = aliasMap.get(name);
        if (translatedName == null) {
            return null;
        }
        
        return (JimpleLocal) getLocalByOrigName(translatedName);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder("the set aliases are:\n");
        
        Set<Map.Entry<String, String>> entries = aliasMap.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            sb.append(entry.getKey()).append(" -> ").append(entry.getValue()).append("; ");
        }
        
        return sb.toString();
    }
    
    private Local getLocalByOrigName(String origName) {
        for (Local l : locals) {
            if (l.getName().equals(origName)) {
                return l;
            }
        }
        
        return null;
    }
    
    

}
