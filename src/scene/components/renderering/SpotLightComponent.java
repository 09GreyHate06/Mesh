package scene.components.renderering;

import com.badlogic.ashley.core.Component;
import org.joml.Vector3f;

public class SpotLightComponent implements Component {
    public Vector3f color = new Vector3f(1.0f, 1.0f, 1.0f);
    public float ambientIntensity = 0.2f;
    public float diffuseIntensity = 1.0f;
    public float specularIntensity = 1.0f;

    public float constant = 1.0f;
    public float linear = 0.14f;
    public float quadratic = 0.0007f;

    public float innerCutOffAngle = 10.0f;
    public float outerCutOffAngle = 15.0f;
}
