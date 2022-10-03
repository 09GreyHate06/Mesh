package glCore.events.mouseEvent;

import glCore.events.Event;
import glCore.events.EventCategory;

public abstract class MouseButtonEvent extends Event {
    protected int _button;

    protected MouseButtonEvent(int button){
        _button = button;
    }

    public int GetMouseButton(){
        return _button;
    }

    @Override
    public int getCategoryFlags() {
        return EventCategory.Mouse.label | EventCategory.Input.label;
    }
}
