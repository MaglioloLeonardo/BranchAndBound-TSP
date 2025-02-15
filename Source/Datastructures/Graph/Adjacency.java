package Source.Datastructures.Graph;

public class Adjacency {
    private GraphNode graphNode;
    private float weight;

    public Adjacency(GraphNode graphNode, float weight){
        this.graphNode = graphNode;
        this.weight = weight;
    }

    public GraphNode getNode() {
        return graphNode;
    }

    public float getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "|"  + graphNode + ", " + weight + "|";
    }
}
