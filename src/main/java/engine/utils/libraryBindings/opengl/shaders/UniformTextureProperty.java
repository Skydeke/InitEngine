package engine.utils.libraryBindings.opengl.shaders;

import engine.rendering.abstracted.Processable;
import engine.utils.libraryBindings.opengl.textures.ITexture;
import engine.utils.libraryBindings.opengl.textures.Texture;
import org.lwjgl.opengl.GL20;

public abstract class UniformTextureProperty<T extends Processable> implements UniformProperty<T> {

    private final String name;
    private final int unit;

    public UniformTextureProperty(String name, int unit) {
        this.name = name;
        this.unit = unit;
    }

    @Override
    public void load(RenderState<T> state) {
        if (valueAvailable()) {
            Texture.bind(getUniformValue(state), unit);
        }
    }

    @Override
    public void initialize(ShadersProgram<T> shadersProgram) {
        if (name.charAt(0) != '#') {
            GL20.glUniform1i(shadersProgram.getUniformLocation(name), unit);
        }
    }

    public abstract ITexture getUniformValue(RenderState<T> state);
}
