package linkedNetwork;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GraphVisualization extends Application {

    private static final int MIN_P = 2;
    private static final int MAX_P = 10; // Adjusted for simplicity
    private static final int MIN_S = 1;
    private static final int MAX_S = 10; // Adjusted for simplicity
    private static final int WIDTH = 1600;
    private static final int HEIGHT = 1200;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Set<String> nodes = generateNodes(MIN_P, MAX_P, MIN_S, MAX_S);
        Set<Rule> rules = new HashSet<>();
        int limit = 12;
        for (int i = 1; i <= limit; i++) {
            for (int j = i + 1; j <= limit; j++) {
                String first = "P" + i * j;
                String second = "S" + i + j;
                String third = String.format("(%1$s,%2$s)", i, j);
                rules.add(new Rule(first, second, third, true));
            }
        }

        Graph<String, DefaultEdge> graph = GraphGenerator.generateGraph(nodes, rules);

        System.out.println("Generated Graph with Nodes: " + graph.vertexSet().size());
        System.out.println("Generated Graph with Edges: " + graph.edgeSet().size());

        Pane pane = new Pane();
        Map<String, Circle> nodeMap = new HashMap<>();
        Map<Circle, Text> labelMap = new HashMap<>();

        // Create nodes
        for (String vertex : graph.vertexSet()) {
            Circle circle = new Circle(10, Color.BLUE);
            circle.setCenterX(Math.random() * WIDTH);
            circle.setCenterY(Math.random() * HEIGHT);
            Text text = new Text(vertex);
            text.setX(circle.getCenterX() - 10);
            text.setY(circle.getCenterY() - 10);

            circle.setOnMouseClicked(event -> {
                // Add interaction here (e.g., display node info)
                System.out.println("Clicked on node: " + vertex);
            });

            nodeMap.put(vertex, circle);
            labelMap.put(circle, text);
            pane.getChildren().addAll(circle, text);
        }

        // Create edges with labels
        for (DefaultEdge edge : graph.edgeSet()) {
            String source = graph.getEdgeSource(edge);
            String target = graph.getEdgeTarget(edge);

            Circle sourceCircle = nodeMap.get(source);
            Circle targetCircle = nodeMap.get(target);

            System.out.println("Creating edge from " + source + " to " + target);

            Line line = new Line();
            line.startXProperty().bind(sourceCircle.centerXProperty());
            line.startYProperty().bind(sourceCircle.centerYProperty());
            line.endXProperty().bind(targetCircle.centerXProperty());
            line.endYProperty().bind(targetCircle.centerYProperty());

            line.setStrokeWidth(2);
            line.setStroke(Color.GRAY);

            // Find the rule that matches this edge
            String edgeLabelString = "";
            for (Rule rule : rules) {
                if (rule.matches(source, target)) {
                    edgeLabelString = rule.getLabel();
                    break;
                }
            }

            // Create label for edge
            Text edgeLabel = new Text();
            edgeLabel.xProperty().bind(line.startXProperty().add(line.endXProperty()).divide(2));
            edgeLabel.yProperty().bind(line.startYProperty().add(line.endYProperty()).divide(2));
            edgeLabel.setText(edgeLabelString);
            edgeLabel.toFront(); // Bring label to front

            pane.getChildren().addAll(line, edgeLabel);
        }

        // Ensure the pane size is set to enable panning
        pane.setPrefSize(WIDTH * 2, HEIGHT * 2);

        ScrollPane scrollPane = new ScrollPane(pane);
        scrollPane.setPannable(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        // Enable zoom functionality
        scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()) {
                double zoomFactor = 1.05;
                if (event.getDeltaY() <= 0) {
                    zoomFactor = 2.0 - zoomFactor;
                }
                pane.setScaleX(pane.getScaleX() * zoomFactor);
                pane.setScaleY(pane.getScaleY() * zoomFactor);
                event.consume();
            }
        });

        // Enable panning with arrow keys
        scrollPane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            double delta = 50;
            switch (event.getCode()) {
                case UP:
                    System.out.println("Arrow Key UP Pressed");
                    scrollPane.setVvalue(scrollPane.getVvalue() - delta / scrollPane.getContent().getBoundsInLocal().getHeight());
                    break;
                case DOWN:
                    System.out.println("Arrow Key DOWN Pressed");
                    scrollPane.setVvalue(scrollPane.getVvalue() + delta / scrollPane.getContent().getBoundsInLocal().getHeight());
                    break;
                case LEFT:
                    System.out.println("Arrow Key LEFT Pressed");
                    scrollPane.setHvalue(scrollPane.getHvalue() - delta / scrollPane.getContent().getBoundsInLocal().getWidth());
                    break;
                case RIGHT:
                    System.out.println("Arrow Key RIGHT Pressed");
                    scrollPane.setHvalue(scrollPane.getHvalue() + delta / scrollPane.getContent().getBoundsInLocal().getWidth());
                    break;
                default:
                    break;
            }
        });

        Scene scene = new Scene(scrollPane, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Interactive Graph Visualization");
        primaryStage.show();

        // Request focus on the ScrollPane to capture key events
        scrollPane.requestFocus();
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