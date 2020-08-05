package engine.rendering.instances.camera;

public class FlyCamera extends CameraController {

    private final CameraController positionControl;
    private final CameraController rotationControl;

    public FlyCamera() {
        this.positionControl = new CameraKeyboardMovement();
        this.rotationControl = new CameraMouseDragRotation();
    }

    @Override
    public void control(Camera camera) {
        rotationControl.control(camera);
        positionControl.control(camera);
    }
}
