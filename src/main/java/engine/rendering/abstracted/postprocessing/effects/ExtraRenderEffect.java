package engine.rendering.abstracted.postprocessing.effects;

import engine.architecture.scene.node.Node;
import engine.utils.libraryBindings.opengl.fbos.Fbo;
import engine.utils.libraryBindings.opengl.textures.ITexture;

public abstract class ExtraRenderEffect extends Effect {

    protected final Fbo fbo;

    protected final int width;
    protected final int height;

    protected ExtraRenderEffect(Node group, int width, int height) {
        super(group);
        this.width = width;
        this.height = height;
        this.fbo = Fbo.create(width, height);
        setEnabled(true);
    }

    protected abstract Fbo createFbo();

    public ITexture getTexture() {
        return fbo.getAttachments().get(0).getTexture();
    }

    @Override
    public void onDelete() {
    }
}
