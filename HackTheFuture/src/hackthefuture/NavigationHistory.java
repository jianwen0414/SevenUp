package hackthefuture;

import java.util.Stack;
import javafx.scene.Scene;

public class NavigationHistory {

    // Stack to keep track of the navigation history
    private static Stack<Scene> historyStack = new Stack<>();

    /**
     * Pushes the current scene onto the history stack.
     * This method is used to save the current state before navigating to a new scene.
     *
     * @param scene The current scene to be saved in the history stack.
     */
    public static void push(Scene scene) {
        historyStack.push(scene);
    }

    /**
     * Pops the last scene from the history stack.
     * This method is used to retrieve the previous scene when navigating back.
     *
     * @return The last scene from the history stack, or null if the stack is empty.
     */
    public static Scene pop() {
        return historyStack.isEmpty() ? null : historyStack.pop();
    }

    /**
     * Checks if the history stack is empty.
     * This method is used to determine if there are any previous scenes to navigate back to.
     *
     * @return True if the history stack is empty, otherwise false.
     */
    public static boolean isEmpty() {
        return historyStack.isEmpty();
    }
}
