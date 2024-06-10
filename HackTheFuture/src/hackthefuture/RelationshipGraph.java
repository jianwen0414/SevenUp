/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hackthefuture;

/**
 * This class provides methods to create and display a graph representing parent-child relationships.
 * It uses the GraphStream library for graph creation and visualization.
 * Author: Jian Wen Lee
 */
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import java.util.List;

public class RelationshipGraph {

    /**
     * Creates a graph based on a list of parent-child relationships.
     * @param relationships The list of parent-child relationships.
     * @return A GraphStream graph representing the relationships.
     */
    public static Graph createGraph(List<ParentChildRelationship> relationships) {
        Graph graph = new SingleGraph("Family Tree"); // Create a new graph with the name "Family Tree"

        // Iterate over each parent-child relationship
        for (ParentChildRelationship relationship : relationships) {
            String parentNode = String.valueOf(relationship.getParentUsername()); // Get parent username
            String childNode = String.valueOf(relationship.getChildUsername()); // Get child username

            // Add parent node to the graph if it doesn't already exist
            if (graph.getNode(parentNode) == null) {
                graph.addNode(parentNode).setAttribute("ui.label", parentNode);
            }

            // Add child node to the graph if it doesn't already exist
            if (graph.getNode(childNode) == null) {
                graph.addNode(childNode).setAttribute("ui.label", childNode);
            }

            // Create an edge between the parent and child nodes
            String edgeId = parentNode + "-" + childNode;
            if (graph.getEdge(edgeId) == null) {
                graph.addEdge(edgeId, parentNode, childNode, true); // true indicates directed edge
            }
        }

        return graph; // Return the constructed graph
    }

    /**
     * Displays the given graph in a new window.
     * @param graph The graph to be displayed.
     */
    public static void displayGraph(Graph graph) {
        Viewer viewer = graph.display(); // Display the graph in a viewer
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY); // Set the close frame policy to hide only
    }
}
