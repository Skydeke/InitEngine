package engine.ui.layout;

import engine.ui.element.UIElement;
import engine.utils.libraryWrappers.maths.joml.Vector2i;
import lombok.Getter;

import java.util.Optional;

public abstract class Layout {

    protected @Getter
    UIElement owner;

    public Layout(UIElement parent) {
        this.owner = parent;
    }

    public Box applyInset(Box input, Inset inset) {
        Vector2i inputPixelSize = owner.getPixelSizeForRelative(input);
        float x = (float) inset.left / (float) inputPixelSize.x;
        float y = (float) inset.bottom / (float) inputPixelSize.y;
        float width = 1 - ((float) inset.right / (float) inputPixelSize.x) - x;
        float height = 1 - ((float) inset.top / (float) inputPixelSize.y) - y;
        return (new Box(x, y, width, height)).relativeTo(input);
    }

    public abstract Optional<Box> findRelativeTransform(UIElement e, final int index);

    public abstract void update();
}
