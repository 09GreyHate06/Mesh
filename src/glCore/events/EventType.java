package glCore.events;

public enum EventType {
    None(0),
    WindowClose(1), WindowResize(2), WindowFocus(3), WindowLostFocus(4), WindowMoved(5),
    AppTick(6), AppUpdate(7), AppRender(8),
    KeyPressed(9), KeyReleased(10), KeyTyped(11),
    MouseButtonPressed(12), MouseButtonReleased(13), MouseMoved(14), MouseScrolled(15);

    public final int label;

    private EventType(int label) {
        this.label = label;
    }
}
