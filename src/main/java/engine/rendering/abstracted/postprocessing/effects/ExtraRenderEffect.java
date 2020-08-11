package engine.rendering.abstracted.postprocessing.effects;

import engine.architecture.scene.node.Node;
import engine.utils.libraryBindings.opengl.fbos.FrameBufferObject;
import engine.utils.libraryBindings.opengl.textures.TextureObject;

public abstract class ExtraRenderEffect extends Effect {

    protected final FrameBufferObject fbo;

    protected final int width;
    protected final int height;

    protected ExtraRenderEffect(Node group, int width, int height) {
        super(group);
        this.width = width;
        this.height = height;
        this.fbo = new FrameBufferObject();
        setEnabled(true);
    }

    protected abstract FrameBufferObject createFbo();

    public TextureObject getTexture() {
        return fbo.getAttachment(0);
    }

    @Override
    public void onDelete() {
    }
}
