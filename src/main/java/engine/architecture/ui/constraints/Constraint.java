package engine.architecture.ui.constraints;

import lombok.Getter;
import lombok.Setter;

public abstract class Constraint {

    @Getter private boolean isDependingOnX;
    @Getter private boolean isDependingOnY;

    @Setter protected UIConstraints uiConstraints;

    public abstract void update();

    public abstract float getScreenValue();

    public void setDependingOnY(){
        isDependingOnX = false;
        isDependingOnY = true;
    }

    public void setDependingOnX(){
        isDependingOnX = true;
        isDependingOnY = false;
    }
}
