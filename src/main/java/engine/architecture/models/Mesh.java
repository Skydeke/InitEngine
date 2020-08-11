package engine.architecture.models;

import engine.rendering.abstracted.Renderable;
import engine.utils.libraryBindings.opengl.constants.DataType;
import engine.utils.libraryBindings.opengl.constants.RenderMode;
import engine.utils.libraryBindings.opengl.constants.VboUsage;
import engine.utils.libraryBindings.opengl.objects.*;
import engine.utils.libraryBindings.opengl.utils.GlRendering;

import java.util.Arrays;

public class Mesh implements Renderable {

    private float[] vert;
    private float[] tex;
    private float[] Norm;
    private int[] indicies;
    private Attribute[] attributes;

    private Vao vao;
    private Material material;

    public Mesh(float[] listToArray, float[] listToArray1, float[] listToArray2, int[] listIntToArray) {
        vert = listToArray;
        tex = listToArray1;
        Norm = listToArray2;
        indicies = listIntToArray;

        final IndexBuffer indexBuffer = engine.utils.libraryBindings.opengl.objects.BufferUtils.loadToIndexBuffer(VboUsage.STATIC_DRAW, indicies);
        float[] data = connectData(vert.length / 3, vert, tex, Norm);
        DataBuffer buffer = engine.utils.libraryBindings.opengl.objects.BufferUtils.loadToDataBuffer(VboUsage.STATIC_DRAW, data);
        vao = Vao.create();
        vao.bind();
        vao.loadIndexBuffer(indexBuffer);
        vao.loadDataBuffer(buffer, Attribute.ofPositions(), Attribute.ofTexCoords(), Attribute.ofNormals());
        vao.unbind();

        attributes = new Attribute[]{Attribute.ofPositions(), Attribute.ofTexCoords(), Attribute.ofNormals()};
    }

    public Mesh(float[] listToArray, float[] listToArray1, int[] listIntToArray) {
        vert = listToArray;
        tex = listToArray1;
        indicies = listIntToArray;

        final IndexBuffer indexBuffer = engine.utils.libraryBindings.opengl.objects.BufferUtils.loadToIndexBuffer(VboUsage.STATIC_DRAW, indicies);
        float[] data = connectData((int) indexBuffer.getSize(), vert, tex);
        DataBuffer buffer = engine.utils.libraryBindings.opengl.objects.BufferUtils.loadToDataBuffer(VboUsage.STATIC_DRAW, data);
        vao = Vao.create();
        vao.bind();
        vao.loadIndexBuffer(indexBuffer);
        vao.loadDataBuffer(buffer, Attribute.ofPositions(), Attribute.ofTexCoords());
        vao.unbind();
        attributes = new Attribute[]{Attribute.ofPositions(), Attribute.ofTexCoords()};

        System.out.println(Arrays.toString(data));
        System.out.println(Arrays.toString(indicies));
    }

    public Mesh(Vao vao) {
        this.vao = vao;
    }

    private static float[] connectData(int vertexCount, float[]... data) {
        int totalComponentLenght = 0;
        int[] pointerByIndex = new int[data.length];
        int[] componentLenghtByIndex = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            float[] array = data[i];
            totalComponentLenght += array.length / vertexCount;
            componentLenghtByIndex[i] = array.length / vertexCount;
        }
        float[] connectedData = new float[totalComponentLenght * vertexCount];
        int dataIndex = 0;
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            for (int i = 0; i < data.length; i++) {
                for (int c = 0; c < componentLenghtByIndex[i]; c++) {
                    connectedData[dataIndex++] = data[i][pointerByIndex[i]];
                    pointerByIndex[i]++;
                }
            }
        }
        return connectedData;
    }

    public float getLowest() {
        float ret = Float.MAX_VALUE;
        for (int i = 1; i < vert.length; i = i + attributes[0].getComponentCount()) {
            if (vert[i] < ret) ret = vert[i];
        }
        return ret;
    }

    public void preconfigureRendering() {
        if (material != null){
            material.preconfigure();
        }
    }

    @Override
    public void bind(ILod lod){
        if (lod.available()) {
            IVbo indexBuffer = lod.current();
            vao.loadIndexBuffer(indexBuffer, false);
        }
        vao.bind();
        vao.enableAttributes();
    }

    @Override
    public void render(RenderMode renderMode) {
        if (vao.hasIndices()) {
            GlRendering.drawElements(renderMode,
                    vao.getIndexCount(), DataType.U_INT, 0);
        } else {
            GlRendering.drawArrays(renderMode, 0, vao.getIndexCount());
        }
    }

    public void unbind(){
        vao.unbind();
    }

    public Model getModel() {
        return null;
    }

    public void delete(boolean b) {
        vao.delete(b);
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
