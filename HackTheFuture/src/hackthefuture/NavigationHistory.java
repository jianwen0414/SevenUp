package hackthefuture;

import java.util.Stack;
import javafx.scene.Scene;

public class NavigationHistory {

    private static Stack<Scene> historyStack = new Stack<>();

    public static void push(Scene scene) {
        historyStack.push(scene);
    }

    public static Scene pop() {
        return historyStack.isEmpty() ? null : historyStack.pop();
    }

    public static boolean isEmpty() {
        return historyStack.isEmpty();
    }
}
