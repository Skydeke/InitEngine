package engine.architecture.ui.constraints;

public class PercentageConstraint extends Constraint{

    private float screenValue;

    public PercentageConstraint(float percentage){
        this.screenValue = percentage;
    }

    @Override
    public void update() {

    }

    @Override
    public float getScreenValue() {
        return screenValue;
    }
}
