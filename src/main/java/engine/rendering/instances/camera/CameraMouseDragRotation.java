package engine.rendering.instances.camera;

import engine.architecture.system.Window;
import engine.architecture.ui.element.ElementManager;
import engine.architecture.ui.event.InputManager;
import engine.utils.libraryWrappers.maths.joml.Vector2d;
import org.lwjgl.glfw.GLFW;

public class CameraMouseDragRotation extends CameraController {

    private CameraMouseRotation rotation = new CameraMouseRotation();
    private Vector2d cursorPosAtStart;

    @Override
    public void control(Camera camera) {
        if (InputManager.instance().isButtonHeld(GLFW.GLFW_MOUSE_BUTTON_RIGHT) ||
                InputManager.instance().isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
            if (cursorPosAtStart == null) {
                Window.instance().hideCursor(true);
                cursorPosAtStart = Window.instance().getCursorPos();
            }
            ElementManager.instance().setEventHog(this);
            rotation.control(camera);
        }
        if (InputManager.instance().isButtonReleased(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
            ElementManager.instance().resetEventHog();
            Window.instance().hideCursor(false);
            Window.instance().setCursorPos(cursorPosAtStart);
            cursorPosAtStart = null;
        }
    }
}
