package engine.architecture.models;

import engine.utils.libraryBindings.maths.objects.Box;
import engine.utils.libraryBindings.opengl.constants.RenderMode;
import engine.utils.libraryBindings.opengl.objects.IVbo;
import engine.utils.libraryBindings.opengl.shaders.RenderState;

public class SimpleModel implements Model {

    private final Mesh[] m;
    private final RenderMode renderMode;

    private final Lod lod;
    private final Box bounds;

    public SimpleModel(Mesh[] m, RenderMode renderMode) {
        this(m, renderMode, Box.of(0, 0, 0));
    }

    public SimpleModel(Mesh[] m, RenderMode renderMode, IVbo... indexBuffers) {
        this(m, renderMode, Box.of(0, 0, 0), indexBuffers);
    }

    public SimpleModel(Mesh[] m, RenderMode renderMode, Box bounds) {
        if (m.length < 1)
            throw new IllegalStateException();
        this.m = m;
        this.renderMode = renderMode;
        this.lod = new Lod();
        this.bounds = bounds;
    }

    public SimpleModel(Mesh[] m, RenderMode renderMode, Box bounds, IVbo... indexBuffers) {
        if (m.length < 1)
            throw new IllegalStateException();
        this.m = m;
        this.renderMode = renderMode;
        this.lod = new Lod(indexBuffers);
        this.bounds = bounds;
    }

    @Override
    public void bindAndConfigure(int meshIdx){
        m[meshIdx].preconfigureRendering();
        m[meshIdx].bind(getLod());
    }

    @Override
    public void render(RenderState state, int meshIdx) {
        m[meshIdx].render(renderMode);
    }

    @Override
    public void unbind(int meshIdx){
        m[meshIdx].unbind();
    }

    public float getLowest() {
        float lowest = 0;
        for (Mesh m : m) {
            if (lowest > m.getLowest()) {
                lowest = m.getLowest();
            }
        }
        return lowest;
    }

    @Override
    public Mesh[] getMeshes() {
        return m;
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
        for (Mesh m : m) {
            m.delete(true);
        }
    }

}
