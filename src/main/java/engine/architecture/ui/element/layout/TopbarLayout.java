package engine.architecture.ui.element.layout;

import engine.architecture.ui.element.UIElement;
import engine.utils.libraryBindings.maths.joml.Vector2i;

import java.util.Optional;

public class TopbarLayout extends Layout {

    public int spacing;

    public TopbarLayout(UIElement parent, int spacing) {
        super(parent);
        this.spacing = spacing;
    }

    @Override
    public Optional<Box> findRelativeTransform(UIElement e, int index) {
        Vector2i size = owner.getPixelSize();
        float ratio = (float) size.y / (float) size.x;
        float space = (float) spacing / (float) size.x;

        Box outer = new Box((ratio + space) * index, 0f, ratio, 1f);

        return Optional.of(applyInset(outer, owner.getChildren().get(index).getInset()));
    }

    @Override
    public void update() {

    }
}
