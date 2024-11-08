package engine.rendering.instances.renderers.pbr;

import engine.architecture.models.Model;
import engine.architecture.scene.entity.Entity;
import engine.rendering.instances.renderers.UUIDRenderer;
import engine.rendering.instances.renderers.shadow.ShadowRenderer;
import lombok.Getter;
import lombok.Setter;

public class PBRModel extends Entity {

    @Getter
    private PBRMaterial material;

    @Setter
    private float UVscalar = 1f;

    public PBRModel(Model mesh, PBRMaterial material) {
        super(mesh);
        this.material = material;
    }

    public void process() {
        PBRRenderer.getInstance().process(this);
        ShadowRenderer.getInstance().process(this);
        UUIDRenderer.getInstance().process(this);
    }

    public float getUVscalar() {
        return UVscalar;
    }

}
