package engine.rendering.instances.camera;

import engine.architecture.event.InputManager;

public class CameraMouseScale extends CameraController {


    private float sensitivity = 3;


    @Override
    public void control(Camera camera) {
        final float change = -(InputManager.instance().getScrollAmount() * sensitivity);
        camera.getTransform().addScale(change);
    }

    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;
    }
}
