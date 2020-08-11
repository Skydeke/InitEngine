package engine.utils.libraryBindings.opengl.shaders;

import engine.rendering.abstracted.Processable;

public interface UniformProperty<T  extends Processable> {

    void load(RenderState<T> state);

    void initialize(ShadersProgram<T> shadersProgram);

    default boolean valueAvailable() {
        return true;
    }
}
