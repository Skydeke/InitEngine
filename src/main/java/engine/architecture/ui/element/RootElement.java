package engine.architecture.ui.element;

import engine.architecture.ui.element.layout.Box;

import java.util.Collections;

public class RootElement extends UIElement {

    public RootElement() {
        super();
        relativeBox = new Box(0, 0, 1, 1);
        absoluteBox = new Box(0, 0, 1, 1);
    }

    @Override
    public void render() {
        Collections.reverse(getChildren());
        getChildren().stream()
                .filter(UIElement::isActivated)
                .forEach(UIElement::render);
        Collections.reverse(getChildren());
    }

}
