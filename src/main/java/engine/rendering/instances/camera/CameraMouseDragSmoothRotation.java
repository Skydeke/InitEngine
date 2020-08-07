package engine.rendering.instances.camera;

import engine.architecture.ui.event.InputManager;
import engine.architecture.ui.event.mouse.MouseClickEvent;
import org.lwjgl.glfw.GLFW;

public class CameraMouseDragSmoothRotation extends CameraController {


    private float sensitivity = .01f;

    private float xVelocity = 0;
    private float yVelocity = 0;


    @Override
    public void control(Camera camera) {
        onEvent(e -> {
            if (e instanceof MouseClickEvent) {
                MouseClickEvent k = (MouseClickEvent) e;
                if (k.getAction() == GLFW.GLFW_PRESS &&
                        k.getKey() == GLFW.GLFW_MOUSE_BUTTON_RIGHT)
                    xVelocity += -InputManager.instance().getCursorDelta().x * sensitivity;
                yVelocity += -InputManager.instance().getCursorDelta().y * sensitivity;
            }
        });
        camera.getTransform().addRotation(xVelocity, yVelocity, 0);
        xVelocity = xVelocity * .95f;
        yVelocity = yVelocity * .95f;
    }

    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;
    }
}
