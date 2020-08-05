package engine.utils.libraryWrappers.opengl.utils;

import org.lwjgl.opengl.GL11;

public enum GlBuffer {

    COLOUR(GL11.GL_COLOR_BUFFER_BIT),
    DEPTH(GL11.GL_DEPTH_BUFFER_BIT),
    STENCIL(GL11.GL_STENCIL_BUFFER_BIT),
    ;

    private final int value;

    GlBuffer(int value) {
        this.value = value;
    }

    public static int getValue(GlBuffer... buffers) {
        int buffersValue = 0;
        for (GlBuffer buffer : buffers) {
            buffersValue |= buffer.get();
        }
        return buffersValue;
    }

    public int get() {
        return value;
    }
}
