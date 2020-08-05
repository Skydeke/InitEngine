package engine.utils.libraryWrappers.opengl.shaders;

import engine.rendering.abstracted.Renderable;

public abstract class UniformValueProperty<T extends Renderable> extends AbstractUniformProperty<T> {

    protected UniformValueProperty(String name) {
        super(name);
    }

    @Override
    public void load(RenderState<T> state) {
        if (valueAvailable()) {
            final UniformValue value = getUniformValue(state);
            value.load(getLocation());
        }
    }

    public abstract UniformValue getUniformValue(RenderState<T> state);

}
