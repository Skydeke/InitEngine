package engine.architecture.scene.light;

import engine.architecture.scene.node.Node;
import engine.utils.libraryBindings.maths.joml.Vector3f;

public abstract class Light extends Node {

    private Vector3f color;
    private float intensity;


    protected Light() {

        setColor(new Vector3f(1, 1, 1));
        setIntensity(1);
        //this.setRotation(new Vector3f(0,1,0));
        this.activate();

        if (!LightManager.registerLight(this))
            this.deactivate();

    }

    public <T extends Light> T setColor(float red, float green, float blue) {
        return setColor(new Vector3f(red, green, blue));
    }

    public Vector3f getColor() {
        return color;
    }

    public <T extends Light> T setColor(Vector3f color) {
        this.color = color;
        return (T) this;
    }

    public float getIntensity() {
        return intensity;
    }

    public <T extends Light> T setIntensity(float intensity) {
        this.intensity = intensity;
        return (T) this;
    }
}
