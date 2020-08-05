package engine.rendering.instances.camera;

import engine.ui.event.KeyboardEvent;
import org.lwjgl.glfw.GLFW;

public class CameraKeyboardRotation extends CameraController {

    private float speed = .3f;

    @Override
    public void control(Camera camera) {
        onEvent(k -> {
            KeyboardEvent e = (KeyboardEvent) k;
            if (e.getAction() == KeyboardEvent.KEY_PRESSED) {
                switch (e.getKey()) {
                    case GLFW.GLFW_KEY_G:
                        camera.getTransform().addRotation(0, -speed, 0);
                        break;
                    case GLFW.GLFW_KEY_J:
                        camera.getTransform().addRotation(0, +speed, 0);
                        break;
                    case GLFW.GLFW_KEY_Y:
                        camera.getTransform().addRotation(-speed, 0, 0);
                        break;
                    case GLFW.GLFW_KEY_N:
                        camera.getTransform().addRotation(+speed, 0, 0);
                        break;
                }
            }

        });
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
