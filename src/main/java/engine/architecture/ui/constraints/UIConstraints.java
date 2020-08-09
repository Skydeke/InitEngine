package engine.architecture.ui.constraints;

import engine.architecture.ui.element.layout.Box;

public class UIConstraints {

    private Constraint xPosition;
    private Constraint yPosition;
    private Constraint xScale;
    private Constraint yScale;

    private Box screenCoords = new Box(0, 0, 0, 0);

    public UIConstraints(Constraint x, Constraint y, Constraint w, Constraint h) {
        x(x);
        y(y);
        w(w);
        h(h);
    }

    public void updateConstraints() {
        if (xPosition != null) {
            xPosition.setXdependant();
            xPosition.update();
            screenCoords.setX(xPosition.getScreenValue());
        }
        if (yPosition != null) {
            yPosition.setYdependant();
            yPosition.update();
            screenCoords.setY(yPosition.getScreenValue());
        }
        if (xScale != null) {
            xScale.setXdependant();
            xScale.update();
            screenCoords.setWidth(xScale.getScreenValue());
        }
        if (yScale != null) {
            yScale.setYdependant();
            yScale.update();
            screenCoords.setHeight(yScale.getScreenValue());
        }
    }

    public UIConstraints x(Constraint xPosition) {
        this.xPosition = xPosition;
        updateConstraints();
        return this;
    }

    public UIConstraints y(Constraint yPosition) {
        this.yPosition = yPosition;
        updateConstraints();
        return this;
    }

    public UIConstraints w(Constraint xScale) {
        this.xScale = xScale;
        updateConstraints();
        return this;
    }

    public UIConstraints h(Constraint yScale) {
        this.yScale = yScale;
        updateConstraints();
        return this;
    }

    public Box getRelativeBox() {
        return screenCoords;
    }
}
