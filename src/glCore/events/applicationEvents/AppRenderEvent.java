package glCore.events.applicationEvents;

import glCore.events.Event;
import glCore.events.EventCategory;
import glCore.events.EventType;

public class AppRenderEvent extends Event {

    public AppRenderEvent(){

    }

    @Override
    public EventType getEventType() {
        return EventType.AppRender;
    }

    @Override
    public String toString() {
        return "AppRenderEvent";
    }

    @Override
    public int getCategoryFlags() {
        return EventCategory.Application.label;
    }
}
