package editor;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import glCore.renderer.Texture2D;
import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.ImVec2;
import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.extension.imguifiledialog.flag.ImGuiFileDialogFlags;
import imgui.flag.ImGuiPopupFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.lwjgl.opengl.GL33;
import scene.Scene;
import scene.components.RelationshipComponent;
import scene.components.TagComponent;
import scene.components.TransformComponent;
import scene.components.renderering.*;
import utils.AssetManager;
import utils.FileDialog;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.Stack;

@FunctionalInterface
interface Func<T extends Component>{
    void invoke(T comp);
}

public class SceneHierarchyPanel {

    private Scene _context;
    private Entity _selectedEntity;
    private final Stack<Entity> _entitiesToDestroy;

    private static final float _dragSpeed = 0.1f;
    private static final String _entityDragDropID = "SceneHierarchyEntity";

    public SceneHierarchyPanel(Scene context){
        _context = context;
        _selectedEntity = null;
        _entitiesToDestroy = new Stack<>();
    }

    public void setContext(Scene context){
        _context = context;
        _selectedEntity = null;
    }

    public void onImGuiRender(){
        while (!_entitiesToDestroy.isEmpty()){
            if(_selectedEntity == _entitiesToDestroy.peek())
                _selectedEntity = null;

            _context.destroyEntity(_entitiesToDestroy.pop());
        }

        if(ImGui.begin("Scene Hierarchy")){
            for(var entity : _context.getEntities()){
                ImGui.beginChild("abc");
                drawEntityNode(entity, false);
                ImGui.endChild();

                if(ImGui.beginDragDropTarget()){
                    Entity payload = ImGui.acceptDragDropPayload(_entityDragDropID);
                    if(payload != null){
                        _context.setEntityRelationship(payload, null);
                    }
                    ImGui.endDragDropTarget();
                }
            }

            if (ImGui.isMouseDown(0) && ImGui.isWindowHovered())
                _selectedEntity = null;

            ImGui.beginChild("abc");
            if(ImGui.beginPopupContextWindow("0", ImGuiPopupFlags.NoOpenOverItems | ImGuiPopupFlags.MouseButtonRight)){
                if(ImGui.menuItem("Create Empty Entity"))
                    _context.createEntity("Empty Entity");
                ImGui.endPopup();
            }

            ImGui.endChild();

            ImGui.end();
        }

        ImGui.begin("Inspector");
        if(_selectedEntity != null)
            drawComponents(_selectedEntity);
        ImGui.end();
    }

    public void setSelectedEntity(Entity entity){
        _selectedEntity = entity;
    }

    private void drawEntityNode(Entity entity, boolean child){
        var relationship = entity.getComponent(RelationshipComponent.class);
        if(relationship.parent != null && !child) return;

        var tag = entity.getComponent(TagComponent.class).tag;

        int flags =  ((_selectedEntity == entity) ? ImGuiTreeNodeFlags.Selected : 0)
                | (relationship.first != null ? 0 : ImGuiTreeNodeFlags.Leaf) | ImGuiTreeNodeFlags.OpenOnArrow;

        flags |= ImGuiTreeNodeFlags.SpanAvailWidth;
        boolean opened = ImGui.treeNodeEx(entity.hashCode(), flags, tag);

        if(ImGui.isItemClicked())
            _selectedEntity = entity;

        if(ImGui.beginDragDropSource()){
            ImGui.setDragDropPayload(_entityDragDropID, entity);
            ImGui.text(tag);
            ImGui.endDragDropSource();
        }

        if(ImGui.beginDragDropTarget()){
            Entity draggedEntity = ImGui.acceptDragDropPayload(_entityDragDropID);
            if(draggedEntity != null)
                _context.setEntityRelationship(draggedEntity, entity);
            ImGui.endDragDropTarget();
        }

        if(ImGui.beginPopupContextItem()){
            if(ImGui.menuItem("Create Empty Entity"))
                _context.createEntity("Empty Entity", entity);
            if(ImGui.menuItem("Destroy Entity"))
                _entitiesToDestroy.push(entity);

            ImGui.endPopup();
        }

        if(opened){
            Entity current = relationship.first;
            while(current != null){
                drawEntityNode(current, true);
                if(current == null) break;
                current = current.getComponent(RelationshipComponent.class).next;
            }
            ImGui.treePop();
        }
    }

    private void drawComponents(Entity entity){
        TagComponent tagComponent = null;
        if((tagComponent = entity.getComponent(TagComponent.class)) != null){
            ImString buffer = new ImString(512);
            buffer.set(tagComponent.tag);
            ImGui.inputText("##Tag", buffer);
            tagComponent.tag = buffer.get();
        }

        ImGui.sameLine();
        ImGui.pushItemWidth(-1.0f);

        if(ImGui.button("Add Component"))
            ImGui.openPopup("AddComponent");

        if(ImGui.beginPopup("AddComponent")){
            if(ImGui.menuItem("Mesh")){
                if(entity.getComponent(MeshComponent.class) == null)
                    entity.add(new MeshComponent());

                ImGui.closeCurrentPopup();
            }

            if(ImGui.menuItem("Mesh Renderer")){
                if(entity.getComponent(MeshRendererComponent.class) == null)
                    entity.add(new MeshRendererComponent());

                ImGui.closeCurrentPopup();
            }

            if(ImGui.menuItem("Material")){
                if(entity.getComponent(MaterialComponent.class) == null)
                    entity.add(new MaterialComponent());

                ImGui.closeCurrentPopup();
            }

            if(ImGui.menuItem("Directional Light")){
                if(entity.getComponent(DirectionalLightComponent.class) == null)
                    entity.add(new DirectionalLightComponent());

                ImGui.closeCurrentPopup();
            }

            if(ImGui.menuItem("Point Light")){
                if(entity.getComponent(PointLightComponent.class) == null)
                    entity.add(new PointLightComponent());

                ImGui.closeCurrentPopup();
            }

            if(ImGui.menuItem("Spot Light ")){
                if(entity.getComponent(SpotLightComponent.class) == null)
                    entity.add(new SpotLightComponent());

                ImGui.closeCurrentPopup();
            }

            ImGui.endPopup();
        }

        ImGui.popItemWidth();

        drawComponent("Transform", entity, TransformComponent.class, (component) -> {
            float[] posBuffer = new float[]{ component.position.x, component.position.y, component.position.z };
            float[] rotBuffer = new float[]{ component.rotation.x, component.rotation.y, component.rotation.z };
            float[] scaBuffer = new float[]{ component.scale.x, component.scale.y, component.scale.z };

            ImGui.dragFloat3("Position", posBuffer, _dragSpeed);
            ImGui.dragFloat3("Rotation", rotBuffer, _dragSpeed);
            ImGui.dragFloat3("Scale", scaBuffer, _dragSpeed);

            component.position.set(posBuffer);
            component.rotation.set(rotBuffer);
            component.scale.set(scaBuffer);
        });

        drawComponent("Mesh", entity, MeshComponent.class, (component) -> {

        });

        drawComponent("Mesh Renderer", entity, MeshRendererComponent.class, (component) -> {
            String[] typeStr = new String[]{ "Points", "Lines", "Line Strip", "Line Loop", "Triangles", "Triangle Strip",
                                             "Triangle Fan", "Quads", "Quad Strip" };
            ImInt index = new ImInt();
            switch (component.primitive){
                case Points: index.set(0); break;
                case Lines: index.set(1); break;
                case LineStrip: index.set(2); break;
                case LineLoop: index.set(3); break;
                case Triangles: index.set(4); break;
                case TriangleStrip: index.set(5); break;
                case TriangleFan: index.set(6); break;
                case Quads: index.set(7); break;
                case QuadStrip: index.set(8); break;
            }

            if(ImGui.combo("Primitive", index, typeStr)){
                switch (index.get()){
                    case 0: component.primitive = Primitive.Points; break;
                    case 1: component.primitive = Primitive.Lines; break;
                    case 2: component.primitive = Primitive.LineStrip; break;
                    case 3: component.primitive = Primitive.LineLoop; break;
                    case 4: component.primitive = Primitive.Triangles; break;
                    case 5: component.primitive = Primitive.TriangleStrip; break;
                    case 6: component.primitive = Primitive.TriangleFan; break;
                    case 7: component.primitive = Primitive.Quads; break;
                    case 8: component.primitive = Primitive.QuadStrip; break;
                }
            }

            ImBoolean receiveLight = new ImBoolean(component.receiveLight);
            ImGui.checkbox("Receive Light", receiveLight);
            component.receiveLight = receiveLight.get();
        });

        drawComponent("Material", entity, MaterialComponent.class, (component) -> {
            String filter = "png,jpg,jpeg,tga";
            String description = "Image Files";
            int id = 0;

            ImGui.text("Diffuse Map");
            ImGui.spacing();
            ImGui.pushItemWidth(-1.0f);
            if(component.diffuseMap != null)
                ImGui.image(component.diffuseMap.getRendererID(), 100.0f, 100.0f);
            ImGui.text((component.diffuseMap != null ? component.diffuseMap.getFilepath() : "N/A"));
            ImGui.popItemWidth();

            ImGui.pushID(id++);
            if(ImGui.button("Browse")){
                String filepath = FileDialog.showFileDialog(description, filter);
                if(!filepath.isEmpty()){
                    if(component.diffuseMap != null)
                        component.diffuseMap.release();
                    component.diffuseMap = new Texture2D(filepath, true);
                }
            }
            ImGui.popID();

            ImGui.pushID(id++);
            ImGui.sameLine();
            ImGui.pushItemWidth(-1.0f);
            if(ImGui.button("Remove")){
                if(component.diffuseMap != null)
                    component.diffuseMap.release();
                component.diffuseMap = null;
            }
            ImGui.popItemWidth();
            ImGui.popID();

            ImGui.separator();

            ImGui.text("Specular Map");
            ImGui.spacing();
            ImGui.pushItemWidth(-1.0f);
            if(component.specularMap != null)
                ImGui.image(component.specularMap.getRendererID(), 100.0f, 100.0f);
            ImGui.text(component.specularMap != null ? component.specularMap.getFilepath() : "N/A");
            ImGui.popItemWidth();

            ImGui.pushID(id++);
            if(ImGui.button("Browse")){
                String filepath = FileDialog.showFileDialog(description, filter);
                if(!filepath.isEmpty()){
                    if(component.specularMap != null)
                        component.specularMap.release();
                    component.specularMap = new Texture2D(filepath, true);
                }
            }
            ImGui.popID();

            ImGui.pushID(id++);
            ImGui.sameLine();
            ImGui.pushItemWidth(-1.0f);
            if(ImGui.button("Remove")){
                if(component.specularMap != null)
                    component.specularMap.release();
                component.specularMap = null;
            }
            ImGui.popItemWidth();
            ImGui.popID();

            ImGui.separator();

            ImGui.text("Normal Map");
            ImGui.spacing();
            ImGui.pushItemWidth(-1.0f);
            if(component.normalMap != null)
                ImGui.image(component.normalMap.getRendererID(), 100.0f, 100.0f);
            ImGui.text(component.normalMap != null ? component.normalMap.getFilepath() : "N/A");
            ImGui.popItemWidth();

            ImGui.pushID(id++);
            if(ImGui.button("Browse")){
                String filepath = FileDialog.showFileDialog(description, filter);
                if(!filepath.isEmpty()){
                    if(component.normalMap != null)
                        component.normalMap.release();
                    component.normalMap = new Texture2D(filepath, true);
                }
            }
            ImGui.popID();

            ImGui.pushID(id);
            ImGui.sameLine();
            ImGui.pushItemWidth(-1.0f);
            if(ImGui.button("Remove")){
                if(component.normalMap != null)
                    component.normalMap.release();
                component.normalMap = null;
            }
            ImGui.popItemWidth();
            ImGui.popID();

            ImGui.separator();

            float[] colorBuffer = new float[]{ component.color.x, component.color.y, component.color.z, component.color.w };
            float[] tilingBuffer = new float[]{ component.tiling.x, component.tiling.y };
            float[] shininessBuffer = new float[]{ component.shininess };
            ImGui.colorEdit4("Color", colorBuffer);
            ImGui.dragFloat2("Tiling", tilingBuffer, _dragSpeed);
            ImGui.dragFloat("Shininess", shininessBuffer, _dragSpeed);
            component.color.set(colorBuffer);
            component.tiling.set(tilingBuffer);
            component.shininess = shininessBuffer[0];
        });

        drawComponent("Directional Light", entity, DirectionalLightComponent.class, (component) -> {
            float[] colorBuffer = new float[] { component.color.x, component.color.y, component.color.z };
            float[] aIBuffer = new float[]{ component.ambientIntensity };
            float[] dIBuffer = new float[]{ component.diffuseIntensity };
            float[] sIBuffer = new float[]{ component.specularIntensity };

            ImGui.colorEdit3("Color", colorBuffer);
            ImGui.dragFloat("Ambient intensity", aIBuffer, _dragSpeed);
            ImGui.dragFloat("Diffuse intensity", dIBuffer, _dragSpeed);
            ImGui.dragFloat("Specular intensity", sIBuffer, _dragSpeed);

            component.color.set(colorBuffer);
            component.ambientIntensity = aIBuffer[0];
            component.diffuseIntensity = dIBuffer[0];
            component.specularIntensity = sIBuffer[0];
        });

        drawComponent("Point Light", entity, PointLightComponent.class, (component) -> {
            float[] colorBuffer = new float[] { component.color.x, component.color.y, component.color.z };
            float[] aIBuffer = new float[]{ component.ambientIntensity };
            float[] dIBuffer = new float[]{ component.diffuseIntensity };
            float[] sIBuffer = new float[]{ component.specularIntensity };
            float[] constBuffer = new float[]{ component.constant };
            float[] linearBuffer = new float[]{ component.linear };
            float[] quadraticBuffer = new float[]{ component.quadratic };

            ImGui.colorEdit3("Color", colorBuffer);
            ImGui.dragFloat("Ambient intensity", aIBuffer, _dragSpeed);
            ImGui.dragFloat("Diffuse intensity", dIBuffer, _dragSpeed);
            ImGui.dragFloat("Specular intensity", sIBuffer, _dragSpeed);
            ImGui.dragFloat("Constant", constBuffer, _dragSpeed);
            ImGui.dragFloat("Linear", linearBuffer, _dragSpeed);
            ImGui.dragFloat("Quadratic", quadraticBuffer, _dragSpeed);

            component.color.set(colorBuffer);
            component.ambientIntensity = aIBuffer[0];
            component.diffuseIntensity = dIBuffer[0];
            component.specularIntensity = sIBuffer[0];
            component.constant = colorBuffer[0];
            component.linear = linearBuffer[0];
            component.quadratic = quadraticBuffer[0];
        });

        drawComponent("Spot Light", entity, SpotLightComponent.class, (component) -> {
            float[] colorBuffer = new float[] { component.color.x, component.color.y, component.color.z };
            float[] aIBuffer = new float[]{ component.ambientIntensity };
            float[] dIBuffer = new float[]{ component.diffuseIntensity };
            float[] sIBuffer = new float[]{ component.specularIntensity };
            float[] constBuffer = new float[]{ component.constant };
            float[] linearBuffer = new float[]{ component.linear };
            float[] quadraticBuffer = new float[]{ component.quadratic };
            float[] innerCutBuffer = new float[]{ component.innerCutOffAngle };
            float[] outerCutBuffer = new float[]{ component.outerCutOffAngle };

            ImGui.colorEdit3("Color", colorBuffer);
            ImGui.dragFloat("Ambient intensity", aIBuffer, _dragSpeed);
            ImGui.dragFloat("Diffuse intensity", dIBuffer, _dragSpeed);
            ImGui.dragFloat("Specular intensity", sIBuffer, _dragSpeed);
            ImGui.dragFloat("Constant", constBuffer, _dragSpeed);
            ImGui.dragFloat("Linear", linearBuffer, _dragSpeed);
            ImGui.dragFloat("Quadratic", quadraticBuffer, _dragSpeed);
            ImGui.dragFloat("Inner cutoff angle", innerCutBuffer, _dragSpeed);
            ImGui.dragFloat("Outer cutoff angle", outerCutBuffer, _dragSpeed);

            component.color.set(colorBuffer);
            component.ambientIntensity = aIBuffer[0];
            component.diffuseIntensity = dIBuffer[0];
            component.specularIntensity = sIBuffer[0];
            component.constant = colorBuffer[0];
            component.linear = linearBuffer[0];
            component.quadratic = quadraticBuffer[0];
            component.innerCutOffAngle = innerCutBuffer[0];
            component.outerCutOffAngle = outerCutBuffer[0];
        });
    }

    private <T extends Component> void drawComponent(String name, Entity entity, Class<T> comp, Func<T> func){
        final int treeNodeFlags = ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.Framed | ImGuiTreeNodeFlags.SpanAvailWidth
                | ImGuiTreeNodeFlags.AllowItemOverlap | ImGuiTreeNodeFlags.FramePadding;

        ImVec2 contentRegionAvailable = ImGui.getContentRegionAvail();
        T component = null;
        if((component = entity.getComponent(comp)) != null){
            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 4.0f, 4.0f);
            float lineHeight = ImGui.getFont().getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
            ImGui.separator();
            boolean open = ImGui.treeNodeEx(comp.hashCode(), treeNodeFlags, name);
            ImGui.popStyleVar();
            ImGui.sameLine(contentRegionAvailable.x - lineHeight * 0.5f);
            if(ImGui.button("+", lineHeight, lineHeight)){
                ImGui.openPopup("ComponentSettings");
            }

            boolean removeComponent = false;
            if(ImGui.beginPopup("ComponentSettings")){
                if(ImGui.menuItem("Remove component"))
                    removeComponent = true;
                ImGui.endPopup();
            }

            if(open){
                func.invoke(component);
                ImGui.treePop();
            }

            if(removeComponent)
                entity.remove(comp);
        }
    }
}
