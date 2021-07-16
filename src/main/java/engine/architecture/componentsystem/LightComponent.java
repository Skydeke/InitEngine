package engine.architecture.componentsystem;

import engine.architecture.scene.light.PointLight;

public class LightComponent extends GameComponent{

    private PointLight light;

    /**
     * Initialize the component, get all the other components needed etc...
     */
    @Override
    public void start() {
        light = new PointLight();
        light.setColor(1f, 0f, 0f).setIntensity(200f);
    }

    /**
     * Update the component, the method is called once per frame
     */
    @Override
    public void update() {
        light.getTransform().setPosition(super.getTransform().getPosition());
    }

    @Override
    public void stop() {
        light.remove();
    }
}
