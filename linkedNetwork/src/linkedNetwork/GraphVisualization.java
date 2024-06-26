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
import java.util.Map;

public class GraphVisualization {
    public static void main(String[] args) {
        // Create the graph using JGraphT
        Graph<String, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);

        // Add the nodes
        String[] nodes = {"P2", "P3", "P4", "P5", "P6", "P7", "P8", "P9", "P10",
                "P11", "P12", "P13", "P14", "P15", "P16", "P17", "P18", "P19", "P20",
                "P21", "P22", "P23", "P24",
                "S1", "S2", "S3", "S4", "S5", "S6", "S7", "S8", "S9", "S10",
                "S11", "S12", "S13", "S14", "S15", "S16", "S17", "S18", "S19", "S20",
                "S21", "S22", "S23", "S24"};

        for (String node : nodes) {
            graph.addVertex(node);
        }

        // Add the edges (adapt this to match the edges in your image)
        graph.addEdge("P2", "S3");
        graph.addEdge("P3", "S4");
        graph.addEdge("P4", "S5");
        graph.addEdge("S5", "P6");
        graph.addEdge("P6", "S7");
        graph.addEdge("S7", "P10");
        graph.addEdge("S7", "P12");

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
                if ((source.equals("P2") && target.equals("S3")) || (source.equals("S3") && target.equals("P2"))) {
                    label = "(1,2)^1";
                } else if ((source.equals("P3") && target.equals("S4")) || (source.equals("S4") && target.equals("P3"))) {
                    label = "(1,3)^1";
                }
                mxGraph.insertEdge(parent, null, label, vertexMap.get(source), vertexMap.get(target));
            }
        } finally {
            mxGraph.getModel().endUpdate();
        }

        // Use a layout to position the nodes
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