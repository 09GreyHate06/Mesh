package scene.components.renderering;

import com.badlogic.ashley.core.Component;

public class MeshRendererComponent implements Component {
    public Primitive primitive = Primitive.Triangles;
    public boolean receiveLight = true;
    // boolean castShadow = true;
}
