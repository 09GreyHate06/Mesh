package glCore.events.keyEvent;

import glCore.events.EventType;

public class KeyTypedEvent extends KeyEvent {

    public KeyTypedEvent(int keyCode){
        super(keyCode);
    }

    @Override
    public EventType getEventType() {
        return EventType.KeyTyped;
    }

    @Override
    public String toString() {
        return "KeyTypedEvent: " + _keyCode;
    }
}
