package glCore.events.applicationEvents;

import glCore.events.Event;
import glCore.events.EventCategory;
import glCore.events.EventType;

public class WindowCloseEvent extends Event {

    public WindowCloseEvent(){

    }

    @Override
    public EventType getEventType() {
        return EventType.WindowClose;
    }

    @Override
    public String toString() {
        return "WindowCloseEvent";
    }

    @Override
    public int getCategoryFlags() {
        return EventCategory.Application.label;
    }
}
