package Source.BranchAndBoundTSP;

import java.util.stream.Collectors;
import java.util.List;
import java.util.Comparator;
import java.util.ArrayList;

import Source.Datastructures.Graph.Exceptions.NodeNotFoundExceptionGraph;
import Source.Datastructures.Pair;
import Source.Datastructures.Graph.*;

import static Source.Datastructures.MST.Kruskal.MSTFor1Tree;

/**
 * Represents a 1-tree relaxation of the Traveling Salesman Problem (TSP) using the Branch and Bound algorithm.
 */

public class IntermediateProblem implements Comparable<IntermediateProblem>{
    private boolean validSolution;
    private boolean hasHamiltonianCycle;
    private int bound;
    private ArrayList<Edge> excludedEdges;
    private ArrayList<Edge> fixedEdges;
    private Graph initialGraph;
    private Integer treeDepthLevel;
    private GraphNode targetNode;
    private Graph currentOneTree;

    /**
     * Constructs a IntermediateProblem instance with the specified initial graph and target node.
     *
     * @param initialGraph The initial graph representing the TSP.
     * @param targetNode The target node for the TSP.
     */
    public IntermediateProblem(Graph initialGraph, GraphNode targetNode) throws NodeNotFoundExceptionGraph {
        this(initialGraph, new ArrayList<>(0), new ArrayList<>(0), targetNode, 0);
    }


    /**
     * Constructs a IntermediateProblem instance with the specified parameters.
     *
     * @param initialGraph The initial graph representing the TSP.
     * @param fixedEdges The list of fixed edges.
     * @param excludedEdges The list of excluded edges.
     * @param candidateNode The candidate node for the TSP.
     * @param subProblemTreeLevel The depth level of the sub-problem tree.
     */
    public IntermediateProblem(Graph initialGraph,
                               ArrayList<Edge> fixedEdges,
                               ArrayList<Edge> excludedEdges,
                               GraphNode candidateNode,
                               Integer subProblemTreeLevel) throws NodeNotFoundExceptionGraph {
        this.fixedEdges = fixedEdges;
        this.excludedEdges = excludedEdges;
        this.initialGraph = initialGraph;
        this.targetNode = candidateNode;
        this.treeDepthLevel = subProblemTreeLevel;

        this.currentOneTree = constructOneTree();
        this.bound = evaluateOneTreeCost(); //Okay
        this.hasHamiltonianCycle = detectHamiltonianCycle(); //Okay
        this.validSolution = currentOneTree.getNodes().size() == initialGraph.getNodes().size() &&
                currentOneTree.getEdges().size()/2 == initialGraph.getNodes().size();
    }


    /**
     * Constructs a one-tree for the current solution.
     *
     * @return The constructed one-tree graph.
     */
    private Graph constructOneTree() throws NodeNotFoundExceptionGraph {
        ArrayList<Edge> adjustedMandatoryEdges = cloneAndFilterMandatoryEdges();

        Graph minimumSpanningTree = computeMST(adjustedMandatoryEdges);
        List<Edge> nodeMandatoryEdges = getNodeIncidentEdges(fixedEdges, targetNode.getID());
        List<Edge> nodeForbiddenEdges = getNodeIncidentEdges(excludedEdges, targetNode.getID());

        if (minimumSpanningTree.getNodes().size() == initialGraph.getNodes().size()) {
            addLeastExpensiveEdges(minimumSpanningTree, nodeMandatoryEdges, nodeForbiddenEdges);
        }

        return minimumSpanningTree;
    }

    /**
     * Clones and filters the mandatory edges to exclude edges incident to the target node.
     *
     * @return The adjusted list of mandatory edges.
     */
    private ArrayList<Edge> cloneAndFilterMandatoryEdges() {
        ArrayList<Edge> adjustedMandatoryEdges = (ArrayList<Edge>) fixedEdges.clone();

        for (Edge edge : fixedEdges) {
            if (edge.getV().equals(targetNode) || edge.getU().equals(targetNode)) {
                adjustedMandatoryEdges.remove(edge);
            }
        }
        return adjustedMandatoryEdges;
    }

    /**
     * Computes the minimum spanning tree (MST) for the given mandatory edges.
     *
     * @param mandatoryEdges The list of mandatory edges.
     * @return The computed MST graph.
     */
    private Graph computeMST(ArrayList<Edge> mandatoryEdges) throws NodeNotFoundExceptionGraph {
        Graph thisGraph = initialGraph.clone();
        thisGraph.deleteNode(targetNode);
        thisGraph = MSTFor1Tree(thisGraph, EdgeWeightComparator.getInstance(), mandatoryEdges, excludedEdges);
        thisGraph.addNode(targetNode);
        return thisGraph;
    }

    /**
     * Retrieves the edges incident to the specified node from the given list of edges.
     *
     * @param edges The list of edges.
     * @param node The node for which to retrieve incident edges.
     * @return The list of edges incident to the specified node.
     */
    private List<Edge> getNodeIncidentEdges(List<Edge> edges, Integer node) {
        return edges.stream()
                .filter(edge -> edge.isIncidentFor(node))
                .collect(Collectors.toList());
    }


    /**
     * Adds the two least expensive edges incident to the target node to the MST.
     *
     * @param mst The minimum spanning tree graph.
     * @param mandatoryEdges The list of mandatory edges.
     * @param forbiddenEdges The list of forbidden edges.
     */
    private void addLeastExpensiveEdges(Graph mst, List<Edge> mandatoryEdges, List<Edge> forbiddenEdges) {
        Edge firstEdge = null, secondEdge = null;

        if (mandatoryEdges.size() >= 2) {
            Pair<Edge, Edge> pair = findTwoLeastExpensiveEdges(mandatoryEdges);
            firstEdge = pair.getFirst();
            secondEdge = pair.getSecond();;
        } else if (mandatoryEdges.size() == 1) {
            firstEdge = mandatoryEdges.get(0);
            secondEdge = findLeastExpensiveEdgeExcluding(mandatoryEdges, forbiddenEdges, firstEdge);
        } else {
            firstEdge = findLeastExpensiveEdgeExcluding(mandatoryEdges, forbiddenEdges, null);
            secondEdge = findLeastExpensiveEdgeExcluding(mandatoryEdges, forbiddenEdges, firstEdge);
        }

        if (firstEdge != null && secondEdge != null) {
            try {
                mst.addEdge(firstEdge);
                mst.addEdge(secondEdge);
            } catch (NodeNotFoundExceptionGraph e) {
                e.printStackTrace();
            }
        }
    }

    public Pair<Edge, Edge> findTwoLeastExpensiveEdges(List<Edge> edges) {
        if (edges == null || edges.size() < 2) {
            throw new IllegalArgumentException("The edge list must contain at least two edges.");
        }

        Edge leastExpensive = new Edge(null, null, Integer.MAX_VALUE);
        Edge secondLeastExpensive = new Edge(null, null, Integer.MAX_VALUE);

        for (Edge edge : edges) {
            if (edge.getWeight() < leastExpensive.getWeight()) {
                secondLeastExpensive = leastExpensive;
                leastExpensive = edge;
            } else if (edge.getWeight() < secondLeastExpensive.getWeight()) {
                secondLeastExpensive = edge;
            }
        }

        return new Pair<>(leastExpensive, secondLeastExpensive);
    }


    /**
     * Finds the least expensive edge from the given list of edges, excluding the specified edge.
     *
     * @param edges The list of edges.
     * @param forbiddenEdges The list of forbidden edges.
     * @param excludedEdge The edge to exclude from the search.
     * @return The least expensive edge found.
     */

    private Edge findLeastExpensiveEdgeExcluding(List<Edge> edges, List<Edge> forbiddenEdges, Edge excludedEdge) {
        Edge leastExpensiveEdge = null;
        for (Edge edge : initialGraph.getEdges()) {
            boolean isForbidden = forbiddenEdges.contains(edge) || forbiddenEdges.contains(edge.reverse());
            boolean isExcluded = excludedEdge != null && (excludedEdge.equals(edge) || excludedEdge.reverse().equals(edge));
            boolean isIncident = edge.isIncidentFor(targetNode.getID());

            if (!isForbidden && !isExcluded && isIncident) {
                if (leastExpensiveEdge == null || leastExpensiveEdge.getWeight() > edge.getWeight()) {
                    leastExpensiveEdge = edge;
                }
            }
        }
        return leastExpensiveEdge;
    }

    /**
     * Returns a string representation of the IntermediateProblem instance.
     *
     * @return A string representation of the IntermediateProblem instance.
     */
    @Override
    public String toString() {
        String toPrint = getCurrentOneTree().toString();
        toPrint = toPrint + "\n cost: " + getBound();
        toPrint = toPrint + "\n Forbidden edges: " + getExcludedEdges().toString();
        toPrint = toPrint + "\n Mandatory edges: " + getFixedEdges().toString();
        return toPrint;
    }


    /**
     * Comparator for comparing edges based on their weights.
     */
    private static class EdgeWeightComparator implements Comparator<Edge> {

        private static EdgeWeightComparator singletonInstance = null;

        /**
         * Returns the singleton instance of the EdgeWeightComparator.
         *
         * @return The singleton instance of the EdgeWeightComparator.
         */
        public synchronized static EdgeWeightComparator getInstance() {
            if (singletonInstance == null) {
                singletonInstance = new EdgeWeightComparator();
            }
            return singletonInstance;
        }

        /**
         * Compares two edges based on their labels.
         *
         * @param edge1 The first edge to be compared.
         * @param edge2 The second edge to be compared.
         * @return A negative integer, zero, or a positive integer as the label of the first edge
         *         is less than, equal to, or greater than the label of the second edge.
         */
        @Override
        public int compare(Edge edge1, Edge edge2) {
            return Float.compare(edge1.getWeight(), edge2.getWeight());
        }
    }

    @Override
    public int compareTo(IntermediateProblem otherIntermediateProblem) {
        int boundComparisonResult = Integer.compare(this.bound, otherIntermediateProblem.bound);
        if (boundComparisonResult != 0) {
            return boundComparisonResult;
        }

        return this.hasHamiltonianCycle ? -1 : 1;
    }


    public Graph getCurrentOneTree() {
        return currentOneTree;
    }



    //do not count the edges twice if the graph is undirected

    private int evaluateOneTreeCost() {
        return (int) (this.currentOneTree.getEdges()
                .stream()
                .mapToDouble(Edge::getWeight)
                .sum()) / 2;
    }

    private boolean detectHamiltonianCycle() {
        return currentOneTree.getNodes()
                .stream()
                .allMatch(node -> {
                    try {
                        return currentOneTree.getDegree(node) == 2;
                    } catch (NodeNotFoundExceptionGraph e) {
                        throw new RuntimeException(e);
                    }
                });
    }


    public Graph getInitialGraph() {
        return initialGraph;
    }

    public ArrayList<Edge> getFixedEdges() {
        return fixedEdges;
    }


    public ArrayList<Edge> getExcludedEdges() {
        return excludedEdges;
    }

    public GraphNode getTargetNode() {
        return targetNode;
    }



    public boolean hasHamiltonianCycle() {
        return hasHamiltonianCycle;
    }


    public int getBound() {
        return bound;
    }



    public Integer getIntermediateTreeLevel() {
        return treeDepthLevel;
    }

    public boolean isValidSolution() {
        return validSolution;
    }

}
