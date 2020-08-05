package engine.architecture.models;

import engine.utils.libraryWrappers.maths.objects.Box;
import engine.utils.libraryWrappers.opengl.shaders.RenderState;
import engine.utils.libraryWrappers.opengl.shaders.ShadersProgram;

public interface Model {

    /**
     * Render the model
     *
     * @param instanceState
     */
    void render(RenderState<?> instanceState, ShadersProgram program);

    /**
     * Returns the level of detail of the model
     *
     * @return the level of detail
     */
    ILod getLod();

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

    Mesh getMesh(int i);
}
