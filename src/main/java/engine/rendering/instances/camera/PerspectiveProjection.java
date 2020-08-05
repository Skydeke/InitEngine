package engine.rendering.instances.camera;

import engine.rendering.abstracted.camera.CameraProjection;
import engine.utils.libraryWrappers.maths.joml.Matrix4f;
import engine.utils.libraryWrappers.maths.utils.Matrix4;

public class PerspectiveProjection implements CameraProjection {

    private final Matrix4f matrix = Matrix4.create();

    @Override
    public Matrix4f getProjectionMatrix(Camera camera) {
        final int width = camera.getContext().getResolution().x;
        final int height = camera.getContext().getResolution().y;
        return Matrix4.ofProjection(camera.getFov(), width, height,
                camera.getNearPlane(), camera.getFarPlane(), matrix).scale(1, 1, 1, matrix);
    }
}
