package engine.rendering.instances.camera;

import engine.ui.event.InputManager;

public class CameraMouseRotation extends CameraController {


    private float sensitivity = 100f;


    @Override
    public void control(Camera camera) {
        final float dx = InputManager.instance().getCursorDelta().x * sensitivity;
        final float dy = InputManager.instance().getCursorDelta().y * sensitivity;
        camera.getTransform().addRotation(-dy, -dx, 0);
    }

    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;
    }
}
