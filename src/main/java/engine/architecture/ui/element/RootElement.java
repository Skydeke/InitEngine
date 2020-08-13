package engine.architecture.ui.element;

import engine.architecture.ui.element.layout.Box;
import engine.architecture.ui.element.layout.LayoutType;

public class RootElement extends UIElement {

    public RootElement() {
        super();
        relativeBox = new Box(0, 0, 1, 1);
        absoluteBox = new Box(0, 0, 1, 1);
        setAlignType(LayoutType.ABSOLUTE);
    }

    @Override
    public void render() {
        getChildren().stream()
                .filter(UIElement::isActivated)
                .forEach(UIElement::render);
    }

}
