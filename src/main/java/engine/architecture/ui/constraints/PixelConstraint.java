package engine.architecture.ui.constraints;

import engine.architecture.system.Window;
import lombok.Getter;

public class PixelConstraint extends Constraint{

    private int pixels;
    @Getter private float valueInScreen;

    public PixelConstraint(int pixels){
        this.pixels = pixels;
    }


    @Override
    public void update() {
        if (isDependingOnX())
            valueInScreen = (float) pixels / Window.instance().getWidth();
        else
            valueInScreen = (float) pixels / Window.instance().getHeight();
    }

    @Override
    public float getScreenValue() {
        return valueInScreen;
    }
}
