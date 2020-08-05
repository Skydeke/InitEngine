package engine.ui.layout;

import engine.ui.element.UIElement;

import java.util.Optional;

public class AbsoluteLayout extends Layout {

    public AbsoluteLayout(UIElement e) {
        super(e);
    }

    /**
     * Absolute layout that takes the entire screen no matter the input
     * and is default the parameter in the Element class
     *
     * @param e     any element object
     * @param index index of element in owner heirarchy
     * @return Box where element conforms to
     */
    @Override
    public Optional<Box> findRelativeTransform(UIElement e, final int index) {
        return Optional.of(e.getRelativeBox());
    }

    @Override
    public void update() {

    }
}
