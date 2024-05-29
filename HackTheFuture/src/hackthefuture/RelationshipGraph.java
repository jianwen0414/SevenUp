/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hackthefuture;

/**
 *
 * @author Jian Wen Lee
 */
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import java.util.List;

public class RelationshipGraph {

    public static Graph createGraph(List<ParentChildRelationship> relationships) {
        Graph graph = new SingleGraph("Family Tree");

        for (ParentChildRelationship relationship : relationships) {
            String parentNode = String.valueOf(relationship.getParentUsername());
            String childNode = String.valueOf(relationship.getChildUsername());

            if (graph.getNode(parentNode) == null) {
                graph.addNode(parentNode).setAttribute("ui.label", parentNode);
            }

            if (graph.getNode(childNode) == null) {
                graph.addNode(childNode).setAttribute("ui.label", childNode);
            }

            String edgeId = parentNode + "-" + childNode;
            if (graph.getEdge(edgeId) == null) {
                graph.addEdge(edgeId, parentNode, childNode, true);
            }
        }

        return graph;
    }

    public static void displayGraph(Graph graph) {
        Viewer viewer = graph.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
    }
}



