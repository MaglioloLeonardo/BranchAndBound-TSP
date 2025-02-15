package Source.BranchAndBoundTSP;
import Source.Datastructures.Graph.*;

import java.util.concurrent.atomic.AtomicInteger;

import java.util.Optional;
import java.util.ArrayList;

public class Solution {
    private Long executionTime = null;
    private FinalState currentState = FinalState.Pending;
    private Graph initGraph;
    private int valueOfCost;
    private final AtomicInteger nodeCountTotal = new AtomicInteger();
    private final AtomicInteger midwayNodeCount = new AtomicInteger();
    private final AtomicInteger unfeasibleClosedNodeCount = new AtomicInteger();
    private final AtomicInteger boundClosedNodeCount = new AtomicInteger();
    private final AtomicInteger optimalClosedNodeCount = new AtomicInteger();

    /**
     * Constructs a Solution instance with the specified initial graph and cost value.
     *
     * @param initGraph The initial graph representing the problem.
     * @param valueOfCost The cost value associated with the solution.
     */
    public Solution(Graph initGraph, int valueOfCost) {
        this.valueOfCost = valueOfCost;
        this.initGraph = initGraph;
    }

    /**
     * Retrieves the total number of completed graphNodes.
     *
     * @return The total number of completed graphNodes.
     */
    public int retrieveCompletedNodes() {
        return this.boundClosedNodeCount.get() + this.unfeasibleClosedNodeCount.get() + this.optimalClosedNodeCount.get();
    }


    /**
     * Identifies a solution with the specified initial graph and cost value.
     *
     * @param initGraph The initial graph representing the solution.
     * @param valueOfCost The cost value associated with the solution.
     * @throws IllegalStateException If the solution is already finalized.
     */
    public void identifiedSolution(Graph initGraph, int valueOfCost) {
        ensureNotCompleted();
        this.valueOfCost = valueOfCost;
        this.initGraph = initGraph;
        this.currentState = FinalState.Feasible;
        System.out.println("Solution identified:" + this.toString());
    }

    /**
     * Finalizes the state of the solution.
     *
     * @throws IllegalStateException If the solution is already in a terminal state.
     */
    public void finalizeSolutionState() {
        switch (this.currentState) {
            case Feasible:
                this.currentState = FinalState.Resolved;
                break;
            case Pending:
                this.currentState = FinalState.Infeasible;
                break;
            default:
                throw new IllegalStateException("Attempt to finalize a solution that is already in a terminal state is not allowed.");
        }
    }

    /**
     * Retrieves the path of the solution.
     *
     * @return An ArrayList of edges representing the path.
     * @throws IllegalStateException If the solution is in an infeasible or pending state.
     */
    public ArrayList<Edge> retrievePath() throws IllegalStateException {
        checkState();

        ArrayList<Edge> routeEdges = new ArrayList<>();
        int initialNodeKey = initGraph.getNodes().get(0).getID();
        int activeNodeKey = initialNodeKey;
        int priorNodeKey = activeNodeKey;

        do {
            Edge followingEdge = locateNextEdge(activeNodeKey, priorNodeKey);
            routeEdges.add(followingEdge);
            priorNodeKey = activeNodeKey;
            activeNodeKey = followingEdge.getV().getID();
        } while (activeNodeKey != initialNodeKey);

        return routeEdges;
    }


    /**
     * Checks the current state of the solution.
     *
     * @throws IllegalStateException If the solution is in an infeasible or pending state.
     */
    private void checkState() throws IllegalStateException {
        if (currentState == FinalState.Infeasible) {
            throw new IllegalStateException("No path exists as the problem is determined to be unsolvable.");
        } else if (currentState == FinalState.Pending) {
            throw new IllegalStateException("An intermediate solution to the related problem has yet to be found.");
        }
    }

    /**
     * Locates the next edge in the path.
     *
     * @param activeNode The current active node.
     * @param priorNode The previous node.
     * @return The next edge in the path.
     */
    private Edge locateNextEdge(int activeNode, int priorNode) {
        int edgeCounter = 0;
        Edge prospectiveEdge;

        do {
            int getNode = initGraph.getNodeById(activeNode).getID();
            GraphNode thisNode = initGraph.getNodeById(getNode);

            prospectiveEdge = initGraph.getEdges(thisNode).get(edgeCounter);
            edgeCounter++;
        } while (priorNode == prospectiveEdge.getV().getID());

        return prospectiveEdge;
    }

    /**
     * Returns a string representation of the solution.
     *
     * @return A string representation of the solution.
     */
    @Override
    public String toString() {
        return switch (this.currentState) {
            case Resolved -> String.format("Optimal solution found, cost: %d. Path:\n%s\n\n",
                    valueOfCost, retrievePath());
            case Feasible -> String.format("Solvable, best cost: %d. Path:\n%s\n\n",
                    valueOfCost, retrievePath());
            case Infeasible -> "Unsolvable. No solution.";
            case Pending -> "Solution not found yet.";
        };
    }

    /**
     * Generates statistics about the solution.
     *
     * @return A string containing statistics about the solution.
     */
    public String generateStatistics() {
        return String.format("""
    Throughout the search process, %d graphNodes were generated. Among them:
    - %d served as branching points, creating new paths;
    - %d were terminated as candidate solutions;
    - %d were pruned due to boundary limitations;
    - %d were discarded for being infeasible.
    """,
                this.nodeCountTotal.get(),
                this.midwayNodeCount.get(),
                this.optimalClosedNodeCount.get(),
                this.boundClosedNodeCount.get(),
                this.unfeasibleClosedNodeCount.get());
    }

    /**
     * Increases the count of midway graphNodes by the specified amount.
     *
     * @param j The number of graphNodes to add to the midway graphNodes count.
     * @throws IllegalStateException If the solution is already finalized.
     */
    public synchronized void addMidwayNodes(int j) {
        ensureNotCompleted();

        this.midwayNodeCount.addAndGet(j);
    }

    /**
     * Increases the count of closed graphNodes for bound by the specified amount.
     *
     * @param j The number of graphNodes to add to the closed graphNodes for bound count.
     * @throws IllegalStateException If the solution is already finalized.
     */
    public synchronized void addBoundClosedNode(int j) {
        ensureNotCompleted();

        this.boundClosedNodeCount.addAndGet(j);
    }

    /**
     * Increases the count of closed graphNodes for infeasibility by the specified amount.
     *
     * @param j The number of graphNodes to add to the closed graphNodes for infeasibility count.
     * @throws IllegalStateException If the solution is already finalized.
     */
    public synchronized void addInfeasibleClosedNodes(int j) {
        ensureNotCompleted();

        this.unfeasibleClosedNodeCount.addAndGet(j);
    }

    /**
     * Increases the total node count by the specified amount.
     *
     * @param updateCount The number of graphNodes to add to the total count.
     * @throws IllegalStateException If the solution is already finalized.
     */
    public synchronized void incrementNodes(int updateCount) {
        ensureNotCompleted();

        this.nodeCountTotal.addAndGet(updateCount);
    }

    /**
     * Increases the count of closed graphNodes for the optimal solution by the specified amount.
     *
     * @param j The number of graphNodes to add to the closed graphNodes for the optimal solution count.
     * @throws IllegalStateException If the solution is already finalized.
     */
    public synchronized void addOptimalClosedNodes(int j) {
        ensureNotCompleted();

        this.optimalClosedNodeCount.addAndGet(j);
    }


    /**
     * Ensures that the solution is not in a completed state.
     *
     * @throws IllegalStateException If the solution is in a final state (Resolved or Infeasible).
     */
    private void ensureNotCompleted() {
        if (currentState == FinalState.Resolved || currentState == FinalState.Infeasible) {
            throw new IllegalStateException("The task has already been finalized and cannot be modified.");
        }
    }

    /**
     * Fetches the number of active graphNodes.
     *
     * @return The number of active graphNodes.
     */
    public synchronized int fetchActiveNodes() {
        return getNodeCountTotal() - (retrieveCompletedNodes() + getMidwayNodeCount());
    }

    /**
     * Gets the execution time of the solution.
     *
     * @return An Optional containing the execution time, if set.
     */
    public Optional<Long> getExecutionTime() {
        return Optional.ofNullable(this.executionTime);
    }

    /**
     * Sets the execution time of the solution.
     *
     * @param l The execution time to set.
     */
    public void setExecutionTime(Long l) {
        assert l != null;
        assert l >= 0: "Execution time must be a non-negative value.";
        this.executionTime = l;
    }

    /**
     * Enum representing the final state of the solution.
     */
    public enum FinalState {
        Resolved, Feasible, Infeasible, Pending
    }


    public Graph getInitGraph() {
        return initGraph;
    }

    public int getValueOfCost() {
        return valueOfCost;
    }

    FinalState getCurrentState() {
        return currentState;
    }

    public int getNodeCountTotal() {
        return nodeCountTotal.get();
    }

    public int getOptimalClosedNodeCount() {
        return optimalClosedNodeCount.get();
    }

    public int getBoundClosedNodeCount() {
        return boundClosedNodeCount.get();
    }

    public int getUnfeasibleClosedNodeCount() {
        return unfeasibleClosedNodeCount.get();
    }

    public int getMidwayNodeCount() {
        return midwayNodeCount.get();
    }
}