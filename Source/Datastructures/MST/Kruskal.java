package Source.Datastructures.MST;
import Source.Datastructures.Graph.*;

import java.util.*;

public class Kruskal {


    public static List<Edge> Mst(Graph G){
        List<GraphNode> vertices = G.getNodes();
        List<Edge> edges = G.getEdges(), toReturn = new ArrayList<>();
        Collections.sort(edges);
        UnionFindSet P = new UnionFindSet();
        List<Node> nodes = P.makeSet(vertices);
        for (Edge edge: edges){
            Node nodeU = Search_node(nodes, edge.getU());
            Node nodeV = Search_node(nodes, edge.getV());
            if(nodeU == null || nodeV == null)throw new Error("Internal error MAKESET");
            if(P.findPartition(nodeU) != P.findPartition(nodeV)){
                toReturn.add(edge);
                P.union(nodeU, nodeV);
            }
        }
        return toReturn;
    }

 public static Graph MSTFor1Tree(Graph graph, Comparator<Edge> comparator, List<Edge> mandatoryEdges, List<Edge> forbiddenEdges) {
        Graph mst = new Graph(false);
        List<Edge> edgeList;
        UnionFindSet unionFind = new UnionFindSet();

        List<Node> nodes = unionFind.makeSet(graph.getNodes());

        edgeList = graph.getEdges();
        edgeList.sort(comparator);

        for (Edge edge : mandatoryEdges) {
            mst.addEdge(edge.getU(), edge.getV(), edge.getWeight());
            Node nodeU = Search_node(nodes, edge.getU());
            Node nodeV = Search_node(nodes, edge.getV());
            if (nodeU == null || nodeV == null) throw new Error("Internal error MAKESET");
            unionFind.union(nodeU, nodeV);
        }

        for (Edge edge : edgeList) {
            if (forbiddenEdges.contains(edge) || forbiddenEdges.contains(edge.reverse()) ||
                    mandatoryEdges.contains(edge) || mandatoryEdges.contains(edge.reverse())) {
                continue;
            }

            Node nodeU = Search_node(nodes, edge.getU());
            Node nodeV = Search_node(nodes, edge.getV());
            if (nodeU == null || nodeV == null) throw new Error("Internal error MAKESET");

            if (!unionFind.findPartition(nodeU).equals(unionFind.findPartition(nodeV))) {
                mst.addEdge(edge.getU(), edge.getV(), edge.getWeight());
                unionFind.union(nodeU, nodeV);
            }
        }

        return mst;
    }




    private static Node Search_node(List<Node> list, Object obj){
        for(Node node: list){
            if(node.getKey().equals(obj))return node;
        }
        return null;
    }

}
