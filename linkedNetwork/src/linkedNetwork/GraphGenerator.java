package linkedNetwork;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

import java.util.Set;

public class GraphGenerator {
    public static Graph<String, DefaultEdge> generateGraph(Set<String> nodeSet, Set<Rule> rules) {
        Graph<String, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);

        for (String node : nodeSet) {
            graph.addVertex(node);
        }

        for (Rule rule : rules) {
            applyRule(graph, rule);
        }

        return graph;
    }

    private static void applyRule(Graph<String, DefaultEdge> graph, Rule rule) {
        for (String source : graph.vertexSet()) {
            for (String target : graph.vertexSet()) {
                if (!source.equals(target) && rule.matches(source, target)) {
                    graph.addEdge(source, target);
                }
            }
        }
    }
}