<h1>🚀 Branch and Bound for the Traveling Salesman Problem (TSP)</h1>

<p>
This project implements the <strong>Branch and Bound</strong> algorithm to solve the 
<strong>Traveling Salesman Problem (TSP)</strong>, leveraging the <strong>1-Tree relaxation method</strong> 
proposed by <strong>Held & Karp</strong>. The implementation balances efficiency and accuracy, 
incorporating Lagrangian relaxation for improved lower bounds.
</p>

<hr>

<h2>📄 Paper Overview</h2>

<ul>
  <li><strong>Understanding the TSP</strong>: A fundamental NP-complete combinatorial optimization problem.</li>
  <li><strong>1-Tree Relaxation (Held & Karp, 1970s)</strong>: A technique to obtain an informative lower bound.</li>
  <li><strong>Branch and Bound Method</strong>: An exact algorithm for finding the optimal solution.</li>
  <li><strong>Bounding Techniques</strong>: Using 1-Tree relaxation and Lagrangian relaxation to enhance efficiency.</li>
  <li><strong>Implementation Analysis</strong>: Evaluating computational performance on different datasets.</li>
</ul>

<hr>

<h2>🚀 Project Implementation</h2>

<p>The core implementation consists of three main components:</p>

<h3>1️⃣ Branch and Bound Algorithm</h3>
<ul>
  <li><strong>File:</strong> <code>BranchAndBound.java</code></li>
  <li><strong>Functionality:</strong>
    <ul>
      <li>Implements a <strong>multi-threaded Branch and Bound approach</strong> for TSP.</li>
      <li>Supports <strong>Best-First Search (BFS) and Depth-First Search (DFS)</strong> policies.</li>
      <li>Uses <strong>a queue to manage subproblems</strong>, allowing parallel execution.</li>
      <li>Integrates <strong>bounding techniques</strong> to prune unpromising paths early.</li>
      <li>Identifies <strong>one-way nodes and infeasible solutions</strong> to improve efficiency.</li>
    </ul>
  </li>
</ul>

<h3>2️⃣ 1-Tree Relaxation and Intermediate Problem Representation</h3>
<ul>
  <li><strong>File:</strong> <code>IntermediateProblem.java</code></li>
  <li><strong>Functionality:</strong>
    <ul>
      <li>Constructs the <strong>1-Tree relaxation</strong> by:
        <ul>
          <li>Computing a <strong>Minimum Spanning Tree (MST)</strong> excluding a specific node.</li>
          <li>Adding the <strong>two lowest-cost edges</strong> to restore connectivity.</li>
        </ul>
      </li>
      <li><strong>Calculates the bound</strong> (lower bound) for pruning in Branch and Bound.</li>
      <li>Detects <strong>Hamiltonian cycles</strong>, ensuring feasibility of solutions.</li>
      <li>Generates <strong>new subproblems</strong> by fixing or excluding edges.</li>
    </ul>
  </li>
</ul>

<h3>3️⃣ Solution Representation</h3>
<ul>
  <li><strong>File:</strong> <code>Solution.java</code></li>
  <li><strong>Functionality:</strong>
    <ul>
      <li>Stores the <strong>optimal solution path and cost</strong>.</li>
      <li>Manages <strong>state transitions</strong> (Pending → Feasible → Resolved/Infeasible).</li>
      <li>Tracks <strong>node statistics</strong>, including:
        <ul>
          <li>Explored nodes</li>
          <li>Pruned nodes (by bounds)</li>
          <li>Infeasible paths</li>
        </ul>
      </li>
      <li>Generates <strong>execution statistics</strong> for performance evaluation.</li>
    </ul>
  </li>
</ul>

<hr>

<p>🔥 <em>This project provides a structured and efficient approach to solving TSP with Branch and Bound. 
Feel free to explore, test, and contribute!</em> 🚀</p>

<br>
(It is possible that the reports in English contain words or formulas that are difficult to read due to automatic stranslation of the pdf; to read them correctly, consult the Italian version)
