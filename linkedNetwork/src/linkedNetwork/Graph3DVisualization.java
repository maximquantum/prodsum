package linkedNetwork;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Graph3DVisualization extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        int minP = 2;
        int maxP = 20; // Lower the limit for quick visualization
        int minS = 1;
        int maxS = 20; // Lower the limit for quick visualization

        Set<String> nodeSet = generateNodes(minP, maxP, minS, maxS);

        int limit = 10;
        Set<Rule> rules = new HashSet<>();
        for (int i = 1; i <= limit; i++) {
            for (int j = i + 1; j <= limit; j++) {
                String first = "P" + (i * j);
                String second = "S" + (i + j);
                String third = String.format("(%d,%d)", i, j);
                rules.add(new Rule(first, second, third, true));
            }
        }

        Graph<String, DefaultEdge> graph = GraphGenerator.generateGraph(nodeSet, rules);

        Group root = new Group();
        Map<String, Sphere> sphereMap = new HashMap<>();

        double radius = 200;
        double angleStep = 360.0 / nodeSet.size();
        int i = 0;
        for (String node : nodeSet) {
            double angle = Math.toRadians(i * angleStep);
            double x = radius * Math.cos(angle);
            double y = radius * Math.sin(angle);
            double z = radius * Math.sin(angle / 2);

            Sphere sphere = new Sphere(10);
            sphere.setMaterial(new PhongMaterial(Color.DODGERBLUE));
            sphere.setTranslateX(x);
            sphere.setTranslateY(y);
            sphere.setTranslateZ(z);

            sphereMap.put(node, sphere);
            root.getChildren().add(sphere);
            i++;
        }

        for (DefaultEdge edge : graph.edgeSet()) {
            String source = graph.getEdgeSource(edge);
            String target = graph.getEdgeTarget(edge);

            Sphere sourceSphere = sphereMap.get(source);
            Sphere targetSphere = sphereMap.get(target);

            double x1 = sourceSphere.getTranslateX();
            double y1 = sourceSphere.getTranslateY();
            double z1 = sourceSphere.getTranslateZ();

            double x2 = targetSphere.getTranslateX();
            double y2 = targetSphere.getTranslateY();
            double z2 = targetSphere.getTranslateZ();

            double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
            Cylinder edgeCylinder = new Cylinder(2, distance);
            edgeCylinder.setMaterial(new PhongMaterial(Color.GRAY));
            edgeCylinder.setTranslateX((x1 + x2) / 2);
            edgeCylinder.setTranslateY((y1 + y2) / 2);
            edgeCylinder.setTranslateZ((z1 + z2) / 2);

            // Rotations
            edgeCylinder.getTransforms().add(new javafx.scene.transform.Rotate(Math.toDegrees(Math.atan2(y2 - y1, x2 - x1)), javafx.scene.transform.Rotate.Z_AXIS));
            edgeCylinder.getTransforms().add(new javafx.scene.transform.Rotate(Math.toDegrees(Math.acos((z2 - z1) / distance)), javafx.scene.transform.Rotate.Y_AXIS));

            root.getChildren().add(edgeCylinder);
        }

        Scene scene = new Scene(root, 800, 600, true);
        scene.setFill(Color.BLACK);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-500);
        scene.setCamera(camera);

        primaryStage.setScene(scene);
        primaryStage.setTitle("3D Graph Visualization");
        primaryStage.show();
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