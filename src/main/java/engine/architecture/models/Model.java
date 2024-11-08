package engine.architecture.models;

import engine.utils.libraryBindings.maths.objects.Box;
import engine.utils.libraryBindings.opengl.shaders.RenderState;

public interface Model {

    /**
     * Render the model
     *
     * @param instanceState
     */
    void render(RenderState<?> instanceState, int meshIdx);

    /**
     * Returns the level of detail of the model
     *
     * @return the level of detail
     */
    ILod getLod();

    void bindAndConfigure(int meshIdx);

    void unbind(int meshIdx);

    /**
     * Returns the bounds of the model
     *
     * @return the bounds
     */
    Box getBounds();

    /**
     * Delete the model
     */
    void delete();

    float getLowest();

    Mesh[] getMeshes();
}
