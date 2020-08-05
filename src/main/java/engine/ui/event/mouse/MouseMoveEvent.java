package engine.ui.event.mouse;

import engine.ui.event.Event;
import lombok.Getter;
import org.joml.Vector2f;

public class MouseMoveEvent extends Event {

    @Getter
    Vector2f screenPos;
    @Getter
    Vector2f screenDelta;

    public MouseMoveEvent(Vector2f screenPos, Vector2f screenDelta) {
        this.screenPos = screenPos;
        this.screenDelta = screenDelta;
    }

    @Override
    public String toString() {
        return "MouseEvent: " + screenPos.toString();
    }
}
