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
        // Define nodes
        Set<String> nodeSet = new HashSet<>();
        String[] nodes = {"P2", "P3", "P4", "P5", "P6", "P7", "P8", "P9", "P10",
                "P11", "P12", "P13", "P14", "P15", "P16", "P17", "P18", "P19", "P20",
                "P21", "P22", "P23", "P24",
                "S1", "S2", "S3", "S4", "S5", "S6", "S7", "S8", "S9", "S10",
                "S11", "S12", "S13", "S14", "S15", "S16", "S17", "S18", "S19", "S20",
                "S21", "S22", "S23", "S24"};
        for (String node : nodes) {
            nodeSet.add(node);
        }

        // Define rules for creating edges
        int limit = 9;
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
            for (String node : nodes) {
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
}