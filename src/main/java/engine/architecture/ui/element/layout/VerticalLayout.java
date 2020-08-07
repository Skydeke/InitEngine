package engine.architecture.ui.element.layout;

import engine.architecture.ui.element.UIElement;

import java.util.Optional;

public class VerticalLayout extends Layout {

    int parentHeight;
    int scrollHeight = 0;

    public VerticalLayout(UIElement parent, int elementHeight) {
        super(parent);
        this.parentHeight = elementHeight;
    }

    public void update() {

    }

    public Optional<Box> findRelativeTransform(UIElement e, int index) {

        int heightPixelY = index * parentHeight - scrollHeight;

        if (index >= owner.getChildren().size() || heightPixelY > owner.getPixelSize().y)
            return Optional.of(new Box(0, 0, 0, 0));

        float heightRelativeY = (float) heightPixelY / (float) owner.getPixelSize().y;
        float sizeRelativeY = (float) parentHeight / (float) owner.getPixelSize().y;

        Box outer = new Box(0, 1 - heightRelativeY - sizeRelativeY,
                1, sizeRelativeY);
        Inset inset = owner.getChildren().get(index).getInset();

        return Optional.of(applyInset(outer, inset));
    }
}
