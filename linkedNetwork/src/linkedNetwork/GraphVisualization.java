package linkedNetwork;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import com.mxgraph.layout.mxFastOrganicLayout;
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
        
        // Define the range for P and S nodes
        int minP = 2; // must be 2
        int maxP = 200;
        int minS = 1; // must be 1
        int maxS = 200;

        // Generate nodes
        Set<String> nodeSet = generateNodes(minP, maxP, minS, maxS);

        // Define rules for creating edges
        int limit = 50;
        Set<Rule> rules = new HashSet<>();
        for (int i = 1; i <= limit; i++) {
            for (int j = i+1; j <= limit; j++) {
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
            for (String node : nodeSet) {
                Object vertex = mxGraph.insertVertex(parent, null, node, 100, 100, 30, 30);
                vertexMap.put(node, vertex);
                edgeCountMap.put(vertex, 0);
            }

            // Add edges to the mxGraph
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
                edgeCountMap.put(sourceVertex, edgeCountMap.get(sourceVertex) + 1);
                edgeCountMap.put(targetVertex, edgeCountMap.get(targetVertex) + 1);
            }

            // Apply colors based on edge counts
            mxStylesheet stylesheet = mxGraph.getStylesheet();
            for (Map.Entry<String, Object> entry : vertexMap.entrySet()) {
                String vertex = entry.getKey();
                Object cell = entry.getValue();
                int edgeCount = edgeCountMap.get(cell);

                Map<String, Object> style = new HashMap<>();
                if (edgeCount == 0) {
                    style.put(mxConstants.STYLE_FILLCOLOR, "grey");
                } else if (edgeCount == 1) {
                    style.put(mxConstants.STYLE_FILLCOLOR, "green");
                } else if (edgeCount == 2) {
                    style.put(mxConstants.STYLE_FILLCOLOR, "yellow");
                } else {
                    style.put(mxConstants.STYLE_FILLCOLOR, "red");
                }

                stylesheet.putCellStyle("STYLE_" + vertex, style);
                ((mxCell) cell).setStyle("STYLE_" + vertex);
            }
        } finally {
            mxGraph.getModel().endUpdate();
        }

        // Use a force-directed layout to position the nodes
        mxIGraphLayout layout = new mxFastOrganicLayout(mxGraph);
        ((mxFastOrganicLayout) layout).setForceConstant(100); // Further increase the force constant to reduce overlapping
        ((mxFastOrganicLayout) layout).setInitialTemp(200); // Increase the initial temperature for better spreading
        ((mxFastOrganicLayout) layout).setMinDistanceLimit(100); // Increase the minimum distance between nodes
        layout.execute(parent);

        // Export the graph as an image
        BufferedImage image = mxCellRenderer.createBufferedImage(mxGraph, null, 2, java.awt.Color.WHITE, true, null);
        try {
            ImageIO.write(image, "PNG", new File("graph.png"));
        } catch (IOException e) {
            e.printStackTrace();
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
}