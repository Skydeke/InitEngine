package engine.rendering.instances.camera;

import engine.architecture.system.Time;
import engine.ui.event.InputManager;
import engine.utils.libraryWrappers.maths.joml.Vector3f;
import engine.utils.libraryWrappers.maths.utils.Vector3;
import org.lwjgl.glfw.GLFW;

public class CameraKeyboardMovement extends CameraController {

    private final Vector3f speeds = Vector3.of(10);

    @Override
    public void control(Camera camera) {
        final Vector3f deltaPosition = Vector3.create();
        if (InputManager.instance().isKeyPressed(GLFW.GLFW_KEY_W) ||
                InputManager.instance().isKeyHeld(GLFW.GLFW_KEY_W)) {
            deltaPosition.add(0, 0, -speeds.z);
        }
        if (InputManager.instance().isKeyPressed(GLFW.GLFW_KEY_A) ||
                InputManager.instance().isKeyHeld(GLFW.GLFW_KEY_A)) {
            deltaPosition.add(-speeds.x, 0, 0);
        }
        if (InputManager.instance().isKeyPressed(GLFW.GLFW_KEY_S) ||
                InputManager.instance().isKeyHeld(GLFW.GLFW_KEY_S)) {
            deltaPosition.add(0, 0, +speeds.z);
        }
        if (InputManager.instance().isKeyPressed(GLFW.GLFW_KEY_D) ||
                InputManager.instance().isKeyHeld(GLFW.GLFW_KEY_D)) {
            deltaPosition.add(+speeds.x, 0, 0);
        }
        if (InputManager.instance().isKeyPressed(GLFW.GLFW_KEY_SPACE) ||
                InputManager.instance().isKeyHeld(GLFW.GLFW_KEY_SPACE)) {
            deltaPosition.add(0, +speeds.y, 0);
        }
        if (InputManager.instance().isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) ||
                InputManager.instance().isKeyHeld(GLFW.GLFW_KEY_LEFT_SHIFT)) {
            deltaPosition.add(0, -speeds.y, 0);
        }
//        deltaPosition.rotateY(camera.getTransform().getRotation().y);
//        deltaPosition.rotateX(camera.getTransform().getRotation().x);
//        deltaPosition.rotateZ(camera.getTransform().getRotation().z);
        deltaPosition.rotate(camera.getTransform().getRotation());
        deltaPosition.mul(Time.getDelta());
        camera.getTransform().getPosition().add(deltaPosition);
    }

    public void setSpeed(float speed) {
        this.speeds.set(speed);
    }

    public void setSpeeds(float xAxis, float yAxis, float zAxis) {
        this.speeds.set(xAxis, yAxis, zAxis);
    }
}
