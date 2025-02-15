package Source;

import Source.Datastructures.Graph.Graph;
import Source.Datastructures.Graph.GraphNode;
import Source.TSPLib.parser.TspLibParser;
import Source.TSPLib.datamodel.tsp.Tsp;

public class TSPLIBGraphGenerator
{
    public static Graph createGraph(String pathToFile) {
        //parse pathToFile from TSPLIBParser
        Tsp tsp =  TspLibParser.parseTsp(pathToFile);
        Graph graph = new Graph(false);
        //for each node in TSP, add a node to the graph
        tsp.getNodes().ifPresent(nodes -> nodes.forEach(node -> graph.addNode(new NodeGraph(node.getId(), node.getX(), node.getY()))));
        //calculate edge weights matrix from TSP
        int[][] edgeWeights = tsp.getEdgeWeightData().orElseThrow(() -> new IllegalArgumentException("No edge weight data found"));

        //for each edge in TSP, add an edge to the graph with the corresponding weight but the graph is undirected so don't add the edge twice
        for (int i = 1; i <= edgeWeights.length; i++) {
            for (int j = 1; j <= edgeWeights[i-1].length; j++) {
                if (i < j) {
                    graph.addEdge(graph.getNodeById(i), graph.getNodeById(j), edgeWeights[i-1][j-1]);
                }
            }
        }

        return graph;
    }

    //generate main method to test the code
    public static void main(String[] args) {
        Graph graph = createGraph("TSPLIB/bayg29.tsp");
        System.out.println(graph);
    }

    public static class NodeGraph implements GraphNode {
        private int id;
        private double x, y;

        public NodeGraph(int id, double x, double y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }

        public int getID() {
            return id;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }
}
