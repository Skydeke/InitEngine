package engine.architecture.scene.entity;

import engine.architecture.componentsystem.ComponentBasedBlueprint;
import engine.architecture.models.Model;
import engine.rendering.instances.renderers.DebugRenderer;

public class EntityBlueprint extends ComponentBasedBlueprint {

    private final Model model;

    public EntityBlueprint(Model model) {
        this.model = model;
    }

    @Override
    public Entity createInstance() {
        final Entity entity = new Entity(model) {
            @Override
            public void process() {
                DebugRenderer.getInstance().process(this);
            }
        };
        getComponents().apply(entity);
        return entity;
    }

}
