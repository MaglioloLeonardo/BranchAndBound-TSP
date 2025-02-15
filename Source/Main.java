package Source;

import Source.BranchAndBoundTSP.Solution;
import Source.Datastructures.Graph.Graph;

import Source.BranchAndBoundTSP.BranchAndBound;

import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            //get input file from keyboard and check if it is valid
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the path of the TSP file: ");
            String filePath = scanner.nextLine();

            File inputFile = new File(filePath);
            if (!inputFile.exists() || !inputFile.isFile()) {
                System.err.println("Invalid file path. Please provide a valid TSP file.");
                return;
            }

            System.out.print("Enter the strategy of visit (BestFS or DFS):");
            String strategy = scanner.nextLine();
            if (!(strategy.equals("BestFS") || strategy.equals("DFS"))) {
                System.err.println("Invalid strategy name. Please provide BestFS or DFS.");
                return;
            }

            System.out.print("Enter the number of threads: ");
            int threadCount = scanner.nextInt();
            if (threadCount <= 0) {
                System.err.println("Invalid thread count. Please provide a positive integer.");
                return;
            }

            //File tempFile = new File("TSPLIB/burma14.tsp");
            Graph graph = TSPLIBGraphGenerator.createGraph(inputFile.getAbsolutePath());
            System.out.println(graph);

            boolean removeInvalidNodes = false;
            BranchAndBound branchAndBound = new BranchAndBound(graph, strategy);


            long time1 = System.currentTimeMillis();
            Solution result = null;
            result = branchAndBound.processTask(removeInvalidNodes, threadCount);

            long time = System.currentTimeMillis() - time1;

            System.out.println("\n________________________________\n"+result.toString());

            System.out.println(result.generateStatistics());

            System.out.println("Execution time: " + time + " milliseconds");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
