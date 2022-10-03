package scene.components.renderering;

import com.badlogic.ashley.core.Component;
import org.joml.Vector3f;

public class DirectionalLightComponent implements Component {
    public Vector3f color = new Vector3f(1.0f, 1.0f, 1.0f);
    public float ambientIntensity = 0.2f;
    public float diffuseIntensity = 1.0f;
    public float specularIntensity = 1.0f;
}

