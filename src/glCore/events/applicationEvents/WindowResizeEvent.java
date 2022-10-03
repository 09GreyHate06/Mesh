package glCore.events.applicationEvents;

import glCore.events.Event;
import glCore.events.EventCategory;
import glCore.events.EventType;

public class WindowResizeEvent extends Event {

    private final int _width;
    private final int _height;

    public WindowResizeEvent(int width, int height){
        _width = width;
        _height = height;
    }

    public int getWidth(){
        return _width;
    }

    public int getHeight(){
        return _height;
    }

    @Override
    public EventType getEventType() {
        return EventType.WindowResize;
    }

    @Override
    public String toString() {
        return "WindowResizeEvent: " + _width + ", " + _height;
    }

    @Override
    public int getCategoryFlags() {
        return EventCategory.Application.label;
    }
}
