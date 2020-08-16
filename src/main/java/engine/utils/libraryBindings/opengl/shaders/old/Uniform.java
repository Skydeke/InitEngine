package engine.utils.libraryBindings.opengl.shaders.old;

import engine.utils.libraryBindings.maths.joml.Matrix4f;
import engine.utils.libraryBindings.maths.joml.Vector4f;
import engine.utils.libraryBindings.maths.utils.Matrix4;
import engine.utils.libraryBindings.maths.utils.Vector4;
import engine.utils.libraryBindings.opengl.shaders.ShadersProgram;
import engine.utils.libraryBindings.opengl.utils.GlConfigs;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

public class Uniform<T> {

    private static final int NO_LOCATION = -1;

    protected final int location;
    private final ValueSetter<T> setter;
    private final UniformLoader<T> loader;

    protected T value;

    protected Uniform(int location, UniformLoader<T> loader, ValueSetter<T> setter, T value) {
        this.location = location;
        this.loader = loader;
        this.setter = setter;
        this.value = value;
    }

    public static <T> Uniform<T> create(ShadersProgram<?> shadersProgram, String uniformName,
                                        UniformLoader<T> loader, ValueSetter<T> setter, T value) {
        int location = getLocation(shadersProgram, uniformName);
        return new Uniform<>(location, loader, setter, value);
    }

    public static <T> Uniform<T> create(ShadersProgram<?> shadersProgram, String uniformName,
                                        UniformLoader<T> loader, T value) {
        return Uniform.create(shadersProgram, uniformName, loader, (a, b) -> b, value);
    }

    public static <T> Uniform<T> create(UniformLoader<T> loader) {
        return new Uniform<>(NO_LOCATION, loader, null, null);
    }

    protected static int getLocation(ShadersProgram<?> shadersProgram, String uniformName) {
        int location = shadersProgram.getUniformLocation(uniformName);
        if (location < 0) {
            System.err.println("Cannot locate uniform " + uniformName);
        }
        return location;
    }

    /**
     * Creates a mat4 uniform
     *
     * @param shadersProgram the program that the uniform is located
     * @param uniformName    the name of the uniform
     * @return the created uniform
     */
    public static Uniform<Matrix4f> createMat4(ShadersProgram<?> shadersProgram, String uniformName) {
        ValueSetter<Matrix4f> setter = (a, b) -> a.set(b);
        UniformLoader<Matrix4f> loader = (l, v) -> {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                GL20.glUniformMatrix4fv(l, false, v.get(stack.mallocFloat(16)));
            }
        };
        return Uniform.create(shadersProgram, uniformName, loader, setter, Matrix4.create().scale(0));
    }

    /**
     * Creates a vec4 uniform
     *
     * @param shadersProgram the program that the uniform is located
     * @param uniformName    the name of the uniform
     * @return the created uniform
     */
    public static Uniform<Vector4f> createVec4(ShadersProgram<?> shadersProgram, String uniformName) {
        ValueSetter<Vector4f> setter = (a, b) -> a.set(b);
        UniformLoader<Vector4f> loader = (l, v) -> GL20.glUniform4f(l, v.x, v.y, v.z, v.w);
        return Uniform.create(shadersProgram, uniformName, loader, setter, Vector4.create());
    }

    /**
     * Creates a bool uniform
     *
     * @param shadersProgram the program that the uniform is located
     * @param uniformName    the name of the uniform
     * @return the created uniform
     */
    public static Uniform<Boolean> createBool(ShadersProgram<?> shadersProgram, String uniformName) {
        UniformLoader<Boolean> loader = (l, v) -> GL20.glUniform1i(l, v ? 1 : 0);
        return Uniform.create(shadersProgram, uniformName, loader, false);
    }

    /**
     * Loads a value to the uniform
     *
     * @param newValue the new value for the uniform
     */
    public void load(T newValue) {
        if (GlConfigs.CACHE_STATE || value == null || setter == null || !value.equals(newValue)) {
            if (setter != null) {
                value = setter.set(value, newValue);
            }
            loader.load(location, newValue);
        }
    }
}
