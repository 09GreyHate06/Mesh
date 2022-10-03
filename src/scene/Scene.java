package scene;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import glCore.renderer.VertexArray;
import scene.components.RelationshipComponent;
import scene.components.TagComponent;
import scene.components.TransformComponent;
import scene.components.renderering.MaterialComponent;
import scene.components.renderering.MeshComponent;

import java.util.Collection;

public class Scene {

    PooledEngine _engine;

    public Scene(){
        _engine = new PooledEngine();
        _engine.addEntityListener(new Listener());
    }

    public Entity createEntity(String tag, Entity parent){
        Entity entity =  _engine.createEntity();
        entity.addAndReturn(new TagComponent()).tag = tag;
        entity.add(new TransformComponent());
        entity.add(new RelationshipComponent());

        setEntityRelationship(entity, parent);

        _engine.addEntity(entity);
        return entity;
    }

    public Entity createEntity(String name){
        return createEntity(name, null);
    }

    public Entity createEntity(){
        return createEntity("Entity", null);
    }

    public void destroyEntity(Entity entity){
        setEntityRelationship(entity, null);

        destroyEntityChildren(entity);
        _engine.removeEntity(entity);
    }

    public void destroyEntities(){
        _engine.removeAllEntities();
    }

    public void addSystem(EntitySystem system){
        _engine.addSystem(system);
    }

    public void removeSystem(EntitySystem system){
        _engine.removeSystem(system);
    }

    public void removeSystems(){
        _engine.removeAllSystems();
    }

    public ImmutableArray<Entity> getEntities(){
        return _engine.getEntities();
    }

    public void update(float delta){
        _engine.update(delta);
    }

    public void setEntityRelationship(Entity entity, Entity parent){
        var relationship = entity.getComponent(RelationshipComponent.class);

        if (relationship.parent != null){
            if (relationship.prev != null && relationship.next != null){

                relationship.prev.getComponent(RelationshipComponent.class).next = relationship.next;
                relationship.next.getComponent(RelationshipComponent.class).prev = relationship.prev;

                relationship.prev = null;
                relationship.next = null;

            } else if (relationship.next != null){

                relationship.parent.getComponent(RelationshipComponent.class).first = relationship.next;
                relationship.next.getComponent(RelationshipComponent.class).prev = null;
                relationship.next = null;

            } else if (relationship.prev != null){

                relationship.prev.getComponent(RelationshipComponent.class).next = null;
                relationship.prev = null;

            } else {
                relationship.parent.getComponent(RelationshipComponent.class).first = null;
            }

            relationship.parent = null;
        }

        if (parent != null) {
            var parentRelationship = parent.getComponent(RelationshipComponent.class);
            if(parentRelationship.first == null){
                parentRelationship.first = entity;
            } else {
                Entity prev = null;
                Entity current = parentRelationship.first;
                while (current != null){
                    prev = current;
                    current = current.getComponent(RelationshipComponent.class).next;
                }
                prev.getComponent(RelationshipComponent.class).next = entity;
                relationship.prev = prev;
            }

            relationship.parent = parent;
        }
    }

    private void destroyEntityChildren(Entity entity){
        var relationship = entity.getComponent(RelationshipComponent.class);
        Entity next = null;
        Entity current = relationship.first;
        while (current != null){
            var currentRelationship = current.getComponent(RelationshipComponent.class);

            if(currentRelationship.first != null)
                destroyEntityChildren(current);

            next = currentRelationship.next;
            destroyEntity(current);
            current = next;
        }
    }
}

class Listener implements EntityListener{

    @Override
    public void entityAdded(Entity entity) {
    }

    @Override
    public void entityRemoved(Entity entity) {
        var mesh = entity.getComponent(MeshComponent.class);
        var mat = entity.getComponent(MaterialComponent.class);
        if(mesh != null)
            mesh.vao.release();

        if(mat != null){
            if(mat.diffuseMap != null)
                mat.diffuseMap.release();
            if(mat.specularMap != null)
                mat.specularMap.release();
            if(mat.normalMap != null)
                mat.normalMap.release();
        }
    }
}
