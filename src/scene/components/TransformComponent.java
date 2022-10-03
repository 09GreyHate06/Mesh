package scene.components;

import com.badlogic.ashley.core.Component;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TransformComponent implements Component {
    public Vector3f position = new Vector3f();
    public Vector3f rotation = new Vector3f();
    public Vector3f scale = new Vector3f(1.0f, 1.0f, 1.0f);

    public Matrix4f getTransformMatrix(){
        Quaternionf rot = new Quaternionf()
                .rotateYXZ((float)Math.toRadians(rotation.y),
                           (float)Math.toRadians(rotation.x),
                           (float)Math.toRadians(rotation.z));

        return new Matrix4f()
                .translate(position)
                .rotate(rot)
                .scale(scale);

    }

    public Vector3f getForward(){
        return new Vector3f(0.0f, 0.0f, -1.0f).mulDirection(getTransformMatrix());
    }

    public Vector3f getUp(){
        return new Vector3f(0.0f, 1.0f, 1.0f).mulDirection(getTransformMatrix());
    }

    public Vector3f getRight(){
        return new Vector3f(1.0f, 0.0f, 0.0f).mulDirection(getTransformMatrix());
    }
}
