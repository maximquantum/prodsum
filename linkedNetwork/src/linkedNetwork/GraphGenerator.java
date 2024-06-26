package linkedNetwork;

import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

public class GraphGenerator {
    public static Graph<String, DefaultEdge> generateGraph(Set<String> nodeSet, Set<Rule> rules) {
        Graph<String, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);

        // Add nodes to the graph
        for (String node : nodeSet) {
            graph.addVertex(node);
        }

        // Apply rules to generate edges
        for (Rule rule : rules) {
            applyRule(graph, rule);
        }

        return graph;
    }

    private static void applyRule(Graph<String, DefaultEdge> graph, Rule rule) {
        // Example: Connect every node starting with "P" to every node starting with "S"
        for (String source : graph.vertexSet()) {
            for (String target : graph.vertexSet()) {
                if (!source.equals(target) && rule.matches(source, target)) {
                    graph.addEdge(source, target);
                }
            }
        }
    }
}