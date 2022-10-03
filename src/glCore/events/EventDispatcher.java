package glCore.events;

public class EventDispatcher {
    private Event _event;

    public EventDispatcher(Event event){
        _event = event;
    }

    public <T extends Event> boolean dispatch(IEventDispatcherFn<T> eventDispatcherFn){
        try{
            //noinspection unchecked
            T e = (T)_event;
            _event.Handled = eventDispatcherFn.invoke(e);
            return true;
        } catch (Exception ignored){
            return false;
        }
    }
}
