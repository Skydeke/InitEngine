package engine.rendering.instances.camera;

import engine.utils.libraryBindings.maths.joml.Vector3f;
import engine.utils.libraryBindings.maths.objects.Transform;
import engine.utils.libraryBindings.maths.objects.Transformable;
import engine.utils.libraryBindings.maths.utils.Vector3;

public class CameraFollowingMovement extends CameraController {

    private final Transform centerTransform;

    public CameraFollowingMovement(Transformable centerTransform) {
        this.centerTransform = centerTransform.getTransform();
    }

    @Override
    public void control(Camera camera) {
        Vector3f positionOffset2 = Vector3.forward();
        positionOffset2.rotate(camera.getTransform().getRotation());
        positionOffset2.mul(camera.getTransform().getScale());
        positionOffset2.add(centerTransform.getPosition());
        camera.getTransform().setPosition(positionOffset2);
    }
}
