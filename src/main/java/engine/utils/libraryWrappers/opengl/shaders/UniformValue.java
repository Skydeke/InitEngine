package engine.utils.libraryWrappers.opengl.shaders;

import engine.utils.libraryWrappers.opengl.utils.MemoryUtils;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

public interface UniformValue {

    FloatBuffer FLOAT_BUFFER = MemoryUtils.allocFloat(16);
    DoubleBuffer DOUBLE_BUFFER = MemoryUtils.allocDouble(16);

    void load(int location);

}
