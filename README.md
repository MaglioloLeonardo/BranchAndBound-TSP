<h1>🚀 Branch and Bound for the Traveling Salesman Problem (TSP)</h1>

<p>
This project presents an optimized implementation of the <strong>Branch and Bound</strong> algorithm 
to solve the <strong>Traveling Salesman Problem (TSP)</strong>. The work is based on the <strong>1-Tree relaxation</strong> 
introduced by <strong>Held & Karp</strong>, which provides a powerful bounding mechanism to improve the efficiency 
of exact methods.
</p>

<p>
The implementation adopts a <strong>parallel computing approach</strong> to speed up the search process, 
leveraging <strong>multi-threading</strong> and different <strong>search strategies</strong> such as 
Best-First Search (BFS) and Depth-First Search (DFS).
</p>

<hr>

<h2>📄 Project Overview</h2>

<ul>
  <li><strong>Problem:</strong> The TSP is a well-known NP-hard problem where a salesman must visit all cities exactly once and return to the starting point, minimizing travel cost.</li>
  <li><strong>Method:</strong> The solution is based on a <strong>Branch and Bound algorithm</strong> with an optimized bounding function derived from the <strong>1-Tree relaxation</strong>.</li>
  <li><strong>Bounding Strategy:</strong> The algorithm computes a lower bound by solving a Minimum Spanning Tree (MST) with additional constraints.</li>
  <li><strong>Parallelism:</strong> The implementation supports parallel execution, allowing multiple branches of the search tree to be explored concurrently.</li>
  <li><strong>Pruning Techniques:</strong> The approach dynamically eliminates suboptimal paths to improve computational efficiency.</li>
</ul>

<hr>

<h2>🚀 Implementation Details</h2>

<ul>
  <li><strong>1-Tree Relaxation:</strong> The Held-Karp bound is computed by constructing a 1-Tree, which provides a relaxed version of the TSP.</li>
  <li><strong>Branching Strategy:</strong> The problem is recursively divided by fixing and excluding edges, progressively refining the solution.</li>
  <li><strong>Parallel Search:</strong> The algorithm supports multithreading, with a task queue managing subproblems dynamically.</li>
  <li><strong>Search Policies:</strong> Users can choose between <strong>Best-First Search (BFS)</strong> and <strong>Depth-First Search (DFS)</strong> for different exploration strategies.</li>
  <li><strong>Termination Criteria:</strong> The algorithm stops when an optimal solution is found or when all subproblems have been processed.</li>
</ul>

<hr>

<h2>📈 Results and Performance</h2>

<ul>
  <li><strong>Scalability:</strong> The implementation is designed to efficiently handle large problem instances using parallel computation.</li>
  <li><strong>Execution Time Optimization:</strong> The use of lower bounding techniques significantly reduces the number of nodes explored.</li>
  <li><strong>Experimental Validation:</strong> The approach has been tested on benchmark TSP instances, showing competitive results in terms of execution time and solution quality.</li>
</ul>

<hr>

<p>🔥 <em>This project provides an efficient and scalable approach to solving TSP using Branch and Bound with 1-Tree relaxation. 
Feel free to explore, test, and contribute!</em> 🚀</p>

<br>
(It is possible that the reports in English contain words or formulas that are difficult to read due to automatic translation of the pdf; to read them correctly, consult the Italian version)
