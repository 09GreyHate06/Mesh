package glCore.events.keyEvent;

import glCore.events.Event;
import glCore.events.EventCategory;

public abstract class KeyEvent extends Event {

    protected int _keyCode;

    protected KeyEvent(int keyCode){
        _keyCode = keyCode;
    }

    public int getKeyCode(){
        return _keyCode;
    }

    @Override
    public int getCategoryFlags() {
        return EventCategory.Keyboard.label | EventCategory.Input.label;
    }
}
