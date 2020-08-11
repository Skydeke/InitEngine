package engine.rendering.abstracted.postprocessing;

import engine.architecture.system.Window;
import engine.rendering.RenderOutputData;
import engine.utils.libraryBindings.opengl.constants.RenderMode;
import engine.utils.libraryBindings.opengl.fbos.FrameBufferObject;
import engine.utils.libraryBindings.opengl.shaders.RenderState;
import engine.utils.libraryBindings.opengl.shaders.ShadersProgram;
import engine.utils.libraryBindings.opengl.textures.TextureObject;
import engine.utils.libraryBindings.opengl.utils.GlBuffer;
import engine.utils.libraryBindings.opengl.utils.GlRendering;
import engine.utils.libraryBindings.opengl.utils.GlUtils;

public abstract class AbstractPostProcessor implements PostProcessor {

    private final ShadersProgram<RenderOutputData> shadersProgram;
    private FrameBufferObject fbo;

    public AbstractPostProcessor(String fragFile) throws Exception {
        this("/engine/rendering/abstracted/postprocessing/simpleVertex.glsl", fragFile);
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
    protected FrameBufferObject createFbo(int width, int height) {
        final FrameBufferObject fbo = new FrameBufferObject();
        fbo.addAttatchments(new TextureObject(width, height));
        return fbo;
    }

    protected void beforeProcess(RenderOutputData renderOutputData) {

    }

    /**
     * Returns the fbo of the post processor
     *
     * @return the fbo
     */
    protected final FrameBufferObject getFbo() {
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

        fbo.bind();
        GlUtils.clear(GlBuffer.COLOUR);
        getShadersProgram().bind();

        getShadersProgram().updatePerRenderUniforms(new RenderState<>(null, renderOutputData, null, 0));
        getShadersProgram().updatePerInstanceUniforms(new RenderState<>(null, renderOutputData, null, 0));

        draw();
        fbo.unbind();

        getShadersProgram().unbind();
    }

    @Override
    public final void resize(int width, int height) {
        fbo.resize(width, height);
    }

    @Override
    public final TextureObject getTexture() {
        return getFbo().getAttachment(0);
    }


    @Override
    public final void cleanUp() {
        getShadersProgram().delete();
    }
}
