package glCore.imguiLayer;

import glCore.core.Application;
import glCore.core.Layer;
import glCore.events.Event;
import glCore.events.EventCategory;
import imgui.*;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

public class ImGuiLayer extends Layer {

    private boolean _blockEvents;
    private final ImGuiImplGlfw _implGlfw;
    private final ImGuiImplGl3 _implGl3;

    public ImGuiLayer(String name) {
        super(name);
        _blockEvents = true;
        _implGlfw = new ImGuiImplGlfw();
        _implGl3 = new ImGuiImplGl3();
    }

    public void begin(){
        _implGlfw.newFrame();
        ImGui.newFrame();
    }

    public void end(){
        ImGuiIO io = ImGui.getIO();
        Application app = Application.get();
        io.setDisplaySize((float)app.getWindow().getWidth(), (float)app.getWindow().getHeight());

        // rendering
        ImGui.render();
        _implGl3.renderDrawData(ImGui.getDrawData());

        if((io.getConfigFlags() & ImGuiConfigFlags.ViewportsEnable) > 0){
            long backupCurrentContext = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupCurrentContext);
        }
    }

    public void blockEvents(boolean enabled) {
        _blockEvents = enabled;
    }

    @Override
    public void onAttach() {

        ImGui.createContext();

        // config
        ImGuiIO io = ImGui.getIO();

        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        io.setWantCaptureKeyboard(true);
        io.setWantCaptureMouse(true);

        io.setFontDefault(io.getFonts().addFontFromFileTTF("assets/fonts/opensans/OpenSans-Regular.ttf",
                18.0f));

        // style
        ImGui.styleColorsDark();

        ImGuiStyle style = ImGui.getStyle();
        if((io.getConfigFlags() & ImGuiConfigFlags.DockingEnable) > 0){
            style.setWindowRounding(0.0f);
            style.getColor(ImGuiCol.WindowBg).w = 1.0f;
        }

        // wesley: set custom style
        setDarkModeThemeColors();

        Application app = Application.get();
        long windowHandle = app.getWindow().getWindowHandle();

        _implGlfw.init(windowHandle, true);
        _implGl3.init("#version 330");
    }

    @Override
    public void onDetach() {
        _implGl3.dispose();
        _implGlfw.dispose();
        ImGui.destroyContext();
    }

    @Override
    public void onEvent(Event event) {
        if(_blockEvents){
            ImGuiIO io = ImGui.getIO();
            event.Handled |= event.isInCategory(EventCategory.Mouse) & io.getWantCaptureMouse();
            event.Handled |= event.isInCategory(EventCategory.Keyboard) & io.getWantCaptureKeyboard();
        }
    }

    private void setDarkModeThemeColors(){
        ImGuiStyle style = ImGui.getStyle();
        style.setColor(ImGuiCol.WindowBg, 0.1f, 0.105f, 0.11f, 1.0f);

        // Headers
        style.setColor(ImGuiCol.Header, 0.2f, 0.205f, 0.21f, 1.0f);
        style.setColor(ImGuiCol.HeaderHovered, 0.3f, 0.305f, 0.31f, 1.0f);
        style.setColor(ImGuiCol.HeaderActive, 0.15f, 0.1505f, 0.151f, 1.0f);

        // Buttons
        style.setColor(ImGuiCol.Button, 0.2f, 0.205f, 0.21f, 1.0f);
        style.setColor(ImGuiCol.ButtonHovered, 0.3f, 0.305f, 0.31f, 1.0f);
        style.setColor(ImGuiCol.ButtonActive, 0.15f, 0.1505f, 0.151f, 1.0f);

        // Frame BG
        style.setColor(ImGuiCol.FrameBg, 0.2f, 0.205f, 0.21f, 1.0f);
        style.setColor(ImGuiCol.FrameBgHovered, 0.3f, 0.305f, 0.31f, 1.0f);
        style.setColor(ImGuiCol.FrameBgActive, 0.15f, 0.1505f, 0.151f, 1.0f);

        // Tabs
        style.setColor(ImGuiCol.Tab, 0.15f, 0.1505f, 0.151f, 1.0f);
        style.setColor(ImGuiCol.TabHovered, 0.38f, 0.3805f, 0.381f, 1.0f);
        style.setColor(ImGuiCol.TabActive,  0.28f, 0.2805f, 0.281f, 1.0f);
        style.setColor(ImGuiCol.TabUnfocused, 0.15f, 0.1505f, 0.151f, 1.0f);
        style.setColor(ImGuiCol.TabUnfocusedActive, 0.2f, 0.205f, 0.21f, 1.0f);

        // Title
        style.setColor(ImGuiCol.TitleBg, 0.15f, 0.1505f, 0.151f, 1.0f);
        style.setColor(ImGuiCol.TitleBgActive, 0.15f, 0.1505f, 0.151f, 1.0f);
        style.setColor(ImGuiCol.TitleBgCollapsed, 0.15f, 0.1505f, 0.151f, 1.0f);
    }
}
