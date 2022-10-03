package glCore.events;

public enum EventCategory {
    None(1),
    Application(1 << 1),
    Input(1 << 2),
    Keyboard(1 << 3),
    Mouse(1 << 4),
    MouseButton(1 << 5);

    public final int label;
    private EventCategory(int label){
        this.label = label;
    }
}
