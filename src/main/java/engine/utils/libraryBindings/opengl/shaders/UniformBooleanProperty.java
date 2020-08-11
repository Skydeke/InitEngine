package engine.utils.libraryBindings.opengl.shaders;

import engine.rendering.abstracted.Processable;
import org.lwjgl.opengl.GL20;

public abstract class UniformBooleanProperty<T extends Processable> extends AbstractUniformProperty<T> {

    protected UniformBooleanProperty(String name) {
        super(name);
    }

    @Override
    public void load(RenderState<T> state) {
        if (valueAvailable()) {
            final boolean value = getUniformValue(state);
            GL20.glUniform1i(getLocation(), value ? 1 : 0);
        }
    }

    public abstract boolean getUniformValue(RenderState<T> state);
}
