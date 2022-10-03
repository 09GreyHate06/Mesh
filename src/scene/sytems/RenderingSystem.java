package scene.sytems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import glCore.core.Application;
import glCore.renderer.Framebuffer;
import glCore.renderer.Texture2D;
import glCore.renderer.UniformBuffer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;
import editor.EditorCamera;
import scene.components.RelationshipComponent;
import scene.components.TransformComponent;
import scene.components.renderering.*;
import utils.AssetManager;

import java.nio.ByteBuffer;
import java.util.Stack;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;

public class RenderingSystem extends EntitySystem {
    private ImmutableArray<Entity> _dirLights;
    private ImmutableArray<Entity> _pointLights;
    private ImmutableArray<Entity> _spotLights;
    private ImmutableArray<Entity> _renderables;

    private UniformBuffer _cameraUbo;

    private final Framebuffer _framebuffer;
    private final EditorCamera _camera;

    private Texture2D _defaultTexture;

    public RenderingSystem(EditorCamera camera, Framebuffer framebuffer){
        super(1000);
        _camera = camera;
        _framebuffer = framebuffer;
    }

    @Override
    public void addedToEngine(Engine engine) {
        _dirLights = engine.getEntitiesFor(Family.all(
                TransformComponent.class,
                RelationshipComponent.class,
                DirectionalLightComponent.class
        ).get());

        _pointLights = engine.getEntitiesFor(Family.all(
                TransformComponent.class,
                RelationshipComponent.class,
                PointLightComponent.class
        ).get());

        _spotLights = engine.getEntitiesFor(Family.all(
                TransformComponent.class,
                RelationshipComponent.class,
                SpotLightComponent.class
        ).get());

        _renderables = engine.getEntitiesFor(Family.all(
                TransformComponent.class,
                RelationshipComponent.class,
                MeshComponent.class,
                MeshRendererComponent.class,
                MaterialComponent.class
        ).get());

        ByteBuffer data = ByteBuffer.allocateDirect(4);
        data.put((byte)(0xff));
        data.put((byte)(0xff));
        data.put((byte)(0xff));
        data.put((byte)(0xff));
        data.flip();
        _defaultTexture = new Texture2D(data, 1, 1, 4,
                GL33.GL_LINEAR_MIPMAP_LINEAR, GL33.GL_LINEAR, GL33.GL_REPEAT, GL33.GL_REPEAT);
        AssetManager.loadAndAddShader("texture", "assets/shaders/texture.shader")
                .setUniformBlockBinding("Camera", 0);
        AssetManager.loadAndAddShader("light", "assets/shaders/light.shader")
                .setUniformBlockBinding("Camera", 0);
        _cameraUbo = new UniformBuffer(0, 16 * 3 * 4, 0);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        AssetManager.removeShader("texture");
        _cameraUbo.release();
        _defaultTexture.release();
    }

    @Override
    public void update(float deltaTime) {
        _framebuffer.bind();
        Application.get().getWindow().setViewport(0, 0, (int)_framebuffer.getWidth(), (int)_framebuffer.getHeight());

        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        setLights();
        render();

        _framebuffer.unbind();
    }

    private void setLights(){
        var lightShader = AssetManager.getShader("light");
        lightShader.bind();

        for(int i = 0; i < _dirLights.size(); i++){
            var entity = _dirLights.get(i);
            var transform = entity.getComponent(TransformComponent.class);
            var relationship = entity.getComponent(RelationshipComponent.class);
            var light = entity.getComponent(DirectionalLightComponent.class);

            Matrix4f transMatrix = getEntityParentsTransform(relationship).mul(transform.getTransformMatrix());
            lightShader.setUniformVec3("u_dirLights[" + i + "].direction", new Vector3f(0.0f, 0.0f, -1.0f).mulDirection(transMatrix));
            lightShader.setUniformVec3("u_dirLights[" + i + "].ambient", light.color.mul(light.ambientIntensity, new Vector3f()));
            lightShader.setUniformVec3("u_dirLights[" + i + "].diffuse", light.color.mul(light.diffuseIntensity, new Vector3f()));
            lightShader.setUniformVec3("u_dirLights[" + i + "].specular", light.color.mul(light.specularIntensity, new Vector3f()));
        }
        lightShader.setUniformInt("u_activeDirectionalLights", _dirLights.size());


        for(int i = 0; i < _pointLights.size(); i++){
            var entity = _pointLights.get(i);
            var transform = entity.getComponent(TransformComponent.class);
            var relationship = entity.getComponent(RelationshipComponent.class);
            var light = entity.getComponent(PointLightComponent.class);

            Matrix4f transMatrix = getEntityParentsTransform(relationship).mul(transform.getTransformMatrix());
            lightShader.setUniformVec3("u_pointLights[" + i + "].position", new Vector3f().mulPosition(transMatrix));
            lightShader.setUniformVec3("u_pointLights[" + i + "].ambient", light.color.mul(light.ambientIntensity, new Vector3f()));
            lightShader.setUniformVec3("u_pointLights[" + i + "].diffuse", light.color.mul(light.diffuseIntensity, new Vector3f()));
            lightShader.setUniformVec3("u_pointLights[" + i + "].specular", light.color.mul(light.specularIntensity, new Vector3f()));
            lightShader.setUniformFloat("u_pointLights[" + i + "].constant", light.constant);
            lightShader.setUniformFloat("u_pointLights[" + i + "].linear", light.linear);
            lightShader.setUniformFloat("u_pointLights[" + i + "].quadratic", light.quadratic);
        }
        lightShader.setUniformInt("u_activePointLights", _pointLights.size());


        for(int i = 0; i < _spotLights.size(); i++){
            var entity = _spotLights.get(i);
            var transform = entity.getComponent(TransformComponent.class);
            var relationship = entity.getComponent(RelationshipComponent.class);
            var light = entity.getComponent(SpotLightComponent.class);

            Matrix4f transMatrix = getEntityParentsTransform(relationship).mul(transform.getTransformMatrix());
            lightShader.setUniformVec3("u_spotLights[" + i + "].direction", new Vector3f(0.0f, 0.0f, -1.0f).mulDirection(transMatrix));
            lightShader.setUniformVec3("u_spotLights[" + i + "].position", new Vector3f().mulPosition(transMatrix));
            lightShader.setUniformVec3("u_spotLights[" + i + "].ambient", light.color.mul(light.ambientIntensity, new Vector3f()));
            lightShader.setUniformVec3("u_spotLights[" + i + "].diffuse", light.color.mul(light.diffuseIntensity, new Vector3f()));
            lightShader.setUniformVec3("u_spotLights[" + i + "].specular", light.color.mul(light.specularIntensity, new Vector3f()));
            lightShader.setUniformFloat("u_spotLights[" + i + "].constant", light.constant);
            lightShader.setUniformFloat("u_spotLights[" + i + "].linear", light.linear);
            lightShader.setUniformFloat("u_spotLights[" + i + "].quadratic", light.quadratic);
            lightShader.setUniformFloat("u_spotLights[" + i + "].innerCutOff", (float)Math.cos(Math.toRadians(light.innerCutOffAngle)));
            lightShader.setUniformFloat("u_spotLights[" + i + "].outerCutOff", (float)Math.cos(Math.toRadians(light.outerCutOffAngle)));
        }
        lightShader.setUniformInt("u_activeSpotLights", _spotLights.size());

        lightShader.unbind();
    }

    private void render(){
        try(MemoryStack stack = MemoryStack.stackPush()){
            ByteBuffer v = stack.malloc(16 * 4);
            ByteBuffer p = stack.malloc(16 * 4);
            ByteBuffer pos = stack.malloc(3 * 4);
            _cameraUbo.setData(0, _camera.getViewMatrix().get(v));
            _cameraUbo.setData(16 * 4, _camera.getProjectionMatrix().get(p));
            _cameraUbo.setData(16 * 2 * 4, _camera.getPosition().get(pos));
        }

        for(var entity : _renderables){
            var transform = entity.getComponent(TransformComponent.class);
            var relationship = entity.getComponent(RelationshipComponent.class);
            var mesh = entity.getComponent(MeshComponent.class);
            var meshRenderer = entity.getComponent(MeshRendererComponent.class);
            var material = entity.getComponent(MaterialComponent.class);

            mesh.vao.bind();

            if(meshRenderer.receiveLight){
                boolean enableNormalMap = false;

                var shader = AssetManager.getShader("light");
                shader.bind();

                if(material.diffuseMap != null)
                    material.diffuseMap.bind(0);
                else
                    _defaultTexture.bind(0);

                if(material.specularMap != null)
                    material.specularMap.bind(1);
                else
                    _defaultTexture.bind(1);

                if(material.normalMap != null){
                    material.normalMap.bind(2);
                    enableNormalMap = true;
                } else {
                    _defaultTexture.bind(2);
                }

                var transMat = getEntityParentsTransform(relationship).mul(transform.getTransformMatrix());
                shader.setUniformMat4f("u_transform", transMat);
                shader.setUniformMat3f("u_normalMatrix", transMat.invert(new Matrix4f()).transpose(new Matrix4f()).get3x3(new Matrix3f()));
                shader.setUniformVec4("u_material.color", material.color);
                shader.setUniformVec2("u_material.tiling", material.tiling);
                shader.setUniformFloat("u_material.shininess", material.shininess);
                shader.setUniformBool("u_material.enableNormalMap", enableNormalMap);
                shader.setUniformInt("u_material.diffuseMap", 0);
                shader.setUniformInt("u_material.specularMap", 1);
                shader.setUniformInt("u_material.normalMap", 2);

            } else {
                var shader = AssetManager.getShader("texture");
                shader.bind();

                if(material.diffuseMap != null)
                    material.diffuseMap.bind(0);
                else
                    _defaultTexture.bind(0);

                shader.setUniformInt("u_texture", 0);
                shader.setUniformMat4f("u_transform", getEntityParentsTransform(relationship).mul(transform.getTransformMatrix()));
                shader.setUniformVec2("u_tiling", material.tiling);
                shader.setUniformVec4("u_color", material.color);
            }

            GL33.glDrawElements(meshRenderer.primitive.nativeValue, mesh.vao.getIndexBuffer().getCount(), GL33.GL_UNSIGNED_INT, 0);
            mesh.vao.unbind();
        }
    }

    private Matrix4f getEntityParentsTransform(RelationshipComponent relationship){
        Matrix4f result = new Matrix4f().identity();

        Entity current = null;
        if((current = relationship.parent) != null){
            Stack<Matrix4f> parentsTransform = new Stack<>();
            while(current != null){
                parentsTransform.push(current.getComponent(TransformComponent.class).getTransformMatrix());
                current = current.getComponent(RelationshipComponent.class).parent;
            }

            while (!parentsTransform.empty()){
                result = parentsTransform.pop().mul(result);
            }
        }

        return result;
    }
}
