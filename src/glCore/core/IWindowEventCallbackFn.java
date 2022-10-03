package glCore.core;

import glCore.events.Event;

@FunctionalInterface
public interface IWindowEventCallbackFn {
    void invoke(Event event);
}
