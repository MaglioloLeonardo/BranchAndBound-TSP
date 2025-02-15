//
package Source.BranchAndBoundTSP;

import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

import Source.Datastructures.Graph.Exceptions.NodeNotFoundExceptionGraph;
import Source.Datastructures.LifoBlockingQueue;
import Source.Datastructures.Graph.*;
import Source.BranchAndBoundTSP.Exceptions.NotSolvableCaseException;

/**
 * Implements the Branch and Bound algorithm for solving the Traveling Salesman Problem (TSP).
 */

public class BranchAndBound {

    private Graph graph;
    private final GraphNode targetNode;
    private final List<GraphNode> targetNodes;
    public boolean IfErrorThenTerminate = true;
    private BlockingQueue<IntermediateProblem> subTaskQueue;
    private final ExecutorService executorService;
    private int threadNumber;
    public static float lowestBound = Float.MAX_VALUE;
    public static float highestBound = Float.MIN_VALUE;

    /**
     * Constructs a BranchAndBound instance with specified graph, target node, and thread number.
     *
     * @param graph The graph representing the TSP.
     * @param targetNode The starting node for the TSP.
     * @param threadNumber The number of threads to use for parallel computing.
     */
    public BranchAndBound(Graph graph, GraphNode targetNode, int threadNumber, String policy) {
        if (policy.equals("BestFS")){
            this.subTaskQueue = new PriorityBlockingQueue<>();
        } else if (policy.equals("DFS")){
            this.subTaskQueue = new LifoBlockingQueue<>();
        } else {
            throw new IllegalArgumentException("Policy not supported");
        }
        this.graph = graph.clone();
        int random = new Random().nextInt(graph.getNodes().size());
        this.targetNode = (targetNode != null) ? targetNode : graph.getNodes().get(random);
        this.targetNodes = graph.getNodes();
        this.threadNumber = (threadNumber > 0) ? threadNumber : Runtime.getRuntime().availableProcessors();
        this.executorService = Executors.newFixedThreadPool(this.threadNumber);
    }

    /**
     * Convenience constructor for BranchAndBound with default settings.
     *
     * @param graph The graph representing the TSP.
     */

    public BranchAndBound(Graph graph, String policy) {
        this(graph, null, 1, policy);
    }

    /**
     * Solves the TSP problem with default settings.
     *
     * @return The result of the TSP problem.
     * @throws NotSolvableCaseException If the problem cannot be solved.
     */
    public Solution processTask() throws NotSolvableCaseException, NodeNotFoundExceptionGraph {
        return processTask(false, this.threadNumber);
    }


    /**
     * Solves the TSP problem with the option to ignore one-way nodes and a specified number of threads.
     *
     * @param excludeUnidirectionalNodes If true, one-way nodes will be ignored.
     * @param numberOfThreads The number of threads to use for parallel computing.
     * @return The result of the TSP problem.
     * @throws NotSolvableCaseException If the problem cannot be solved.
     */
    public Solution processTask(boolean excludeUnidirectionalNodes, int numberOfThreads) throws NotSolvableCaseException, NodeNotFoundExceptionGraph {
        if (numberOfThreads <= 0) {
            throw new IllegalArgumentException("Threads must be at least 1");
        }
        List<Integer> UnidirectionalNodesKeys = identifyOneWayNodes(excludeUnidirectionalNodes);
        Solution optimalSolution = new Solution(graph, Integer.MAX_VALUE);
        initializeRootProblem(optimalSolution);
        ExecutorService threadPool = Executors.newFixedThreadPool(numberOfThreads);
        return solveTSPWithParallelComputing(optimalSolution, threadPool);
    }

    /**
     * Executes the problem-solving tasks in parallel.
     *
     * @param threadPool The ExecutorService to use for executing tasks.
     * @param minSolution The current minimum TSP result.
     * @throws InterruptedException If the execution is interrupted.
     * @throws ExecutionException If an execution error occurs.
     */
    private void executeProblemSolving(ExecutorService threadPool, Solution minSolution) throws InterruptedException, ExecutionException {

        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < threadNumber; i++) {
            tasks.add(() -> {
                return null;
            });
        }
        List<Future<Void>> futures = threadPool.invokeAll(tasks);
        for (Future<Void> future : futures) {
            // Ensure all tasks complete successfully
            future.get();
        }
    }

    /**
     * Initializes the root problem for the Branch and Bound algorithm.
     * It creates the initial sub-problem and adds it to the priority queue.
     * Also, increments the node count in the minimum TSP result.
     *
     * @param minSolution The current minimum TSP result to be updated.
     */
    private void initializeRootProblem(Solution minSolution) throws NodeNotFoundExceptionGraph {
        subTaskQueue = new PriorityBlockingQueue<IntermediateProblem>();
        IntermediateProblem rootProblem = new IntermediateProblem(graph, targetNode);
        subTaskQueue.add(rootProblem);
        minSolution.incrementNodes(1);

    }

    /**
     * Identifies nodes in the graph that have less than two edges (one-way nodes).
     * Depending on the ignoreOneWayNodes flag, it either removes these nodes from the graph
     * or throws an UnsolvableProblemException if such nodes exist and should not be ignored.
     *
     * @param ignoreOneWayNodes Flag indicating whether to ignore one-way nodes.
     * @return A list of keys representing one-way nodes.
     * @throws NotSolvableCaseException If one-way nodes are found and should not be ignored.
     */
    private List<Integer> identifyOneWayNodes(boolean ignoreOneWayNodes) throws NotSolvableCaseException {
        List<Integer> unidirectionalNodeKeys = graph.getNodes()
                .stream()
                .filter(node -> graph.getEdges(node).size() < 2)
                .map(GraphNode::getID)
                .collect(Collectors.toUnmodifiableList());

        if (!ignoreOneWayNodes && !unidirectionalNodeKeys.isEmpty()) {
            throw new NotSolvableCaseException(unidirectionalNodeKeys);
        }

        if (ignoreOneWayNodes) {
            unidirectionalNodeKeys.forEach(id -> graph.deleteNode(graph.getNodeById(id)));
        }

        return unidirectionalNodeKeys;
    }

    private int generateBranches(IntermediateProblem currentProblem) throws NodeNotFoundExceptionGraph {

        HashMap<Integer, Integer> parentNodeMap = new HashMap<>();
        DepthFirstSearch( targetNode.getID(), parentNodeMap, currentProblem.getCurrentOneTree());
        int addedNodeCount = 0;

        ArrayList<Edge> subCycle = new ArrayList<>();

        int toNode = currentProblem.getTargetNode().getID();
        int fromNode = Integer.MAX_VALUE;

        while (fromNode != currentProblem.getTargetNode().getID()) {
            fromNode = parentNodeMap.get(toNode);
            subCycle.add(currentProblem.getCurrentOneTree().getEdge(currentProblem.getCurrentOneTree().getNodeById(fromNode), currentProblem.getCurrentOneTree().getNodeById(toNode)));
            toNode = fromNode;
        }

        Set<Edge> essentialEdges = new HashSet<>(currentProblem.getFixedEdges());
        Set<Edge> excludedEdges = new HashSet<>(currentProblem.getExcludedEdges());

        for (Edge integerIntegerEdge : subCycle) {
            if (!(currentProblem.getFixedEdges().contains(integerIntegerEdge) ||
                    currentProblem.getFixedEdges().contains(integerIntegerEdge.reverse()))
            ) {
                excludedEdges.add(integerIntegerEdge);
                IntermediateProblem sp = new IntermediateProblem(graph,
                        new ArrayList<>(essentialEdges),
                        new ArrayList<>(excludedEdges),
                        targetNode,
                        currentProblem.getIntermediateTreeLevel() + 1);
                subTaskQueue.add(sp);
                addedNodeCount++;

                excludedEdges.remove(integerIntegerEdge);
                essentialEdges.add(integerIntegerEdge);
            }
        }

        return addedNodeCount;
    }


    /**
     * Finds cycle edges in the graph based on the parent node map and the active problem's one-tree.
     * This method is used to identify the edges that form a cycle, which is crucial for generating new sub-problems.
     *
     * @param parentNodeMap A map containing the parent nodes for each node, used to trace back the cycle.
     * @param activeProblem The current sub-problem being solved, which contains the one-tree.
     * @return A list of edges that form a cycle in the one-tree of the active problem.
     */
    private List<Edge> findCycleEdges(HashMap<Integer, Integer> parentNodeMap, IntermediateProblem activeProblem) {
        List<Edge> cycleEdges = new ArrayList<>();
        int currentNode = targetNode.getID();
        int previousNode = Integer.MAX_VALUE;

        while (previousNode != targetNode.getID()) {
            previousNode = parentNodeMap.get(currentNode);

            GraphNode currentGraphNode = activeProblem.getInitialGraph().getNodeById(currentNode);
            GraphNode previousGraphNode = activeProblem.getInitialGraph().getNodeById(previousNode);

            cycleEdges.add(activeProblem.getCurrentOneTree().getEdge(previousGraphNode, currentGraphNode));

            currentNode = previousNode;
        }
        return cycleEdges;
    }



    /**
     * Creates a new sub-problem based on the given sets of essential and excluded edges, and enqueues it.
     * This method is crucial for the branching step of the Branch and Bound algorithm, where new sub-problems
     * are generated by excluding certain edges to explore different possible solutions.
     *
     * @param essentialEdges A set of edges that must be included in the new sub-problem.
     * @param excludedEdges A set of edges that must not be included in the new sub-problem.
     * @param activeProblem The current sub-problem from which the new sub-problem is branched off.
     * @param targetNode The target node for the new sub-problem.
     */
    private void createAndEnqueueIntermediateProblem(Set<Edge> essentialEdges, Set<Edge> excludedEdges, IntermediateProblem activeProblem, GraphNode targetNode) throws NodeNotFoundExceptionGraph {
        IntermediateProblem newIntermediateProblem = new IntermediateProblem(graph,
                new ArrayList<>(essentialEdges),
                new ArrayList<>(excludedEdges),
                targetNode,
                activeProblem.getIntermediateTreeLevel() + 1);
        subTaskQueue.add(newIntermediateProblem);
    }


    /**
     * A callable task that computes solutions for sub-problems in parallel.
     * This class is responsible for continuously polling sub-problems from a queue and processing them
     * to find feasible solutions or branch out into new sub-problems.
     */
    public class ComputeTask implements Callable<Void> {
        private final AtomicInteger level;
        private final AtomicBoolean completed;
        private final Solution optimalSolution;

        private final BlockingQueue<IntermediateProblem> intermediateProblemQueue;

        /**
         * Constructs a new ComputeTask instance.
         *
         * @param level A counter for the current level of sub-problems being processed.
         * @param completed A flag indicating whether the computation is completed.
         * @param optimalSolution The current minimum TSP result found.
         * @param intermediateProblemQueue A queue of sub-problems to be processed.
         * @param executorService The executor service used for submitting new tasks.
         */

        public ComputeTask(AtomicInteger level, AtomicBoolean completed,
                           Solution optimalSolution, BlockingQueue<IntermediateProblem> intermediateProblemQueue, ExecutorService executorService) {
            this.level = level;
            this.completed = completed;
            this.optimalSolution = optimalSolution;
            this.intermediateProblemQueue = intermediateProblemQueue;
        }

        @Override
        public Void call() {
            try {
                while (!completed.get()) {
                    IntermediateProblem activeTask = intermediateProblemQueue.poll(100, TimeUnit.MILLISECONDS);

                    if (activeTask == null) {
                        if (intermediateProblemQueue.isEmpty()) {
                            completed.set(true);
                            shutdownExecutorService();
                        }
                        continue;
                    }
                    if(lowestBound > activeTask.getBound()){
                        lowestBound = activeTask.getBound();
                    }
                    if(highestBound < activeTask.getBound()){
                        highestBound = activeTask.getBound();
                    }

                    //print the bounds only if change is detected

                    /*if(System.currentTimeMillis() % 10000 == 0){
                        System.out.println("Lowest bound: " + lowestBound + " Highest bound: " + highestBound);
                        System.out.println(optimalSolution.generateStatistics());
                    }*/


                    if (activeTask.isValidSolution()) {
                        if (activeTask.hasHamiltonianCycle()) {
                            if (optimalSolution.getValueOfCost() > activeTask.getBound()) {
                                optimalSolution.identifiedSolution(activeTask.getCurrentOneTree(),
                                        activeTask.getBound());
                                optimalSolution.addOptimalClosedNodes(1);
                                //System.out.println("New optimal solution found: " + optimalSolution.getValueOfCost());
                            }
                        } else if (activeTask.getBound() < optimalSolution.getValueOfCost()) {
                            //executorService.submit(new BranchTask(activeTask));
                            int newProblemCount = BranchAndBound.this.generateBranches(activeTask);
                            optimalSolution.incrementNodes(newProblemCount);
                            optimalSolution.addMidwayNodes(1);
                        } else {
                            optimalSolution.addBoundClosedNode(1);
                        }
                    } else {
                        optimalSolution.addInfeasibleClosedNodes(1);
                    }
                }
            } catch (InterruptedException e) {
                if (!completed.get()) {
                    e.printStackTrace();
                }
            } catch (NodeNotFoundExceptionGraph e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        private void shutdownExecutorService() {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                    if (!executorService.awaitTermination(60, TimeUnit.SECONDS))
                        System.err.println("ExecutorService did not terminate");
                }
            } catch (InterruptedException ie) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        private class BranchTask implements Runnable {
            private final IntermediateProblem intermediateProblem;

            public BranchTask(IntermediateProblem intermediateProblem) {
                this.intermediateProblem = intermediateProblem;
            }

            @Override
            public void run() {
                try {
                    int newProblemCount = BranchAndBound.this.generateBranches(intermediateProblem);
                    optimalSolution.incrementNodes(newProblemCount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Performs a Depth-First Search (DFS) on the given graph starting from the currentNode.
     * It uses recursion to explore all the nodes connected to the currentNode and marks them in the parentMap.
     * This method is used to identify cycles and paths in the graph.
     *
     * @param currentNode The starting node for the DFS.
     * @param parentMap A map to keep track of each node's parent node to detect cycles and paths.
     * @param graph The graph on which the DFS is performed.
     */
    private void DepthFirstSearch(int currentNode,
                                  HashMap parentMap,
                                  Graph graph) {
        for (Edge outgoingEdge : graph.getEdges(graph.getNodeById(currentNode))) {
            Integer targetNodeKey = outgoingEdge.getV().getID();
            boolean isTargetNodeUnvisited = !parentMap.containsKey(targetNodeKey);
            boolean isNotCircularReference = !parentMap.containsKey(currentNode) ||
                    !parentMap.get(currentNode).equals(targetNodeKey);

            if (isTargetNodeUnvisited && isNotCircularReference) {
                parentMap.put(targetNodeKey, currentNode);
                DepthFirstSearch(graph.getNodeById(targetNodeKey).getID(), parentMap, graph);
            }
        }
    }

    /**
     * Solves the Traveling Salesman Problem (TSP) using parallel computing.
     * It divides the problem into sub-problems, solves them in parallel, and aggregates the results.
     *
     * @param minSolution The Solution object to store the minimum result found during computation.
     * @param threadPool The ExecutorService to manage parallel execution.
     * @return The Solution containing the intermediateProblem to the TSP problem.
     */
    private Solution solveTSPWithParallelComputing(Solution minSolution, ExecutorService threadPool) {
        long startTime = System.currentTimeMillis();
        AtomicInteger currentLevel = new AtomicInteger(0);
        AtomicBoolean computationCompleted = new AtomicBoolean(false);
        ArrayList<Future<Void>> intermediateProblems = submitIntermediateProblemTasks(currentLevel, computationCompleted, minSolution, threadPool);

        awaitCompletion(intermediateProblems);
        minSolution.setExecutionTime(System.currentTimeMillis() - startTime);
        shutdownAndAwaitTermination(threadPool, 5);
        minSolution.finalizeSolutionState();
        return minSolution;
    }

    /**
     * Submits tasks for solving sub-problems of the TSP in parallel.
     *
     * @param currentLevel The current depth level in the problem tree being processed.
     * @param computationCompleted A flag indicating whether the computation is completed.
     * @param minSolution The current minimum TSP result found.
     * @param threadPool The ExecutorService to use for executing tasks.
     * @return A list of Future objects representing the tasks submitted for execution.
     */
    private ArrayList<Future<Void>> submitIntermediateProblemTasks(AtomicInteger currentLevel, AtomicBoolean computationCompleted, Solution minSolution, ExecutorService threadPool) {
        ArrayList<Future<Void>> intermediateProblems = new ArrayList<>(threadNumber);
        for (int i = 0; i < /*threadNumber*/ 1; i++) {
            intermediateProblems.add(threadPool.submit(new ComputeTask(currentLevel, computationCompleted, minSolution, subTaskQueue, executorService)));
        }
        return intermediateProblems;
    }

    /**
     * Waits for all submitted sub-problem tasks to complete.
     * If an InterruptedException or ExecutionException occurs, it cancels all tasks and may terminate the program.
     *
     * @param intermediateProblems The list of Future objects representing the tasks submitted for execution.
     */
    private void awaitCompletion(ArrayList<Future<Void>> intermediateProblems) {
        for (Future<Void> intermediateProblemsSolver : intermediateProblems) {
            try {
                intermediateProblemsSolver.get();
            } catch (InterruptedException | ExecutionException e) {
                intermediateProblems.forEach(future -> future.cancel(true));
                if (IfErrorThenTerminate) {
                    System.exit(2);
                }
            }
        }
    }

    /**
     * Attempts to shut down an ExecutorService and waits for termination up to a specified timeout.
     * If the service does not terminate within the timeout, it attempts to stop all actively executing tasks.
     *
     * @param threadPool The ExecutorService to shut down.
     * @param timeoutSeconds The maximum time to wait for termination in seconds.
     */
    private void shutdownAndAwaitTermination(ExecutorService threadPool, int timeoutSeconds) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
                if (!threadPool.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
                    System.err.println("ExecutorService did not terminate");
                }
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        } finally {
            resetResourcesForNextRun();
        }
    }

    /**
     * Resets resources to their initial state for the next run of the algorithm.
     * This includes reinitializing the sub-problem queue and suggesting garbage collection.
     */
    private void resetResourcesForNextRun() {
        subTaskQueue = new PriorityBlockingQueue<>();
        System.gc();
    }
}


