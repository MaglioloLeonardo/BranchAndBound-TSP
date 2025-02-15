package Source.Datastructures.Graph;

import java.util.Objects;

public class Edge implements Comparable<Edge>{
    private GraphNode u;
    private GraphNode v;
    private float weight;

    public Edge(GraphNode u, GraphNode v, float weight){
        this.u = u;
        this.v = v;
        this.weight = weight;
    }

    public GraphNode getU(){return u;}

    public GraphNode getV(){return v;}

    public float getWeight() {
        return weight;
    }

    //reverse the edge
    public Edge reverse(){
        return new Edge(v, u, weight);
    }

    public boolean isIncidentFor(int nodeId){
        return u.getID() == nodeId  || v.getID() == nodeId;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return weight == edge.weight &&
                Objects.equals(u, edge.u) &&
                Objects.equals(v, edge.v);
    }

    public String toString() {
        return "("  + u + ", " + v + "|" +  weight + ")";
    }

    public int compareTo(Edge edge){
        if(this.weight >= edge.getWeight()){
            return 1;
        }else{
            return -1;
        }
    }
}
