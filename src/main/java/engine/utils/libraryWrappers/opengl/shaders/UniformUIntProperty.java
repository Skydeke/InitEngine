package engine.utils.libraryWrappers.opengl.shaders;

import engine.rendering.abstracted.Processable;
import org.lwjgl.opengl.GL30;

public abstract class UniformUIntProperty<T extends Processable> extends AbstractUniformProperty<T> {

    protected UniformUIntProperty(String name) {
        super(name);
    }

    @Override
    public void load(RenderState<T> state) {
        if (valueAvailable()) {
            final int value = getUniformValue(state);
            GL30.glUniform1ui(getLocation(), value);
        }
    }

    public abstract int getUniformValue(RenderState<T> state);

}
