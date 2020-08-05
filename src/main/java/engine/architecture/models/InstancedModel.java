package engine.architecture.models;

import engine.utils.libraryWrappers.maths.objects.Box;
import engine.utils.libraryWrappers.opengl.constants.DataType;
import engine.utils.libraryWrappers.opengl.constants.RenderMode;
import engine.utils.libraryWrappers.opengl.objects.Vao;
import engine.utils.libraryWrappers.opengl.objects.Vbo;
import engine.utils.libraryWrappers.opengl.shaders.RenderState;
import engine.utils.libraryWrappers.opengl.utils.GlRendering;

public class InstancedModel implements Model {

    private final Vao vao;
    private final RenderMode renderMode;
    private final int vertices;

    private final Lod lod;
    private final Box bounds;

    private int instances;

    public InstancedModel(Vao vao, RenderMode renderMode, int vertices) {
        this(vao, renderMode, vertices, Box.of(0, 0, 0));
    }

    public InstancedModel(Vao vao, RenderMode renderMode, int vertices, Box bounds) {
        this.vao = vao;
        this.renderMode = renderMode;
        this.vertices = vertices;
        this.bounds = bounds;
        this.lod = new Lod();
    }

    public InstancedModel(Vao vao, RenderMode renderMode, int vertices, Vbo... indexBuffers) {
        this(vao, renderMode, vertices, Box.of(0, 0, 0), indexBuffers);
    }

    public InstancedModel(Vao vao, RenderMode renderMode, int vertices, Box bounds, Vbo... indexBuffers) {
        this.vao = vao;
        this.renderMode = renderMode;
        this.vertices = vertices;
        this.bounds = bounds;
        this.lod = new Lod(indexBuffers);
    }

    public void setInstances(int instances) {
        this.instances = instances;
    }

    @Override
    public void render(RenderState<?> instanceState, int meshIdx) {
        if (this.vao.hasIndices()) {
            GlRendering.drawElementsInstanced(renderMode,
                    vertices, DataType.U_INT, 0, instances);
        } else {
            GlRendering.drawArraysInstanced(renderMode, 0,
                    vertices, instances);
        }
    }

    @Override
    public void bindAndConfigure(int meshIdx) {
        this.vao.bind();
        this.vao.enableAttributes();
    }

    @Override
    public void unbind(int meshIdx) {
        this.vao.unbind();
    }

    @Override
    public Lod getLod() {
        return lod;
    }

    @Override
    public Box getBounds() {
        return bounds;
    }

    @Override
    public void delete() {
        vao.delete(true);
    }

    @Override
    public float getLowest() {
        return 0;
    }

    @Override
    public Mesh[] getMeshes() {
        return null;
    }
}
