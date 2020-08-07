package engine.architecture.ui.event.mouse;

import engine.architecture.system.Window;
import engine.architecture.ui.event.Event;
import engine.utils.libraryWrappers.maths.joml.Vector2f;
import engine.utils.libraryWrappers.maths.joml.Vector2i;
import lombok.Getter;

public class MouseWheelEvent extends Event {

    Vector2i pixelScreenPos;
    Vector2f screenPos;

    @Getter
    private float wheelDelta;

    public MouseWheelEvent(double delta, Vector2f screenPos) {
        this.screenPos = screenPos;
        this.pixelScreenPos = new Vector2i(
                (int) (screenPos.x * Window.instance().getWidth()),
                (int) (screenPos.y * Window.instance().getHeight()));
        this.wheelDelta = (float) delta;
    }
}
