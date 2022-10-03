package glCore.events.mouseEvent;

import glCore.events.EventType;

public class MouseButtonReleasedEvent extends MouseButtonEvent {

    public MouseButtonReleasedEvent(int button){
        super(button);
    }

    @Override
    public EventType getEventType() {
        return EventType.MouseButtonReleased;
    }

    @Override
    public String toString() {
        return "MouseButtonReleasedEvent: " + _button;
    }
}
