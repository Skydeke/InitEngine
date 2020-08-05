package engine.architecture.scene.light;

import engine.architecture.scene.entity.Entity;
import engine.fileLoaders.ModelLoader;
import engine.rendering.instances.renderers.DebugRenderer;

public class PointLight extends Light {

    private Entity debugEntity;

    public PointLight(PointLight light) {
        this();
        setColor(light.getColor());
        setIntensity(light.getIntensity());
    }

    public PointLight() {
        super();
        debugEntity = new Entity(ModelLoader.cube) {
            @Override
            public void process() {
                DebugRenderer.getInstance().process(this);
            }
        };
        addChild(debugEntity);
        getTransform().setScale(.2f);
    }
}
