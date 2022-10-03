package glCore.events;

public abstract class Event {
    public boolean Handled = false;

    public abstract EventType getEventType();
    public abstract int getCategoryFlags();
    public abstract String toString();
    public boolean isInCategory(EventCategory category){
        return (getCategoryFlags() & category.label) > 0;
    }
}
