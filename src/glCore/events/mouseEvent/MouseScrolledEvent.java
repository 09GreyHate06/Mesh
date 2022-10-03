package glCore.events.mouseEvent;

import glCore.events.Event;
import glCore.events.EventCategory;
import glCore.events.EventType;

public class MouseScrolledEvent extends Event {

    private final float _xOffset;
    private final float _yOffset;

    public MouseScrolledEvent(float xOffset, float yOffset){
        _xOffset = xOffset;
        _yOffset = yOffset;
    }

    public float getXOffset(){
        return  _xOffset;
    }

    public float getYOffset(){
        return  _yOffset;
    }

    @Override
    public EventType getEventType() {
        return EventType.MouseScrolled;
    }

    @Override
    public String toString() {
        return "MouseScrolledEvent: " + _xOffset + ", " + _yOffset;
    }

    @Override
    public int getCategoryFlags() {
        return EventCategory.Mouse.label | EventCategory.Input.label;
    }
}
