package glCore.events.keyEvent;

import glCore.events.EventType;

public class KeyReleasedEvent extends KeyEvent {

    public KeyReleasedEvent(int keyCode){
        super(keyCode);
    }

    @Override
    public EventType getEventType() {
        return EventType.KeyReleased;
    }

    @Override
    public String toString() {
        return "KeyReleasedEvent: " + _keyCode;
    }
}
