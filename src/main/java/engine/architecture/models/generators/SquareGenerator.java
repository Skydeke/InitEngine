package engine.architecture.models.generators;

import engine.architecture.models.Mesh;
import engine.architecture.models.Model;
import engine.architecture.models.SimpleModel;
import engine.utils.libraryWrappers.opengl.constants.DataType;
import engine.utils.libraryWrappers.opengl.constants.RenderMode;
import engine.utils.libraryWrappers.opengl.constants.VboUsage;
import engine.utils.libraryWrappers.opengl.objects.*;

public class SquareGenerator implements IModelGenerator {

    private static final SquareGenerator instance = new SquareGenerator();

    private static final int[] indices = {0, 1, 2, 3};// Second Traingle

    //    private static final float[] POSITIONS = {0f, 1f, 0f, 0f, 1f, 1f, 1f, 0f};
    private static final float[] POSITIONS = {
            0f, 1f,
            0f, 0f,
            1f, 1f,
            1f, 0f};

    public static SquareGenerator getInstance() {
        return SquareGenerator.instance;
    }


    @Override
    public Vao createVao() {
        final Vao vao = Vao.create();
        vao.loadIndexBuffer(createIndexVbo());
        vao.loadDataBuffer(createDataVbo(), Attribute.of(0, 2, DataType.FLOAT, false));
        vao.unbind();
        return vao;
    }

    @Override
    public DataBuffer createDataVbo() {
        return BufferUtils.loadToDataBuffer(VboUsage.STATIC_DRAW, POSITIONS);
    }

    @Override
    public IndexBuffer createIndexVbo() {
        return BufferUtils.loadToIndexBuffer(VboUsage.STATIC_DRAW, indices);
    }

    @Override
    public Model generateModel() {
        return new SimpleModel(new Mesh[]{new Mesh(createVao())}, RenderMode.TRIANGLE_STRIP);
    }
}
