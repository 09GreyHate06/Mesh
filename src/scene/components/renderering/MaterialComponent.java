package scene.components.renderering;

import com.badlogic.ashley.core.Component;
import glCore.renderer.Texture2D;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class MaterialComponent implements Component {
    public Texture2D diffuseMap = null;
    public Texture2D specularMap = null;
    public Texture2D normalMap = null;
    public Vector4f color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    public Vector2f tiling = new Vector2f(1.0f, 1.0f);
    public float shininess = 32.0f;
}
