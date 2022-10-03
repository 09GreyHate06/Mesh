package editor;

import glCore.core.Input;
import glCore.core.Time;
import glCore.events.Event;
import glCore.events.EventDispatcher;
import glCore.events.mouseEvent.MouseScrolledEvent;
import org.joml.*;
import org.joml.Math;
import org.lwjgl.glfw.GLFW;

public class EditorCamera {

    private float _aspect, _fov, _near, _far;
    private float _pitch, _yaw;
    private Vector3f _position;

    private Matrix4f _projection, _view;
    private Vector3f _focalPoint;

    private Vector2f _initialMousePos;
    private boolean _reset;
    private float _distance;
    private float _viewportWidth, _viewportHeight;

    public EditorCamera(float fov, float viewportWidth, float viewportHeight, float near, float far,
                        float yaw, float pitch, Vector3f position){
        _aspect = viewportWidth / viewportHeight;
        _fov = fov;
        _near = near;
        _far = far;
        _yaw = yaw;
        _pitch = pitch;
        _position = position;

        _projection = new Matrix4f().identity();
        _view = new Matrix4f().identity();
        _focalPoint = new Vector3f();
        _initialMousePos = new Vector2f();
        _reset = true;
        _distance = 10.0f;
        _viewportWidth = viewportWidth;
        _viewportHeight = viewportHeight;

        updateProjectionMatrix();
        updateViewMatrix();
    }

    public EditorCamera(float fov, float viewportWidth, float viewportHeight, float near, float far){
        this(fov, viewportWidth, viewportHeight, near, far, 0.0f, 0.0f, new Vector3f());
    }

    public EditorCamera(){
        this(45.0f, 1280.0f, 720.0f, 0.1f, 1000.0f);
    }

    public void onUpdate(){
        if(_reset){
            _initialMousePos = Input.getMousePos();
            _reset = false;
        }

        if(Input.getKey(GLFW.GLFW_KEY_LEFT_ALT)){
            var mouse = Input.getMousePos();
            var delta = mouse.sub(_initialMousePos, new Vector2f()).mul(0.003f, new Vector2f());
            _initialMousePos = mouse;

            if(Input.getKey(GLFW.GLFW_KEY_LEFT_CONTROL) && Input.getMouseButton(GLFW.GLFW_MOUSE_BUTTON_LEFT))
                mousePan(delta);
            else if(Input.getMouseButton(GLFW.GLFW_MOUSE_BUTTON_LEFT))
                mouseRotate(delta);
        }

        updateViewMatrix();
    }

    public void onEvent(Event event){
        EventDispatcher dispatcher = new EventDispatcher(event);
        dispatcher.dispatch(this::onMouseScroll);

    }

    public void setViewportSize(float width, float height){
        _viewportWidth = width;
        _viewportHeight = height;
        updateProjectionMatrix();
    }

    public void setDistance(float distance){
        _distance = distance;
    }

    public float getDistance(){
        return _distance;
    }

    public final Matrix4f getViewMatrix(){
        return _view;
    }

    public final Matrix4f getProjectionMatrix(){
        return _projection;
    }

    public Vector3f getUpDirection(){
        return new Vector3f(0.0f, 1.0f, 0.0f).rotate(getOrientation());
    }

    public Vector3f getRightDirection(){
        return new Vector3f(1.0f, 0.0f, 0.0f).rotate(getOrientation());
    }

    public Vector3f getForwardDirection(){
        return new Vector3f(0.0f, 0.0f, -1.0f).rotate(getOrientation());
    }

    public final Vector3f getPosition(){
        return _position;
    }

    public Quaternionf getOrientation(){
        return new Quaternionf().rotateYXZ(-_yaw, -_pitch, 0.0f);
    }

    public float getPitch(){
        return _pitch;
    }

    public float getYaw(){
        return _yaw;
    }

    public void reset(){
        _reset = true;
    }

    private void updateProjectionMatrix(){
        _aspect = _viewportWidth / _viewportHeight;
        _projection.identity();
        _projection.perspective(Math.toRadians(_fov), _aspect, _near, _far);
    }

    private void updateViewMatrix(){
        _position = calculatePosition();
        _view.identity();
        _view.translate(_position);
        _view.rotate(getOrientation());
        _view.invert();
    }

    private boolean onMouseScroll(MouseScrolledEvent event){
        float delta = event.getYOffset() * 0.1f;
        mouseZoom(delta);
        updateViewMatrix();

        return false;
    }

    private void mousePan(Vector2f delta){
        var speed = panSpeed();
        _focalPoint.add(getRightDirection().negate().mul(delta.x).mul(speed.x).mul(_distance));
        _focalPoint.add(getUpDirection().mul(delta.y).mul(speed.y).mul(_distance));
    }

    private void mouseRotate(Vector2f delta){
        float yawSign = getUpDirection().y < 0 ? -1.0f : 1.0f;
        _yaw += yawSign * delta.x * rotationSpeed();
        _pitch += delta.y * rotationSpeed();
    }

    private void mouseZoom(float delta){
        _distance -= delta * zoomSpeed();
        if(_distance < 1.0f){
            _focalPoint.add(getForwardDirection());
            _distance = 1.0f;
        }
    }

    private Vector3f calculatePosition(){
        return _focalPoint.sub(getForwardDirection().mul(_distance), new Vector3f());
    }

    private Vector2f panSpeed(){
        float x = Math.min(_viewportWidth / 1000.0f, 2.4f); // max 2.4f;
        float xFactor = 0.0366f * (x * x) - 0.1778f * x + 0.3021f;

        float y = Math.min(_viewportHeight / 1000.0f, 2.4f); // max 2.4
        float yFactor = 0.0366f * (y * y) - 0.1778f * y + 0.3021f;

        return new Vector2f(xFactor, yFactor);
    }

    private float rotationSpeed(){
        return 0.8f;
    }

    private float zoomSpeed(){
        float distance = _distance * 0.2f;
        distance = Math.max(distance, 0.0f);
        float speed = distance * distance;
        speed = Math.min(speed, 100.0f); // max speed = 100
        return speed;
    }
}
