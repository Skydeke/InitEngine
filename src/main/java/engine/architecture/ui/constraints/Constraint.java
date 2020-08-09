package engine.architecture.ui.constraints;

import lombok.Getter;

public abstract class Constraint {

    @Getter private boolean isXdependant;
    @Getter private boolean isYdependant;

    public abstract void update();

    public abstract float getScreenValue();

    public void setYdependant(){
        isXdependant = false;
        isYdependant = true;
    }

    public void setXdependant(){
        isXdependant = true;
        isYdependant = false;
    }
}
