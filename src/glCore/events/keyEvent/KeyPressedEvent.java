package glCore.events.keyEvent;

import glCore.events.EventType;

public class KeyPressedEvent extends  KeyEvent {

    private long _repeat;

    public KeyPressedEvent(int keyCode, long repeat){
        super(keyCode);
        _repeat = repeat;
    }

    long getRepeatCount(){
        return _repeat;
    }

    @Override
    public EventType getEventType() {
        return EventType.KeyPressed;
    }

    @Override
    public String toString() {
        return "KeyPressedEvent: " + _keyCode + ", repeat: " + _repeat;
    }
}
