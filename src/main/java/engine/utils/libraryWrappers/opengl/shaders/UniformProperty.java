package engine.utils.libraryWrappers.opengl.shaders;

import engine.rendering.abstracted.Renderable;

public interface UniformProperty<T  extends Renderable> {

    void load(RenderState<T> state);

    void initialize(ShadersProgram<T> shadersProgram);

    default boolean valueAvailable() {
        return true;
    }
}
