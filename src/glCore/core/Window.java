package glCore.core;
import glCore.events.applicationEvents.WindowCloseEvent;
import glCore.events.applicationEvents.WindowResizeEvent;
import glCore.events.keyEvent.KeyPressedEvent;
import glCore.events.keyEvent.KeyReleasedEvent;
import glCore.events.keyEvent.KeyTypedEvent;
import glCore.events.mouseEvent.MouseButtonPressedEvent;
import glCore.events.mouseEvent.MouseButtonReleasedEvent;
import glCore.events.mouseEvent.MouseMovedEvent;
import glCore.events.mouseEvent.MouseScrolledEvent;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.*;


import java.nio.IntBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.GL33.*;

import static org.lwjgl.glfw.GLFW.*;

public class Window {

    private final long _windowHandle;
    private String _title;
    private int _width, _height;

    private IWindowEventCallbackFn _eventCallbackFn;
    private static long _keyRepeat = 0;

    public Window(int width, int height, String title){
        _width = width;
        _height = height;
        _title = title;

        _eventCallbackFn = null;

        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        if(!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // glfw window config
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_SAMPLES, 4);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        _windowHandle = glfwCreateWindow(_width, _height, _title, NULL, NULL);
        if(_windowHandle == NULL){
            throw new RuntimeException("Failed to create window!");
        }

        // center window to the monitor
        try(MemoryStack stack = stackPush()){
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            glfwGetWindowSize(_windowHandle, pWidth, pHeight);

            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            assert vidMode != null;
            glfwSetWindowPos(_windowHandle,
                    (vidMode.width() - pWidth.get(0)) / 2,
                    (vidMode.height() - pHeight.get(0)) / 2);
        }


        glfwMakeContextCurrent(_windowHandle);
        SetVSync(true); // vSync

        /*System.out.println("OpenGL Info:\n" +
        "                   Vendor: " + glGetString(GL_VENDOR) +
        "                   Renderer: " + glGetString(GL_RENDERER) +
        "                   Version: " + glGetString(GL_VERSION));*/

        // set callbacks
        setCallbacks();

        glfwShowWindow(_windowHandle);
        GL.createCapabilities();
    }

    public void shutdown(){
        glfwDestroyWindow(_windowHandle);
        //glfwSetErrorCallback(null).free();
        glfwTerminate();
    }

    public void updateWindow(){
        glfwPollEvents();
        glfwSwapBuffers(_windowHandle);
    }

    public void setTitle(String title){
        _title = title;
        glfwSetWindowTitle(_windowHandle, _title);
    }

    public int getWidth(){
        return _width;
    }

    public int getHeight(){
        return _height;
    }

    public void setViewport(int x, int y, int width, int height){
        glViewport(x, y, width, height);
    }

    public void SetVSync(boolean enabled){
        if(enabled)
            glfwSwapInterval(1);
        else
            glfwSwapInterval(0);
    }

    public void setEventCallback(IWindowEventCallbackFn eventCallback){
        _eventCallbackFn = eventCallback;
    }

    public long getWindowHandle(){
        return _windowHandle;
    }

    private void setCallbacks(){

        glfwSetWindowSizeCallback(_windowHandle, (handle, width, height) ->{
            _width = width;
            _height = height;
            _eventCallbackFn.invoke(new WindowResizeEvent(width, height));
        });

        glfwSetWindowCloseCallback(_windowHandle, (handle) -> _eventCallbackFn.invoke(new WindowCloseEvent()));

        glfwSetKeyCallback(_windowHandle, (handle, key, scancodes, action, mods) -> {
            switch (action){
                case GLFW_PRESS:
                    _keyRepeat = 0;
                    _eventCallbackFn.invoke(new KeyPressedEvent(key, _keyRepeat));
                    break;
                case GLFW_RELEASE:
                    _keyRepeat = 0;
                    _eventCallbackFn.invoke(new KeyReleasedEvent(key));
                    break;
                case GLFW_REPEAT:
                    _keyRepeat++;
                    _eventCallbackFn.invoke(new KeyPressedEvent(key, _keyRepeat));
                    break;
            }
        });

        glfwSetCharCallback(_windowHandle, (handle, keyCode) -> _eventCallbackFn.invoke(new KeyTypedEvent(keyCode)));

        glfwSetMouseButtonCallback(_windowHandle, (handle, button, action, mods) -> {
            switch (action){
                case GLFW_PRESS:
                    _eventCallbackFn.invoke(new MouseButtonPressedEvent(button));
                    break;
                case GLFW_RELEASE:
                    _eventCallbackFn.invoke(new MouseButtonReleasedEvent(button));
                    break;
            }
        });

        glfwSetCursorPosCallback(_windowHandle, (handle, xPos, yPos) -> _eventCallbackFn.invoke(new MouseMovedEvent((float)xPos, (float)yPos)));

        glfwSetScrollCallback(_windowHandle, (handle, xOffset, yOffset) -> _eventCallbackFn.invoke(new MouseScrolledEvent((float)xOffset, (float)yOffset)));


    }
}
