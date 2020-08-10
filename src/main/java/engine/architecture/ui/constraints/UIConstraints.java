package engine.architecture.ui.constraints;

import engine.architecture.ui.element.layout.Box;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class UIConstraints {

    private Optional<Constraint> xPosition = Optional.empty();
    private Optional<Constraint> yPosition = Optional.empty();
    @Getter private Optional<Constraint> xScale = Optional.empty();
    @Getter private Optional<Constraint> yScale = Optional.empty();

    private Box screenCoords = new Box(0, 0, 0, 0);

    public UIConstraints(Constraint x, Constraint y, Constraint w, Constraint h) {
        x(x);
        y(y);
        w(w);
        h(h);
    }

    public UIConstraints(){

    }

    public void updateConstraints() {
        if (xPosition.isPresent()) {
            xPosition.get().setDependingOnX();
            xPosition.get().update();
            screenCoords.setX(xPosition.get().getScreenValue());
        }
        if (yPosition.isPresent()) {
            yPosition.get().setDependingOnY();
            yPosition.get().update();
            screenCoords.setY(yPosition.get().getScreenValue());
        }
        if (xScale.isPresent()) {
            xScale.get().setDependingOnX();
            xScale.get().update();
            screenCoords.setWidth(xScale.get().getScreenValue());
        }
        if (yScale.isPresent()) {
            yScale.get().setDependingOnY();
            yScale.get().update();
            screenCoords.setHeight(yScale.get().getScreenValue());
        }
    }

    public UIConstraints x(@NotNull Constraint xPosition) {
        this.xPosition = Optional.of(xPosition);
        this.xPosition.get().setUiConstraints(this);
        updateConstraints();
        return this;
    }

    public UIConstraints y(@NotNull Constraint yPosition) {
        this.yPosition = Optional.of(yPosition);
        this.yPosition.get().setUiConstraints(this);
        updateConstraints();
        return this;
    }

    public UIConstraints w(@NotNull Constraint xScale) {
        this.xScale = Optional.of(xScale);
        this.xScale.get().setUiConstraints(this);
        updateConstraints();
        return this;
    }

    public UIConstraints h(@NotNull Constraint yScale) {
        this.yScale = Optional.of(yScale);
        this.yScale.get().setUiConstraints(this);
        updateConstraints();
        return this;
    }

    public Box getRelativeBox() {
        return screenCoords;
    }
}
