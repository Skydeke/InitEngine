package engine.architecture.models.generators;

import engine.architecture.models.Mesh;
import engine.architecture.models.Model;
import engine.architecture.models.SimpleModel;
import engine.utils.libraryWrappers.opengl.constants.DataType;
import engine.utils.libraryWrappers.opengl.constants.RenderMode;
import engine.utils.libraryWrappers.opengl.constants.VboUsage;
import engine.utils.libraryWrappers.opengl.objects.*;

public class LineGenerator implements IModelGenerator {

    private static final LineGenerator instance = new LineGenerator(0, 0, 1, 1);

    private final float[] array;

    public LineGenerator(float x1, float y1, float x2, float y2) {
        this.array = new float[]{x1, y1, x2, y2};
    }

    public static LineGenerator getInstance() {
        return LineGenerator.instance;
    }

    @Override
    public Vao createVao() {
        final Vao vao = Vao.create();
        vao.loadDataBuffer(createDataVbo(),
                Attribute.of(0, 2, DataType.FLOAT, false));
        return vao;
    }

    @Override
    public DataBuffer createDataVbo() {
        return BufferUtils.loadToDataBuffer(VboUsage.STATIC_DRAW, array);
    }

    @Override
    public IndexBuffer createIndexVbo() {
        return IndexBuffer.NULL;
    }

    @Override
    public Model generateModel() {
        Mesh[] m = {new Mesh(createVao())};
        return new SimpleModel(m, RenderMode.LINES);
    }
}
