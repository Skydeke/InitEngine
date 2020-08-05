package engine.architecture.models;

import engine.utils.libraryWrappers.maths.objects.Box;
import engine.utils.libraryWrappers.opengl.constants.DataType;
import engine.utils.libraryWrappers.opengl.constants.RenderMode;
import engine.utils.libraryWrappers.opengl.constants.VboUsage;
import engine.utils.libraryWrappers.opengl.objects.*;
import engine.utils.libraryWrappers.opengl.shaders.RenderState;
import engine.utils.libraryWrappers.opengl.utils.GlRendering;

public class ModifiableModel implements Model {

    private final Vao vao;
    private final DataBuffer dataBuffer;
    private final IndexBuffer indexBuffer;

    private final Lod lod = new Lod();
    private RenderMode renderMode = RenderMode.TRIANGLES;
    private Box bounds = Box.NONE;

    public ModifiableModel(Attribute... attributes) {
        this.vao = Vao.create();
        this.dataBuffer = new DataBuffer(VboUsage.STATIC_DRAW);
        this.indexBuffer = new IndexBuffer(VboUsage.STATIC_DRAW);

        this.vao.loadIndexBuffer(indexBuffer);
        this.vao.loadDataBuffer(dataBuffer, attributes);
    }

    public void setIndices(int[] indices) {
        this.indexBuffer.allocateInt(indices.length);
        this.indexBuffer.storeData(0, indices);
    }

    public void setData(float[] data) {
        this.dataBuffer.allocateFloat(data.length);
        this.dataBuffer.storeData(0, data);
    }

    public void setRenderMode(RenderMode renderMode) {
        this.renderMode = renderMode;
    }

    @Override
    public void render(RenderState<?> instanceState, int meshIdx) {
        if (vao.hasIndices()) {
            GlRendering.drawElements(renderMode,
                    vao.getIndexCount(), DataType.U_INT, 0);
        } else {
            GlRendering.drawArrays(renderMode, 0, vao.getIndexCount());
        }
    }

    @Override
    public void bindAndConfigure(int meshIdx) {
        if (getLod().available()) {
            IVbo indexBuffer = getLod().current();
            vao.loadIndexBuffer(indexBuffer, false);
        }
        vao.bind();
        vao.enableAttributes();
    }

    @Override
    public void unbind(int meshIdx) {
        vao.unbind();
    }

    @Override
    public Lod getLod() {
        return lod;
    }

    @Override
    public Box getBounds() {
        return bounds;
    }

    public void setBounds(Box bounds) {
        this.bounds = bounds;
    }

    @Override
    public void delete() {
        this.vao.delete(true);
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
