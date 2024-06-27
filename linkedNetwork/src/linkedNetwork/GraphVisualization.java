package linkedNetwork;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GraphVisualization {
    public static void main(String[] args) {
        boolean visualizeZeroEdges = true; // Set this to true to visualize nodes with zero edges

        for (int k = 1; k <= 10; k++) {
            // Define the range for P and S nodes
            int difference = 1;
            int proportion = 0;
            int minN = 1;
            int maxP = k; // Use maxP instead of maxN

            Set<Integer> primes = HelperFunctions.generatePrimes(maxP);
            Set<Pair> rootPairs = new HashSet<>();
            Set<String> nodes = new HashSet<>();

            for (int p : primes) {
                if (p <= maxP) {
                    Pair pair = new Pair(1, p);
                    rootPairs.add(pair);
                    nodes.add("P" + (pair.first * pair.second));
                    nodes.add("S" + (pair.first + pair.second));
                }
            }
            for (int p : primes) {
                int pSquare = p * p;
                if (pSquare <= maxP) {
                    Pair pair = new Pair(1, pSquare);
                    rootPairs.add(pair);
                    nodes.add("P" + (pair.first * pair.second));
                    nodes.add("S" + (pair.first + pair.second));
                }
            }

            Set<Pair> allPairs = new HashSet<>(rootPairs);
            Set<Rule> rules = new HashSet<>();

            // Part 1: Generate edges and nodes up to the fourth step
            generateEdgesAndNodes(allPairs, nodes, minN, maxP, difference, rules, 3);

            // Find S4 pairs
            Set<Pair> s4Pairs = new HashSet<>();
            for (Pair pair : allPairs) {
                if (StepsToSolve.stepsToSolve(pair, minN, maxP, difference, 0).equals("4")) {
                    s4Pairs.add(pair);
                }
            }

            // Part 2: Generate edges and nodes up to three steps from S4 nodes
//            generateEdgesAndNodes(s4Pairs, nodes, minN, maxP, difference, rules, 3);

            // Generate the graph using the rules
            Graph<String, DefaultEdge> graph = GraphGenerator.generateGraph(nodes, rules);

            // Export the table as a CSV file
            try (FileWriter csvWriter = new FileWriter(String.format("pairs-P=%s.csv", maxP))) {
                csvWriter.append("x,y,s\n");
                for (Pair pair : s4Pairs) {
                    String steps = StepsToSolve.stepsToSolve(pair, minN, maxP, difference, 0);
                    if ("4".equals(steps)) {
                        csvWriter.append(String.format("%d,%d,%s\n", pair.first, pair.second, steps));
                    } else {
                        csvWriter.append(String.format("%d,%d,X\n", pair.first, pair.second));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Create a visualization using JGraphX
            mxGraph mxGraph = new mxGraph();
            Object parent = mxGraph.getDefaultParent();

            Map<String, Object> vertexMap = new HashMap<>();
            Map<String, Integer> edgeCountMap = new HashMap<>();

            mxGraph.getModel().beginUpdate();
            try {
                // Add vertices to the mxGraph and initialize edge counts
                for (String node : nodes) {
                    Object vertex = mxGraph.insertVertex(parent, null, node, 100, 100, 30, 30);
                    vertexMap.put(node, vertex);
                    edgeCountMap.put(node, 0);
                }

                // Add edges to the mxGraph and update edge counts
                for (DefaultEdge edge : graph.edgeSet()) {
                    String source = graph.getEdgeSource(edge);
                    String target = graph.getEdgeTarget(edge);
                    String label = "";

                    // Apply labels based on rules
                    for (Rule rule : rules) {
                        if (rule.matches(source, target)) {
                            label = rule.getLabel();
                            break;
                        }
                    }

                    Object sourceVertex = vertexMap.get(source);
                    Object targetVertex = vertexMap.get(target);
                    mxGraph.insertEdge(parent, null, label, sourceVertex, targetVertex);
                    edgeCountMap.put(source, edgeCountMap.get(source) + 1);
                    edgeCountMap.put(target, edgeCountMap.get(target) + 1);
                }

                // Apply colors based on the number of edges
                mxStylesheet stylesheet = mxGraph.getStylesheet();
                for (Map.Entry<String, Object> entry : vertexMap.entrySet()) {
                    String vertex = entry.getKey();
                    Object cell = entry.getValue();

                    Map<String, Object> style = new HashMap<>();
                    if (vertex.startsWith("S")) {
                        int num = Integer.parseInt(vertex.substring(1));
                        int ways = countSumWays(num, minN, difference);
                        if (ways == 0) {
                            style.put(mxConstants.STYLE_FILLCOLOR, "grey");
                        } else if (ways == 1) {
                            style.put(mxConstants.STYLE_FILLCOLOR, "green");
                        } else if (ways == 2) {
                            style.put(mxConstants.STYLE_FILLCOLOR, "yellow");
                        } else {
                            style.put(mxConstants.STYLE_FILLCOLOR, "red");
                        }
                    } else if (vertex.startsWith("P")) {
                        int num = Integer.parseInt(vertex.substring(1));
                        int ways = countProductWays(num, minN, difference);
                        if (ways == 0) {
                            style.put(mxConstants.STYLE_FILLCOLOR, "grey");
                        } else if (ways == 1) {
                            style.put(mxConstants.STYLE_FILLCOLOR, "green");
                        } else if (ways == 2) {
                            style.put(mxConstants.STYLE_FILLCOLOR, "yellow");
                        } else {
                            style.put(mxConstants.STYLE_FILLCOLOR, "red");
                        }
                    }

                    stylesheet.putCellStyle("STYLE_" + vertex, style);
                    ((mxCell) cell).setStyle("STYLE_" + vertex);
                }

                // Remove nodes with zero edges if visualizeZeroEdges is false
                if (!visualizeZeroEdges) {
                    for (Map.Entry<String, Object> entry : vertexMap.entrySet()) {
                        if (edgeCountMap.get(entry.getKey()) == 0) {
                            mxGraph.getModel().remove(entry.getValue());
                        }
                    }
                }
            } finally {
                mxGraph.getModel().endUpdate();
            }

            // Use an organic layout to position the nodes
            mxIGraphLayout layout = new mxOrganicLayout(mxGraph);
            layout.execute(parent);

            // Export the graph as an image
            BufferedImage image = mxCellRenderer.createBufferedImage(mxGraph, null, 2, java.awt.Color.WHITE, true, null);
            if (image != null) {
                try {
                    ImageIO.write(image, "PNG", new File(String.format("graph-P=%s.png", maxP)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Failed to render the graph for P = " + maxP);
            }
        }
    }

    private static void generateEdgesAndNodes(Set<Pair> pairs, Set<String> nodes, int minN, int maxP, int difference, Set<Rule> rules, int maxDepth) {
        Set<Pair> currentPairs = new HashSet<>(pairs);
        for (int depth = 0; depth < maxDepth; depth++) {
            Set<Pair> nextPairs = new HashSet<>();
            for (Pair pair : currentPairs) {
                int S = pair.first + pair.second;
                int P = pair.first * pair.second;
                
                for (Pair sumPair : HelperFunctions.pairsFromSum(S, minN, difference)) {
                    nextPairs.add(sumPair);
                    nodes.add("S" + S);
                    nodes.add("P" + (sumPair.first * sumPair.second));
                    String first = "S" + S;
                    String second = "P" + (sumPair.first * sumPair.second);
                    String steps = StepsToSolve.stepsToSolve(sumPair, minN, maxP, difference, 0);
                    String third = String.format("(%1$s,%2$s)%3$s", sumPair.first, sumPair.second, steps);
                    rules.add(new Rule(first, second, third, true));
                }
                
                for (Pair productPair : HelperFunctions.pairsFromProduct(P, minN, difference)) {
                    nextPairs.add(productPair);
                    nodes.add("P" + P);
                    nodes.add("S"+ (productPair.first + productPair.second));
                    String first = "P" + P;
                    String second = "S" + (productPair.first + productPair.second);
                    String steps = StepsToSolve.stepsToSolve(productPair, minN, maxP, difference, 0);
                    String third = String.format("(%1$s,%2$s)%3$s", productPair.first, productPair.second, steps);
                    rules.add(new Rule(first, second, third, true));
                }
            
            }
            currentPairs = nextPairs;
            pairs.addAll(nextPairs);
        }
    }
    
    private static int countSumWays(int num, int minN, int difference) {
        int count = 0;
        for (int i = minN; i <= num; i++) {
            for (int j = i + difference; j <= num; j++) {
                if (i + j == num) {
                    count++;
                }
            }
        }
        return count;
    }

    private static int countProductWays(int num, int minN, int difference) {
        int count = 0;
        for (int i = minN; i <= num; i++) {
            for (int j = i + difference; j <= num; j++) {
                if (i * j == num) {
                    count++;
                }
            }
        }
        return count;
    }
}