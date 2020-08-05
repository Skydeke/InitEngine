package engine.architecture.models.generators;

import engine.architecture.models.Mesh;
import engine.architecture.models.Model;
import engine.architecture.models.SimpleModel;
import engine.utils.libraryWrappers.maths.joml.Vector3f;
import engine.utils.libraryWrappers.maths.objects.Triangle;
import engine.utils.libraryWrappers.maths.utils.Maths;
import engine.utils.libraryWrappers.maths.utils.Vector2;
import engine.utils.libraryWrappers.maths.utils.Vector3;
import engine.utils.libraryWrappers.opengl.constants.RenderMode;
import engine.utils.libraryWrappers.opengl.constants.VboUsage;
import engine.utils.libraryWrappers.opengl.objects.*;
import engine.utils.libraryWrappers.opengl.utils.MemoryUtils;

import java.nio.FloatBuffer;

public class TriangleGenerator implements IModelGenerator {

    private static final TriangleGenerator instance = new TriangleGenerator(createTriangle());

    private final Triangle triangle;

    public TriangleGenerator(Triangle triangle) {
        this.triangle = triangle;
    }

    private static Triangle createTriangle() {
        final float sin60 = Maths.sinf(Maths.PI / 3);
        final float cos60 = Maths.cosf(Maths.PI / 3);
        final Vector3f p2 = Vector3.of(+sin60, 0, -cos60);
        final Vector3f p3 = Vector3.of(-sin60, 0, -cos60);
        return new Triangle(Vector3.of(0, 0, 1), p2, p3);
    }

    public static TriangleGenerator getInstance() {
        return TriangleGenerator.instance;
    }

    @Override
    public Vao createVao() {
        final Vao vao = Vao.create();
        vao.loadIndexBuffer(createIndexVbo());
        vao.loadDataBuffer(createDataVbo(), Attribute.ofPositions(),
                Attribute.ofTexCoords(), Attribute.ofNormals());
        return vao;
    }

    @Override
    public DataBuffer createDataVbo() {
        final FloatBuffer buffer = MemoryUtils.allocFloat(24);

        triangle.getP1().get(buffer).position(buffer.position() + 3);
        Vector2.of(.5f, 0).get(buffer).position(buffer.position() + 2);
        Vector3.upward().get(buffer).position(buffer.position() + 3);

        triangle.getP2().get(buffer).position(buffer.position() + 3);
        Vector2.of(1, 1).get(buffer).position(buffer.position() + 2);
        Vector3.upward().get(buffer).position(buffer.position() + 3);

        triangle.getP3().get(buffer).position(buffer.position() + 3);
        Vector2.of(0, 1).get(buffer).position(buffer.position() + 2);
        Vector3.upward().get(buffer).position(buffer.position() + 3);

        buffer.flip();

        return BufferUtils.loadToDataBuffer(VboUsage.STATIC_DRAW, buffer);
    }

    @Override
    public IndexBuffer createIndexVbo() {
        return BufferUtils.loadToIndexBuffer(VboUsage.STATIC_DRAW, 0, 1, 2);
    }

    @Override
    public Model generateModel() {
        Mesh[] m = {new Mesh(createVao())};
        return new SimpleModel(m, RenderMode.TRIANGLES);
    }
}
