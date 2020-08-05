package engine.utils.libraryWrappers.opengl.shaders;

import engine.utils.libraryWrappers.opengl.textures.TextureObject;
import org.lwjgl.opengl.GL20;

public abstract class UniformTextureProperty<T> implements UniformProperty<T> {

    private final String name;
    private final int unit;

    public UniformTextureProperty(String name, int unit) {
        this.name = name;
        this.unit = unit;
    }

    @Override
    public void load(RenderState<T> state) {
        if (valueAvailable()) {
            TextureObject.bind(getUniformValue(state), unit);
        }
    }

    @Override
    public void initialize(ShadersProgram<T> shadersProgram) {
        if (name.charAt(0) != '#') {
            GL20.glUniform1i(shadersProgram.getUniformLocation(name), unit);
        }
    }

    public abstract TextureObject getUniformValue(RenderState<T> state);
}
