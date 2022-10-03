package glCore.core;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class Input {

    private Input() {

    }

    public static boolean getKey(int keycode){
        long window = Application.get().getWindow().getWindowHandle();

        int state = GLFW.glfwGetKey(window, keycode);
        return state == GLFW.GLFW_PRESS;
    }

    public static boolean getMouseButton(int button){
        long window = Application.get().getWindow().getWindowHandle();

        int state = GLFW.glfwGetMouseButton(window, button);
        return state == GLFW.GLFW_PRESS;
    }

    public static Vector2f getMousePos(){
        long window = Application.get().getWindow().getWindowHandle();

        double[] x = new double[1];
        double[] y = new double[1];
        GLFW.glfwGetCursorPos(window, x, y);
        return new Vector2f((float)x[0], (float)y[0]);
    }
}
