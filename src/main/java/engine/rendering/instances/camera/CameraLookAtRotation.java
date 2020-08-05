package engine.rendering.instances.camera;

import engine.utils.libraryWrappers.maths.joml.Vector3f;

public class CameraLookAtRotation extends CameraController {

    private final Vector3f center;

    public CameraLookAtRotation(Vector3f center) {
        this.center = center;
    }

    @Override
    public void control(Camera camera) {
        camera.getTransform().lookAt(center);
    }
}
