package glCore.events.mouseEvent;

import glCore.events.EventType;

public class MouseButtonPressedEvent extends MouseButtonEvent{

    public MouseButtonPressedEvent(int button){
        super(button);
    }

    @Override
    public EventType getEventType() {
        return EventType.MouseButtonPressed;
    }

    @Override
    public String toString() {
        return "MouseButtonPressedEvent: " + _button;
    }
}
