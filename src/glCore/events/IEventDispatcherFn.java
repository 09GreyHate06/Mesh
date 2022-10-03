package glCore.events;

@FunctionalInterface
public interface IEventDispatcherFn<T extends Event> {
    boolean invoke(T arg);
}
