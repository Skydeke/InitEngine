package engine.utils.libraryBindings.opengl.objects;

import engine.utils.libraryBindings.opengl.constants.DataType;
import engine.utils.libraryBindings.opengl.constants.VboAccess;
import engine.utils.libraryBindings.opengl.constants.VboTarget;
import engine.utils.libraryBindings.opengl.constants.VboUsage;
import engine.utils.libraryBindings.opengl.utils.GlConfigs;
import org.lwjgl.opengl.GL15;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Vbo implements IVbo {

    public static final Vbo NULL_ARRAY = new Vbo(0, VboTarget.ARRAY_BUFFER, VboUsage.STATIC_DRAW);
    public static final Vbo NULL_INDEX = new Vbo(0, VboTarget.ELEMENT_ARRAY_BUFFER, VboUsage.STATIC_DRAW);
    private static final Logger LOGGER = Logger.getLogger(Vbo.class.getName());
    private static int boundVbo = 0;

    private final int id;
    private final int target;
    private final int usage;

    private long size = 0;
    private boolean deleted = false;

    private DataType dt;

    private Vbo(int id, VboTarget target, VboUsage usage) {
        this.id = id;
        this.target = target.get();
        this.usage = usage.get();
        //this.bind();

//        LOGGER.log(Level.FINE, "Vbo " + id + " created");
    }

    public static Vbo create(VboTarget target, VboUsage usage) {
        final int id = GL15.glGenBuffers();
        return new Vbo(id, target, usage);
    }

    private void printReallocation(String type, int newSize) {
        LOGGER.log(Level.WARNING, "Vbo : " + id + ", reallocates " + type + " buffer again, " +
                "new limit: " + newSize + ", last limit: " + size);
    }

    /*
     *  Vbo allocations
     */

    public void allocateFloat(long size) {
        allocateData(size * DataType.FLOAT.getBytes());
        dt = DataType.FLOAT;
    }

    public void allocateInt(long size) {
        allocateData(size * DataType.INT.getBytes());
        dt = DataType.INT;
    }

    public void allocateData(long sizeInBytes) {
        bind();
        GL15.glBufferData(target, sizeInBytes, usage);
        size = sizeInBytes;
    }

    /*
     *  Vbo allocations and storing data
     */

    public void allocateFloat(FloatBuffer floatBuffer) {
        bind();
        GL15.glBufferData(target, floatBuffer, usage);
        size = (long) floatBuffer.limit() * DataType.FLOAT.getBytes();
    }

    public void allocateFloat(float[] floatArray) {
        bind();
        GL15.glBufferData(target, floatArray, usage);
        size = (long) floatArray.length * DataType.FLOAT.getBytes();
    }

    public void allocateInt(IntBuffer intBuffer) {
        bind();
        GL15.glBufferData(target, intBuffer, usage);
        size = (long) intBuffer.limit() * DataType.INT.getBytes();
    }

    public void allocateInt(int[] intArray) {
        bind();
        GL15.glBufferData(target, intArray, usage);
        size = (long) intArray.length * DataType.INT.getBytes();
    }

    public void allocateData(ByteBuffer byteBuffer) {
        bind();
        GL15.glBufferData(target, byteBuffer, usage);
        size = byteBuffer.limit();
    }

    /*
     *  Vbo storing data
     */

    public void storeData(int pointer, FloatBuffer floatBuffer) {
        bind();
        int size = floatBuffer.limit() * DataType.FLOAT.getBytes();
        dt = DataType.FLOAT;
        if (pointer + size > this.size) {
            printReallocation("float", size);
            allocateData(size);
            this.size = size;
        }
        GL15.glBufferSubData(target, pointer, floatBuffer);
    }

    public void storeData(int pointer, float[] floatArray) {
        bind();
        int size = floatArray.length * DataType.FLOAT.getBytes();
        dt = DataType.FLOAT;
        if (pointer + size > this.size) {
            printReallocation("float", size);
            allocateData(size);
            this.size = size;
        }
        GL15.glBufferSubData(target, pointer, floatArray);
    }

    public void storeData(int pointer, IntBuffer intBuffer) {
        bind();
        int size = intBuffer.limit() * DataType.INT.getBytes();
        dt = DataType.INT;
        if (pointer + size > this.size) {
            printReallocation("int", size);
            allocateData(size);
            this.size = size;
        }
        GL15.glBufferSubData(target, pointer, intBuffer);
    }

    public void storeData(int pointer, int[] intArray) {
        bind();
        int size = intArray.length * DataType.INT.getBytes();
        dt = DataType.INT;
        if (pointer + size > this.size) {
            printReallocation("int", size);
            allocateData(size);
            this.size = size;
        }
        GL15.glBufferSubData(target, pointer, intArray);
    }

    public void storeData(int pointer, ByteBuffer byteBuffer) {
        bind();
        int size = byteBuffer.limit();
        if (pointer + size > this.size) {
            printReallocation("byte", size);
            allocateData(size);
            this.size = size;
        }
        GL15.glBufferSubData(target, pointer, byteBuffer);
    }

    /*
     *  Vbo mapping
     */

    public ByteBuffer map(VboAccess access) {
        bind();
        return GL15.glMapBuffer(target, access.get());
    }

    public void unmap() {
        bind();
        GL15.glUnmapBuffer(target);
    }

    /*
     *  Vbo utilities
     */

    @Override
    public long getSize() {
        return dt != null ? size / dt.getBytes() : size;
    }

    @Override
    public void bind() {
        if (GlConfigs.CACHE_STATE || boundVbo != id) {
            GL15.glBindBuffer(target, id);
            boundVbo = id;
        }
    }

    @Override
    public void bindToVao(Vao vao) {
        vao.bind();
        GL15.glBindBuffer(target, id);
        boundVbo = id;
    }

    @Override
    public void unbind() {
        if (GlConfigs.CACHE_STATE || boundVbo != 0) {
            GL15.glBindBuffer(target, 0);
            boundVbo = 0;
        }
    }

    @Override
    public void delete() {
        if (GlConfigs.CACHE_STATE || !deleted) {
            GL15.glDeleteBuffers(id);
            deleted = true;
        }
    }

}
