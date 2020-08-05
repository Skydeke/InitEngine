package engine.rendering.instances.renderers.sky;

import engine.architecture.scene.entity.Entity;
import engine.fileLoaders.ModelLoader;

public class Sky extends Entity {

    public Sky() {
        super(ModelLoader.sphere);

        getTransform().setScale(2800);
    }

    @Override
    public void process() {
        SkyRenderer.getInstance().process(this);
    }
}
