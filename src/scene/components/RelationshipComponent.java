package scene.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import glCore.renderer.VertexArray;

public class RelationshipComponent implements Component {
    public Entity parent = null;
    public Entity first = null;
    public Entity prev = null;
    public Entity next = null;
}

