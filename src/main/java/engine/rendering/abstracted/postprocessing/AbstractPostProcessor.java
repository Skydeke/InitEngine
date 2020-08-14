package engine.rendering.abstracted.postprocessing;

import engine.architecture.system.Window;
import engine.rendering.RenderOutputData;
import engine.utils.libraryBindings.opengl.constants.DataType;
import engine.utils.libraryBindings.opengl.constants.FormatType;
import engine.utils.libraryBindings.opengl.constants.RenderMode;
import engine.utils.libraryBindings.opengl.fbos.Fbo;
import engine.utils.libraryBindings.opengl.fbos.FboTarget;
import engine.utils.libraryBindings.opengl.fbos.attachment.TextureAttachment;
import engine.utils.libraryBindings.opengl.shaders.RenderState;
import engine.utils.libraryBindings.opengl.shaders.ShadersProgram;
import engine.utils.libraryBindings.opengl.textures.ITexture;
import engine.utils.libraryBindings.opengl.textures.TextureConfigs;
import engine.utils.libraryBindings.opengl.utils.GlBuffer;
import engine.utils.libraryBindings.opengl.utils.GlRendering;
import engine.utils.libraryBindings.opengl.utils.GlUtils;

public abstract class AbstractPostProcessor implements PostProcessor {

    private final ShadersProgram<RenderOutputData> shadersProgram;
    private Fbo fbo;

    public AbstractPostProcessor(String fragFile) throws Exception {
        this("/engine/postprocessing/simpleVertex.glsl", fragFile);
    }

    public AbstractPostProcessor(String vertFile, String fragFile) throws Exception {
        this.shadersProgram = ShadersProgram.create(vertFile, fragFile);
    }

    /**
     * Create an fbo with the given width and height values
     *
     * @param width  the width of the fbo
     * @param height the height of the fbo
     * @return the created fbo
     */
    protected Fbo createFbo(int width, int height) {
        final Fbo fbo = Fbo.create(width, height);
        fbo.addAttachment(TextureAttachment.ofColour(0, new TextureConfigs(
                FormatType.RGB8, FormatType.RGB, DataType.U_BYTE)));
        return fbo;
    }

    protected void beforeProcess(RenderOutputData renderOutputData) {

    }

    /**
     * Returns the fbo of the post processor
     *
     * @return the fbo
     */
    protected final Fbo getFbo() {
        return fbo == null ? (fbo = createFbo(Window.instance().getWidth(), Window.instance().getHeight())) : fbo;
    }

    /**
     * Returns the shaders program of the post processor
     *
     * @return the shaders program
     */
    protected final ShadersProgram<RenderOutputData> getShadersProgram() {
        return shadersProgram;
    }

    protected void draw() {
        GlRendering.drawArrays(RenderMode.TRIANGLE_STRIP, 0, 4);
    }

    @Override
    public void process(RenderOutputData renderOutputData) {
        beforeProcess(renderOutputData);

        fbo.bind(FboTarget.DRAW_FRAMEBUFFER);
        GlUtils.clear(GlBuffer.COLOUR);
        getShadersProgram().bind();

        getShadersProgram().updatePerRenderUniforms(new RenderState<>(null, renderOutputData, null, 0));
        getShadersProgram().updatePerInstanceUniforms(new RenderState<>(null, renderOutputData, null, 0));

        draw();

        getShadersProgram().unbind();
    }

    @Override
    public final void resize(int width, int height) {
        if (getFbo() == null) {
            fbo = createFbo(width, height);
        } else if (!getFbo().isSized(width, height)) {
            getFbo().delete();
            fbo = createFbo(width, height);
        }
    }

    @Override
    public final ITexture getTexture() {
        return getFbo().getAttachments().get(0).getTexture();
    }

    @Override
    public final void blitToFbo(Fbo fbo) {
        getFbo().blitFbo(fbo);
    }

    @Override
    public final void blitToScreen() {
        getFbo().blitToScene();
    }

    @Override
    public final void cleanUp() {
        getFbo().delete();
        getShadersProgram().delete();
    }
}
