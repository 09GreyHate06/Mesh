package glCore.core;

import glCore.events.Event;

public abstract class Layer {
    protected String _debugName;

    protected Layer(String name) {
        _debugName = name;
    }

    public void onAttach() {}
    public void onDetach() {}
    public void onUpdate() {}
    public void onEvent(Event event) {}
    public void onImGuiRender() {}

    public final String getName(){
        return _debugName;
    }
}
