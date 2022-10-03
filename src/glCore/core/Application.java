package glCore.core;

import glCore.events.Event;
import glCore.events.EventDispatcher;
import glCore.events.applicationEvents.WindowCloseEvent;
import glCore.events.applicationEvents.WindowResizeEvent;
import glCore.imguiLayer.ImGuiLayer;

public abstract class Application {

    private final Window _window;
    private final ImGuiLayer _imGuiLayer;
    private final LayerStack _layerStack;
    private boolean _running = true;
    private boolean _minimized = false;
    

    private static Application _instance = null;

    protected Application(){
        assert _instance == null : "Application already exists";

        _instance = this;

        _window = new Window(1280, 720, "Mesh");
        _window.setEventCallback(this::onEvent);

        _layerStack = new LayerStack();

        _imGuiLayer = new ImGuiLayer("ImGuiLayer");
        pushOverlay(_imGuiLayer);
    }

    public void pushLayer(Layer layer){
        _layerStack.pushLayer(layer);
    }

    public void popLayer(Layer layer){
        _layerStack.popLayer(layer);
    }

    public void pushOverlay(Layer overlay){
        _layerStack.pushOverlay(overlay);
    }

    public void popOverlay(Layer overlay){
        _layerStack.popOverlay(overlay);
    }

    public final ImGuiLayer getImGuiLayer(){
        return _imGuiLayer;
    }

    public final Window getWindow(){
        return _window;
    }

    public void close(){
        _running = false;
    }

    private void shutdown(){
        _layerStack.disposeLayers();
        _window.shutdown();
    }

    private void run(){
        Time.init();

        while (_running){
            Time.updateDelta();

            if(!_minimized){

                for(Layer layer : _layerStack.getLayers())
                    layer.onUpdate();

                _imGuiLayer.begin();
                for (Layer layer : _layerStack.getLayers())
                    layer.onImGuiRender();
                _imGuiLayer.end();

            }

            _window.updateWindow();
        }
    }

    private void onEvent(Event event){
        EventDispatcher dispatcher = new EventDispatcher(event);
        dispatcher.dispatch(this::onWindowClose);
        dispatcher.dispatch(this::onWindowResized);

        for(int i = _layerStack.getLayers().size() - 1; i >= 0; i--){
            if(event.Handled) break;

            _layerStack.getLayers().get(i).onEvent(event);
        }
    }

    protected static void launch(Application app){
        app.run();
        app.shutdown();
    }

    private boolean onWindowResized(WindowResizeEvent event){
        if(event.getWidth() == 0 || event.getHeight() == 0){
            _minimized = true;
            return  false;
        }

        _minimized = false;
        _window.setViewport(0, 0, event.getWidth(), event.getHeight());
        return false;
    }

    private boolean onWindowClose(WindowCloseEvent event){
        _running = false;
        return true;
    }


    public static Application get(){
        return _instance;
    }
}
