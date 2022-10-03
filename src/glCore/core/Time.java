package glCore.core;

import org.lwjgl.glfw.GLFW;

import java.util.concurrent.TimeUnit;

public class Time {

    private static float _lastFrame;
    private static float _startTime;
    private static float _deltaBuffer;

    private Time(){

    }

    public static void init(){
        _startTime = getTime();
        _lastFrame = _startTime;
    }

    public static void updateDelta(){
        var old = _lastFrame;
        _lastFrame = getTime();
        _deltaBuffer = _lastFrame - old;
    }

    public static float getTime(){
        return (float)GLFW.glfwGetTime();
    }

    public static float delta(){
        return _deltaBuffer;
    }
}
