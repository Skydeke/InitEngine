package engine.rendering.instances.camera;

import engine.utils.libraryBindings.maths.joml.Vector3f;
import engine.utils.libraryBindings.maths.objects.Transformable;
import engine.utils.libraryBindings.maths.utils.Vector3;

public class ThirdPersonCamera extends CameraController {

    private final CameraController positionControl;
    private final CameraController rotationControl;
    private final CameraController scaleControl;
    private final Vector3f offset;

    public ThirdPersonCamera(Transformable center, Camera camera) {
        this.positionControl = new CameraFollowingMovement(center);
        this.rotationControl = new CameraMouseDragRotation();
        this.scaleControl = new CameraMouseScale();
        this.offset = Vector3.create();
    }

    public ThirdPersonCamera(Transformable center, Camera camera, Vector3f offset) {
        this.positionControl = new CameraFollowingMovement(center);
        this.rotationControl = new CameraMouseDragRotation();
        this.scaleControl = new CameraMouseScale();
        this.offset = offset;
    }

    @Override
    public void control(Camera camera) {
        rotationControl.control(camera);
        positionControl.control(camera);
        scaleControl.control(camera);
        camera.getTransform().addPosition(offset);
    }
}
