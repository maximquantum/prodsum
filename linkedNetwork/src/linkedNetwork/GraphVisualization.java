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

        for (int k = 1; k <= 20; k++) {
            // Define the range for P and S nodes
            int difference = 1;
            int proportion = 0;
            int minN = 1;
            int maxN = k;

            // Generate nodes based on all possible pairs
            Set<String> nodeSet = generateNodesFromPairs(minN, maxN, difference, proportion);

            // Generate all possible pairs
            Set<Pair> pairs = PairVectorSpace.generatePairs(minN, maxN, difference, proportion);

            // Define rules for creating edges
            Set<Rule> rules = new HashSet<>();
            for (Pair pair : pairs) {
                String first = "P" + (pair.first * pair.second);
                String second = "S" + (pair.first + pair.second);
                String steps = StepsToSolve.stepsToSolve(pair, minN, maxN, difference, 0);
                String third = String.format("(%1$s,%2$s)%3$s", pair.first, pair.second, steps);
                rules.add(new Rule(first, second, third, true));
            }

            // Generate the graph using the rules
            Graph<String, DefaultEdge> graph = GraphGenerator.generateGraph(nodeSet, rules);

            // Create a visualization using JGraphX
            mxGraph mxGraph = new mxGraph();
            Object parent = mxGraph.getDefaultParent();

            Map<String, Object> vertexMap = new HashMap<>();
            Map<String, Integer> edgeCountMap = new HashMap<>();

            mxGraph.getModel().beginUpdate();
            try {
                // Add vertices to the mxGraph and initialize edge counts
                for (String node : nodeSet) {
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

                // Apply colors based on new rules
                mxStylesheet stylesheet = mxGraph.getStylesheet();
                for (Map.Entry<String, Object> entry : vertexMap.entrySet()) {
                    String vertex = entry.getKey();
                    Object cell = entry.getValue();
                    Map<String, Object> style = new HashMap<>();

                    if (vertex.startsWith("S")) {
                        int num = Integer.parseInt(vertex.substring(1));
                        int ways = countSumWays(num, minN, maxN, difference);
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
                        int ways = countProductWays(num, minN, maxN, difference);
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
//            mxIGraphLayout layout = new mxOrganicLayout(mxGraph);
//            layout.execute(parent);

            // Export the graph as an image
//            BufferedImage image = mxCellRenderer.createBufferedImage(mxGraph, null, 2, java.awt.Color.WHITE, true, null);
//            if (image != null) {
//                try {
//                    ImageIO.write(image, "PNG", new File(String.format("graph-N=%s.png", maxN)));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                System.out.println("Failed to render the graph for N = " + maxN);
//            }

            // Export the table as a CSV file
            try (FileWriter csvWriter = new FileWriter(String.format("pairs-N=%s.csv", maxN))) {
                csvWriter.append("x,y,s\n");
                for (Pair pair : pairs) {
                    String steps = StepsToSolve.stepsToSolve(pair, minN, maxN, difference, 0);
                    csvWriter.append(String.format("%d,%d,%s\n", pair.first, pair.second, steps));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Set<String> generateNodesFromPairs(int minN, int maxN, int difference, int proportion) {
        Set<String> nodes = new HashSet<>();
        for (int i = minN; i <= maxN; i++) {
            for (int j = Math.max(i + difference, proportion * i); j <= maxN; j++) {
                nodes.add("P" + (i * j));
                nodes.add("S" + (i + j));
            }
        }
        return nodes;
    }

    private static int countSumWays(int num, int minN, int maxN, int difference) {
        int count = 0;
        int numMaxN = Math.max(num, maxN);
        for (int i = minN; i <= numMaxN; i++) {
            for (int j = i + difference; j <= numMaxN; j++) {
                if (i + j == num) {
                    count++;
                }
            }
        }
        return count;
    }

    private static int countProductWays(int num, int minN, int maxN, int difference) {
        int count = 0;
        int numMaxN = Math.max(num, maxN);
        for (int i = minN; i <= numMaxN; i++) {
            for (int j = i + difference; j <= numMaxN; j++) {
                if (i * j == num) {
                    count++;
                }
            }
        }
        return count;
    }
    
}