package dfa.analyses;

/**
 * 
 * @author Sebastian Rauch
 *
 *  A pair of values.
 *
 * @param <V> the type of the values
 */
public class Pair<V> {

    private V val1;
    private V val2;

    public Pair(V val1, V val2) {
        this.val1 = val1;
        this.val2 = val2;
    }

    public V getFirst() {
        return val1;
    }

    public V getSecond() {
        return val2;
    }
    
}
