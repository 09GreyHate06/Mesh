package editor;

import com.badlogic.ashley.core.Entity;
import editor.SceneHierarchyPanel;
import glCore.core.Application;
import glCore.core.Layer;
import glCore.core.Time;
import glCore.events.Event;
import glCore.events.EventType;
import glCore.events.applicationEvents.WindowResizeEvent;
import glCore.renderer.*;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.extension.imguizmo.flag.Mode;
import imgui.flag.*;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;
import editor.EditorCamera;
import org.w3c.dom.Text;
import scene.Scene;
import scene.components.TransformComponent;
import scene.components.renderering.*;
import scene.sytems.RenderingSystem;
import utils.AssetManager;
import utils.BasicMesh;
import utils.FileDialog;
import utils.ModelLoader;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class EditorLayer extends Layer {

    private Scene _scene;

    private EditorCamera _camera;
    private SceneHierarchyPanel _sceneHierarchyPanel;

    private Framebuffer _msFramebuffer;
    private Framebuffer _framebuffer;

    private boolean _viewportFocus = true;
    private float _sceneViewportWidth = 1280.0f;
    private float _sceneViewportHeight = 720.0f;


    // utils
    private VertexArray _cubeVAO;
    private VertexArray _planeVAO;

    // Menu bar about
    private final ImBoolean _openAbout = new ImBoolean(false);
    private final ImBoolean _openLibraryUsed = new ImBoolean(false);;
    private final ImBoolean _openCredits = new ImBoolean(false);
    private final ImBoolean _openRenderer = new ImBoolean(false);
    private final ImBoolean _openRendererSettings = new ImBoolean(false);

    private final ImBoolean _enableDepthTest = new ImBoolean(true);
    private final ImBoolean _enableFaceCull = new ImBoolean(true);
    private final String[] _faces = new String[]{ "Back", "Front" };
    private final ImInt _faceToCull = new ImInt(0);
    private final ImBoolean _enableBlendTest = new ImBoolean(true);

    public EditorLayer(String name) {
        super(name);
    }

    @Override
    public void onAttach() {

        GL33.glEnable(GL33.GL_DEPTH_TEST);
        GL33.glEnable(GL33.GL_CULL_FACE);
        GL33.glEnable(GL33.GL_BLEND);
        GL33.glBlendFunc(GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA);

        _camera = new EditorCamera();;
        _msFramebuffer = new Framebuffer(
                Application.get().getWindow().getWidth(),
                Application.get().getWindow().getHeight(),
                4, true);

        _framebuffer = new Framebuffer(
                Application.get().getWindow().getWidth(),
                Application.get().getWindow().getHeight(),
                1, false);

        _scene = new Scene();
        _scene.addSystem(new RenderingSystem(_camera, _msFramebuffer));
        _sceneHierarchyPanel = new SceneHierarchyPanel(_scene);

/*        ModelLoader.loadModel("D:\\3D_Models\\armor-set\\source\\armor 2021.obj", _scene);

        var vao = new VertexArray();
        VertexBufferLayout vBufLayout = new VertexBufferLayout();
        vBufLayout.pushElements(0, 3, false);
        vBufLayout.pushElements(1, 2, false);
        vBufLayout.pushElements(2, 3, false);
        vao.addVertexBuffer(new VertexBuffer(cubeVert, vBufLayout));
        vao.addIndexBuffer(new IndexBuffer(cubeIndices));

        Entity cubeA = _scene.createEntity("cubeA");
        cubeA.getComponent(TransformComponent.class).scale = new Vector3f(30.0f, 0.5f, 30.0f);
        cubeA.getComponent(TransformComponent.class).position.y = -1.0f;
        cubeA.addAndReturn(new MeshComponent()).vao = vao;
        cubeA.addAndReturn(new MeshRendererComponent());
        var cubeAMat = cubeA.addAndReturn(new MaterialComponent());
        cubeAMat.color = new Vector4f(1.0f);
        var texture = new Texture2D("D:/Textures/basketball_court_floor.jpg", true);
        cubeAMat.diffuseMap = texture;
        cubeAMat.specularMap = texture.addRef(Texture2D.class);
        cubeAMat.normalMap = null;
        cubeAMat.shininess = 128.0f;
        cubeAMat.tiling = new Vector2f(10.0f, 10.0f);

        var pointLightEntity = _scene.createEntity("pointLight");
        pointLightEntity.getComponent(TransformComponent.class).position = new Vector3f(0.0f, 8.0f, -4.0f);
        var pointLight = pointLightEntity.addAndReturn(new PointLightComponent());
        pointLight.color = new Vector3f(1.0f, 0.0f, 1.0f);

        var spotLightEntity = _scene.createEntity("spotLight");
        var plTrans = spotLightEntity.getComponent(TransformComponent.class);
        plTrans.position = new Vector3f(0.0f, 8.0f, 0.0f);
        plTrans.rotation = new Vector3f(-90.0f, 0.0f, 0.0f);
        spotLightEntity.addAndReturn(new SpotLightComponent()).color = new Vector3f(0.0f, 1.0f, 1.0f);*/

        VertexBufferLayout layout = new VertexBufferLayout();
        layout.pushElements(0, 3, false);
        layout.pushElements(1, 2, false);
        layout.pushElements(2, 3, false);
        layout.pushElements(3, 3, false);
        layout.pushElements(4, 3, false);

        _cubeVAO = new VertexArray();
        _cubeVAO.bind();
        var cubeVbo = new VertexBuffer(BasicMesh.createCubeVertices(-0.5f, 0.5f, 1.0f, 1.0f), layout);
        var cubeIbo = new IndexBuffer(BasicMesh.createCubeIndices());
        _cubeVAO.addVertexBuffer(cubeVbo);
        _cubeVAO.addIndexBuffer(cubeIbo);
        _cubeVAO.unbind();

        _planeVAO = new VertexArray();
        _planeVAO.bind();
        var planeVbo = new VertexBuffer(BasicMesh.createPlaneVertices(-0.5f, 0.5f, 1.0f, 1.0f), layout);
        var planeIbo = new IndexBuffer(BasicMesh.createPlaneIndices());
        _planeVAO.addVertexBuffer(planeVbo);
        _planeVAO.addIndexBuffer(planeIbo);
        _planeVAO.unbind();

        var dirLightEntity = _scene.createEntity("dirLight");
        dirLightEntity.getComponent(TransformComponent.class).rotation = new Vector3f(-50.0f, -30.0f, 0.0f);
        var dirLight = dirLightEntity.addAndReturn(new DirectionalLightComponent());
        dirLight.color = new Vector3f(1.0f, 1.0f, 1.0f);
    }

    @Override
    public void onDetach() {
        _msFramebuffer.release();
        _framebuffer.release();
        _scene.destroyEntities();
        _scene.removeSystems();
        _cubeVAO.release();
        _planeVAO.release();
        AssetManager.destroyAssets();
    }

    @Override
    public void onEvent(Event event) {
        if(event.getEventType() == EventType.WindowResize){
            var wr = (WindowResizeEvent)event;
            if(wr.getWidth() > 0 && wr.getHeight() > 0){
                Application.get().getWindow().setViewport(0, 0, wr.getWidth(), wr.getHeight());
            }
        }

        _camera.onEvent(event);
    }

    @Override
    public void onUpdate() {

        if(_viewportFocus)
            _camera.onUpdate();
        else
            _camera.reset();

        if(_sceneViewportWidth > 0.0f && _sceneViewportHeight > 0.0f &&
                (_msFramebuffer.getWidth() != _sceneViewportWidth ||
                 _msFramebuffer.getHeight() != _sceneViewportHeight)){

            _msFramebuffer.resize((int)_sceneViewportWidth, (int)_sceneViewportHeight);
            _framebuffer.resize((int)_sceneViewportWidth, (int)_sceneViewportHeight);
            _camera.setViewportSize(_sceneViewportWidth, _sceneViewportHeight);
        }

        _scene.update(Time.delta());
        _framebuffer.blit(_msFramebuffer, GL33.GL_COLOR_BUFFER_BIT, GL33.GL_NEAREST);
    }

    @Override
    public void onImGuiRender() {

        dockSpaceBegin();

        // Viewport
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
        ImGui.begin("Viewport");
        _viewportFocus = ImGui.isWindowFocused() && ImGui.isWindowHovered();
        Application.get().getImGuiLayer().blockEvents(!_viewportFocus);
        ImVec2 viewportPanelSize = ImGui.getContentRegionAvail();
        _sceneViewportWidth = viewportPanelSize.x;
        _sceneViewportHeight = viewportPanelSize.y;
        ImGui.image(_framebuffer.getColorAttachmentRendererID(),
                _sceneViewportWidth, _sceneViewportHeight, 0, 1, 1, 0);
        ImGui.end();
        ImGui.popStyleVar();

        if(ImGui.beginMenuBar()){

            if(ImGui.beginMenu("File")){
                if(ImGui.menuItem("Create Cube"))
                    createCubeEntity();

                if(ImGui.menuItem("Create Plane"))
                    createPlaneEntity();

                if(ImGui.menuItem("Load Model")){
                    String filter = "obj,fbx";
                    String filepath = FileDialog.showFileDialog("3D Model Files", filter);
                    if(!filepath.isEmpty())
                        ModelLoader.loadModel(filepath, _scene);
                }

                if(ImGui.menuItem("Exit"))
                    Application.get().close();

                ImGui.endMenu();
            }

            if(ImGui.beginMenu("Rendering")){
                if(ImGui.menuItem("Current Renderer"))
                    _openRenderer.set(true);
                if(ImGui.menuItem("Rendering Settings"))
                    _openRendererSettings.set(true);

                ImGui.endMenu();
            }

            if(ImGui.beginMenu("About")){
                if(ImGui.menuItem("Library Used"))
                    _openLibraryUsed.set(true);

                if(ImGui.menuItem("Credits"))
                    _openCredits.set(true);

                if(ImGui.menuItem("About"))
                    _openAbout.set(true);

                ImGui.endMenu();
            }

            ImGui.endMenuBar();
        }

        _sceneHierarchyPanel.onImGuiRender();

        if(_openLibraryUsed.get()){
            ImGui.begin("Library Used", _openLibraryUsed);
            showLibraryUsed();
            ImGui.end();
        }

        if(_openCredits.get()){
            ImGui.begin("Credits", _openCredits);
            showCredits();
            ImGui.end();
        }

        if(_openAbout.get()){
            ImGui.begin("About", _openAbout);
            showAbout();
            ImGui.end();
        }

        if(_openRenderer.get()){
            ImGui.begin("Renderer", _openRenderer);
            showRenderer();
            ImGui.end();
        }

        if(_openRendererSettings.get()){
            ImGui.begin("Renderer Settings", _openRendererSettings);
            showRendererSettings();
            ImGui.end();
        }

        ImGui.begin("FPS");
        ImGui.text(Float.toString(ImGui.getIO().getFramerate()));
        ImGui.end();
        dockSpaceEnd();
    }

    private void dockSpaceBegin(){
        int dockWindowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoTitleBar
                | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove
                | ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;
        int dockNodeFlags = ImGuiDockNodeFlags.None;

        final ImGuiViewport viewport = ImGui.getMainViewport();
        ImGui.setNextWindowPos(viewport.getWorkPos().x, viewport.getWorkPos().y);
        ImGui.setNextWindowSize(viewport.getWorkSize().x, viewport.getWorkSize().y);
        ImGui.setNextWindowViewport(viewport.getID());
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);

        // proceed even if Dock is collapse because we want to keep our dockspace active
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
        ImGui.begin("DockSpace", dockWindowFlags);
        ImGui.popStyleVar();
        ImGui.popStyleVar(2);

        ImGuiIO io = ImGui.getIO();
        if((io.getConfigFlags() & ImGuiConfigFlags.DockingEnable) == 0){
            throw new IllegalStateException("Docking is not enabled!");
        }

        // Submit the DockSpace
        int dockSpaceID = ImGui.getID("WesDockSpace");
        ImGui.dockSpace(dockSpaceID, 0.0f, 0.0f, dockNodeFlags);
    }

    private void dockSpaceEnd(){
        ImGui.end();
    }

    private Entity createCubeEntity(){
        Entity entity = _scene.createEntity("Cube");
        var mc = entity.addAndReturn(new MeshComponent());
        mc.vao = _cubeVAO.addRef(VertexArray.class);
        var mat = entity.addAndReturn(new MaterialComponent());
        mat.diffuseMap = null;
        mat.specularMap = null;
        mat.normalMap = null;
        mat.color = new Vector4f(1.0f);
        mat.tiling = new Vector2f(1.0f);
        mat.shininess = 32.0f;
        var mr = entity.addAndReturn(new MeshRendererComponent());
        mr.primitive = Primitive.Triangles;
        mr.receiveLight = true;

        return entity;
    }

    private Entity createPlaneEntity(){
        Entity entity = _scene.createEntity("Plane");
        var mc = entity.addAndReturn(new MeshComponent());
        mc.vao = _planeVAO.addRef(VertexArray.class);
        var mat = entity.addAndReturn(new MaterialComponent());
        mat.diffuseMap = null;
        mat.specularMap = null;
        mat.normalMap = null;
        mat.color = new Vector4f(1.0f);
        mat.tiling = new Vector2f(1.0f);
        mat.shininess = 32.0f;
        var mr = entity.addAndReturn(new MeshRendererComponent());
        mr.primitive = Primitive.Triangles;
        mr.receiveLight = true;

        return entity;
    }

    private void showLibraryUsed(){
        ImGui.pushItemWidth(-1.0f);
        int flag = ImGuiInputTextFlags.ReadOnly;
        ImGui.text("LWJGL");
        ImGui.text("LWJGL-assimp");
        ImGui.text("LWJGL-glfw");
        ImGui.text("LWJGL-opengl");
        ImGui.text("LWJGL-stb");
        ImGui.text("LWJGL-nfd");
        ImGui.inputText("##git1", new ImString("https://github.com/LWJGL/lwjgl3.git"), flag);

        ImGui.spacing();
        ImGui.spacing();

        ImGui.text("Ashley");
        ImGui.inputText("##git2", new ImString("https://github.com/libgdx/ashley.git"), flag);

        ImGui.spacing();
        ImGui.spacing();

        ImGui.text("ImGui");
        ImGui.inputText("##git3", new ImString("https://github.com/ocornut/imgui.git"), flag);

        ImGui.spacing();
        ImGui.spacing();

        ImGui.text("JOML");
        ImGui.inputText("##git4", new ImString("https://github.com/JOML-CI/JOML.git"), flag);
        ImGui.popItemWidth();
    }

    private void showCredits(){
        ImGui.pushItemWidth(-1.0f);
        ImGui.text("Wesley Karl Andres");
        ImGui.text("Arby Borcena");
        ImGui.text("Jabneel Ramisan");
        ImGui.text("Dominick Sagrado");
        ImGui.text("Gironnie Arisgado");
        ImGui.popItemWidth();
    }

    private void showAbout(){
        ImGui.pushItemWidth(-1.0f);
        ImGui.spacing();
        ImGui.text("Description");
        ImGui.spacing();
        ImGui.text("Mesh application is a 3D model viewer that can load a 3D model \nand manipulate its component at runtime."
        		+ "\n\n Date Created: 4/15/22");
        ImGui.popItemWidth();
    }

    private void showRenderer(){
        ImGui.pushItemWidth(-1.0f);
        ImGui.text("OpenGL");
        ImGui.text("Vendor: " + GL33.glGetString(GL11.GL_VENDOR));
        ImGui.text("Renderer: " + GL33.glGetString(GL11.GL_RENDERER));
        ImGui.text("Version: " + GL33.glGetString(GL11.GL_VERSION));
        ImGui.popItemWidth();
    }

    private void showRendererSettings(){
        ImGui.checkbox("Enable Depth Test", _enableDepthTest);
        ImGui.checkbox("Enable Blend Test", _enableBlendTest);
        ImGui.checkbox("Enable Face Culling", _enableFaceCull);

        if(_enableDepthTest.get()){
            GL33.glEnable(GL33.GL_DEPTH_TEST);
        } else{
            GL33.glDisable(GL33.GL_DEPTH_TEST);
        }
        if(_enableBlendTest.get()){
            GL33.glEnable(GL33.GL_BLEND);
            GL33.glBlendFunc(GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA);
        } else {
            GL33.glDisable(GL33.GL_BLEND);
        }
        if(_enableFaceCull.get()){
            GL33.glEnable(GL33.GL_CULL_FACE);
            ImGui.pushItemWidth(70.0f);
            ImGui.combo("Face to cull", _faceToCull, _faces);
            ImGui.popItemWidth();
            if(_faceToCull.get() == 0)
                GL33.glCullFace(GL33.GL_BACK);
            else if(_faceToCull.get() == 1)
                GL33.glCullFace(GL33.GL_FRONT);
        } else {
            GL33.glDisable(GL33.GL_CULL_FACE);
        }
    }
}
