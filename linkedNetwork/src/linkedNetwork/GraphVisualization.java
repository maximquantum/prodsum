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
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GraphVisualization {
    public static void main(String[] args) {
        boolean visualizeZeroEdges = false; // Set this to true to visualize nodes with zero edges

        for (int k = 1; k <= 20; k++) {
            
            // Define the range for P and S nodes
            int minN = 0;
            int maxN = k;
            int minP = 0; // must be 2
            int maxP = maxN * (maxN - 1);
            int minS = 0; // must be 1
            int maxS = maxN + (maxN - 1);

            // Generate nodes
            Set<String> nodeSet = generateNodes(minP, maxP, minS, maxS);

            // Define rules for creating edges
            Set<Rule> rules = new HashSet<>();
            for (int i = minN; i <= maxN; i++) {
                for (int j = i + 1; j <= maxN; j++) {
                    String first = "P" + (i * j);
                    String second = "S" + (i + j);
                    String third = String.format("(%1$s,%2$s)", i, j);
                    rules.add(new Rule(first, second, third, true));
                }
            }

            // Generate the graph using the rules
            Graph<String, DefaultEdge> graph = GraphGenerator.generateGraph(nodeSet, rules);

            // Create a visualization using JGraphX
            mxGraph mxGraph = new mxGraph();
            Object parent = mxGraph.getDefaultParent();

            Map<String, Object> vertexMap = new HashMap<>();
            Map<Object, Integer> edgeCountMap = new HashMap<>();

            mxGraph.getModel().beginUpdate();
            try {
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
                    if (sourceVertex == null) {
                        sourceVertex = mxGraph.insertVertex(parent, null, source, 100, 100, 30, 30);
                        vertexMap.put(source, sourceVertex);
                        edgeCountMap.put(sourceVertex, 0);
                    }
                    if (targetVertex == null) {
                        targetVertex = mxGraph.insertVertex(parent, null, target, 100, 100, 30, 30);
                        vertexMap.put(target, targetVertex);
                        edgeCountMap.put(targetVertex, 0);
                    }
                    mxGraph.insertEdge(parent, null, label, sourceVertex, targetVertex);
                    edgeCountMap.put(sourceVertex, edgeCountMap.get(sourceVertex) + 1);
                    edgeCountMap.put(targetVertex, edgeCountMap.get(targetVertex) + 1);
                }

                // Apply colors based on new rules
                mxStylesheet stylesheet = mxGraph.getStylesheet();
                for (Map.Entry<String, Object> entry : vertexMap.entrySet()) {
                    String vertex = entry.getKey();
                    Object cell = entry.getValue();

                    Map<String, Object> style = new HashMap<>();
                    if (vertex.startsWith("S")) {
                        int num = Integer.parseInt(vertex.substring(1));
                        int ways = countSumWays(num, minN, maxN);
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
                        int ways = countProductWays(num, minN, maxN);
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
                    vertexMap.entrySet().removeIf(entry -> edgeCountMap.get(entry.getValue()) == 0);
                }
            } finally {
                mxGraph.getModel().endUpdate();
            }

            // Use an organic layout to position the nodes
            mxIGraphLayout layout = new mxOrganicLayout(mxGraph);
            layout.execute(parent);

            // Export the graph as an image
            BufferedImage image = mxCellRenderer.createBufferedImage(mxGraph, null, 2, java.awt.Color.WHITE, true, null);
            try {
                ImageIO.write(image, "PNG", new File(String.format("graph-N=%s.png", maxN)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Set<String> generateNodes(int minP, int maxP, int minS, int maxS) {
        Set<String> nodes = new HashSet<>();
        for (int i = minP; i <= maxP; i++) {
            nodes.add("P" + i);
        }
        for (int i = minS; i <= maxS; i++) {
            nodes.add("S" + i);
        }
        return nodes;
    }

    private static int countSumWays(int num, int minN, int maxN) {
        int count = 0;
        for (int i = minN; i <= maxN; i++) {
            for (int j = i + 1; j <= maxN; j++) {
                if (i + j == num) {
                    count++;
                }
            }
        }
        return count;
    }

    private static int countProductWays(int num, int minN, int maxN) {
        int count = 0;
        for (int i = minN; i <= maxN; i++) {
            for (int j = i + 1; j <= maxN; j++) {
                if (i * j == num) {
                    count++;
                }
            }
        }
        return count;
    }

    private static boolean isPrime(int num) {
        if (num <= 1) return false;
        for (int i = 2; i * i <= num; i++) {
            if (num % i == 0) return false;
        }
        return true;
    }

    private static boolean isPrimeSquare(int num) {
        int sqrt = (int) Math.sqrt(num);
        return sqrt * sqrt == num && isPrime(sqrt);
    }

    private static boolean isProductOfTwoPrimes(int num) {
        int count = 0;
        for (int i = 2; i <= num / 2; i++) {
            if (num % i == 0 && isPrime(i)) {
                count++;
                if (count > 2) return false;
            }
        }
        return count == 2;
    }
}