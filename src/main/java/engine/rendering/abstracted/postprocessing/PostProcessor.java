package engine.rendering.abstracted.postprocessing;

import engine.rendering.RenderOutputData;
import engine.utils.libraryBindings.opengl.fbos.Fbo;
import engine.utils.libraryBindings.opengl.textures.ITexture;

public interface PostProcessor {

    /**
     * Process the given data and return the new data
     *
     * @param renderOutputData the data
     */
    void process(RenderOutputData renderOutputData);

    /**
     * Returns the processed colour texture
     *
     * @return the processed colour texture
     */
    ITexture getTexture();

    /**
     * Resize the fbo, should be called every
     * time the size of the window changed
     *
     * @param width  the width
     * @param height the height
     */
    void resize(int width, int height);

    /**
     * Clean up the post processor
     */
    void cleanUp();

    void blitToFbo(Fbo fbo);

    void blitToScreen();
}
