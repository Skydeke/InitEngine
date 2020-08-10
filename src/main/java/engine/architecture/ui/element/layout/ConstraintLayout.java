package engine.architecture.ui.element.layout;

import engine.architecture.ui.element.UIElement;

import java.util.Optional;

public class ConstraintLayout extends Layout {

    public ConstraintLayout(UIElement e) {
        super(e);
    }

    /**
     * Constraint layout that takes the entire screen no matter the input
     * and is default the parameter in the Element class
     *
     * @param e     any element object
     * @param index index of element in owner heirarchy
     * @return Box where element conforms to
     */
    @Override
    public Optional<Box> findRelativeTransform(UIElement e, final int index) {
        if (e.getConstraints() != null){
            e.getConstraints().updateConstraints();
            return Optional.of(e.getConstraints().getRelativeBox());
        }else {
            return Optional.of(e.getRelativeBox());
        }
    }

    @Override
    public void update() {
        if (owner.getConstraints() != null)
            owner.getConstraints().updateConstraints();
    }
}
