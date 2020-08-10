package engine.architecture.ui.constraints;

public class CenterConstraint extends Constraint {

    private float screenValue;


    @Override
    public void update() {
        if (isDependingOnX() && uiConstraints.getXScale().isPresent()) {
            screenValue = (1 - uiConstraints.getXScale().get().getScreenValue()) / 2;
        } else if (isDependingOnY() && uiConstraints.getYScale().isPresent()){
            screenValue = (1 - uiConstraints.getYScale().get().getScreenValue()) / 2;
        }
    }

    @Override
    public float getScreenValue() {
        return screenValue;
    }
}
