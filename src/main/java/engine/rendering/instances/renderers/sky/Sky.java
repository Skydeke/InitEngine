package engine.rendering.instances.renderers.sky;

import engine.architecture.models.Material;
import engine.architecture.scene.entity.Entity;
import engine.fileLoaders.ModelLoader;

public class Sky extends Entity {

    public Sky() throws Exception {
//        super(ModelLoader.load("/models/primitives/dome.obj"));
        super(ModelLoader.sphere);
        getModel().getMeshes()[0].setMaterial(Material.builder().deactivateBackFaceCulling().create());
        getTransform().setScale(1000);
    }

    @Override
    public void process() {
        SkyRenderer.getInstance().process(this);
    }
}
