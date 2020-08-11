package engine.architecture.ui.element.layout;

import engine.architecture.ui.element.UIElement;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

public class ViewportLayout extends Layout {

    @Setter
    @Getter
    int topBarHeight;

    public ViewportLayout(UIElement parent) {
        super(parent);
    }

    public void update() {
        if (owner.getConstraints() != null)
            owner.getConstraints().updateConstraints();
    }

    public Optional<Box> findRelativeTransform(UIElement e, int index) {
        float factorTop = (float) topBarHeight / (float) owner.getAbsoluteBox().resolution().y;
        if (index == 0) { // top bar
            return Optional.of(new Box(0, 1 - factorTop, 1, factorTop));
        } else if (index == 1) { // edge panel
            return Optional.of(new Box(0, 0, 1, 1 - factorTop));
        } else {
            if (e.getConstraints() != null){
                e.getConstraints().updateConstraints();
                Inset inset = owner.getChildren().get(index).getInset();
                Box box = new Box(0, 0, 1, 1 - factorTop);
                Optional<Box> pos = Optional.of(applyInset(box, inset));
                return Optional.of(e.getConstraints().getRelativeBox().relativeTo(pos.get()));
            }else {
                Inset inset = owner.getChildren().get(index).getInset();
                Box box = new Box(0, 0, 1, 1 - factorTop);
                return Optional.of(applyInset(box, inset));
            }
        }
    }
}
