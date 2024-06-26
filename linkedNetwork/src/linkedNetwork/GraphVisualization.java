package linkedNetwork;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;

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
            	String first = "P" + String.valueOf(i*j);
            	String second = "S" + String.valueOf(i+j);
            	String third = String.format("(%1$s,%2$s)",i,j);
            	rules.add(new Rule(first, second, third, true));
            }
        }

        // Generate the graph using the rules
        Graph<String, DefaultEdge> graph = GraphGenerator.generateGraph(nodeSet, rules);

        // Create a visualization using JGraphX
        mxGraph mxGraph = new mxGraph();
        Object parent = mxGraph.getDefaultParent();

        Map<String, Object> vertexMap = new HashMap<>();

        mxGraph.getModel().beginUpdate();
        try {
            for (String node : nodeSet) {
                Object vertex = mxGraph.insertVertex(parent, null, node, 100, 100, 30, 30);
                vertexMap.put(node, vertex);
            }

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

                mxGraph.insertEdge(parent, null, label, vertexMap.get(source), vertexMap.get(target));
            }
        } finally {
            mxGraph.getModel().endUpdate();
        }

        // Use a force-directed layout to position the nodes
        mxIGraphLayout layout = new mxFastOrganicLayout(mxGraph);
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