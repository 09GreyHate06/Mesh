package glCore.events.mouseEvent;

import glCore.events.Event;
import glCore.events.EventCategory;
import glCore.events.EventType;

public class MouseMovedEvent extends Event {

    private final float _mouseX;
    private final float _mouseY;

    public MouseMovedEvent(float mouseX, float mouseY){
        _mouseX = mouseX;
        _mouseY = mouseY;
    }

    public float getX(){
        return _mouseX;
    }

    public float getY(){
        return _mouseY;
    }

    @Override
    public EventType getEventType() {
        return EventType.MouseMoved;
    }

    @Override
    public String toString() {
        return "MouseMovedEvent: " + _mouseX + ", " + _mouseY;
    }

    @Override
    public int getCategoryFlags() {
        return EventCategory.Mouse.label | EventCategory.Input.label;
    }
}
