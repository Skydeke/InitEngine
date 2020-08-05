package engine.rendering.instances.camera;

import java.util.Arrays;
import java.util.List;

public class CameraControllers extends CameraController {

    private final List<CameraController> controllers;

    public CameraControllers(CameraController... controllers) {
        this.controllers = Arrays.asList(controllers);
    }

    @Override
    public void control(Camera camera) {
        for (CameraController controller : controllers) {
            controller.control(camera);
        }
    }
}
