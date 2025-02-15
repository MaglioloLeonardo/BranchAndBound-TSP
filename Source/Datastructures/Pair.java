package Source.Datastructures;

public class Pair<F, S> {
    public final F first;
    public final S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    //get first element of the pair
    public F getFirst() {
        return first;
    }

    //get second element of the pair
    public S getSecond() {
        return second;
    }
}