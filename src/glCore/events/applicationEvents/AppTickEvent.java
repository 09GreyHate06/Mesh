package glCore.events.applicationEvents;

import glCore.events.Event;
import glCore.events.EventCategory;
import glCore.events.EventType;

public class AppTickEvent extends Event {

    public AppTickEvent(){

    }

    @Override
    public EventType getEventType() {
        return EventType.AppTick;
    }

    @Override
    public String toString() {
        return "AppTickEvent";
    }

    @Override
    public int getCategoryFlags() {
        return EventCategory.Application.label;
    }
}
