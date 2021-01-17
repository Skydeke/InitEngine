package engine.utils;

import engine.architecture.system.Window;
import org.joml.Vector2d;
import org.joml.Vector2f;

public class Coordinates {

    /**
     * Converts glfw pixel coordinates to engine.utils.libraryWrappers.opengl NDC coordinates.
     * Note: glfw y axis starts at top whereas engine.utils.libraryWrappers.opengl NDC coordinates
     * at the bottom
     *
     * @param screenCoords screen pixel coordinates
     * @return normalized device coordinates
     */
    public static Vector2f screenToNDC(Vector2f screenCoords) {
        float x = screenCoords.x / Window.instance().getWidth() * 2 - 1;
        float y = 2 - screenCoords.y / Window.instance().getHeight() * 2 - 1;
        return new Vector2f(x, y);
    }

    /**
     * Converts glfw pixel coordinates to engine.utils.libraryWrappers.opengl NDC coordinates.
     * Note: glfw y axis starts at top whereas engine.utils.libraryWrappers.opengl NDC coordinates
     * at the bottom
     *
     * @param screenCoords screen pixel coordinates
     * @return normalized device coordinates
     */
    public static Vector2f screenToNDC(Vector2d screenCoords) {
        float x = (float) screenCoords.x / Window.instance().getWidth() * 2 - 1;
        float y = 2 - (float) screenCoords.y / Window.instance().getHeight() * 2 - 1;
        return new Vector2f(x, y);
    }
}
