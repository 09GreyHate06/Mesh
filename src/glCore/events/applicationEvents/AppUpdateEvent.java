package glCore.events.applicationEvents;

import glCore.events.Event;
import glCore.events.EventCategory;
import glCore.events.EventType;

public class AppUpdateEvent extends Event {

    public AppUpdateEvent(){

    }

    @Override
    public EventType getEventType() {
        return EventType.AppUpdate;
    }

    @Override
    public String toString() {
        return "AppUpdateEvent";
    }

    @Override
    public int getCategoryFlags() {
        return EventCategory.Application.label;
    }
}
