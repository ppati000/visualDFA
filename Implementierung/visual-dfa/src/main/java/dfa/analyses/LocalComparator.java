package dfa.analyses;

import java.util.Comparator;

import soot.jimple.internal.JimpleLocal;

/**
 * @author Nils Jessen
 * @author Sebastian Rauch
 * 
 *         A {@code Comparator} that compares {@code JimpleLocal}s by their name ({@code getName()}).
 */
public class LocalComparator implements Comparator<JimpleLocal> {

    @Override
    public int compare(JimpleLocal l1, JimpleLocal l2) {
        return l1.getName().compareTo(l2.getName());
    }
    
}
