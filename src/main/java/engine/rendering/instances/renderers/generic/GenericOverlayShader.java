package engine.rendering.instances.renderers.generic;

import engine.architecture.scene.node.Node;
import engine.rendering.Shader;
import engine.rendering.instances.camera.Camera;
import engine.utils.libraryWrappers.maths.joml.Matrix4f;

public class GenericOverlayShader extends Shader {

    private static GenericOverlayShader instance;

    private GenericOverlayShader() {
        super();
        createVertexShader("res/shaders/overlay/outline_vs.glsl");
        createFragmentShader("res/shaders/overlay/outline_fs.glsl");
        link();

        addUniform("projectionMatrix");
        addUniform("modelMatrix");
        addUniform("viewMatrix");
    }

    public static GenericOverlayShader instance() {
        if (instance == null)
            instance = new GenericOverlayShader();
        return instance;
    }

    @Override
    public void updateUniforms(Node node) {

        Camera camera = boundContext.getCamera();
        setUniform("projectionMatrix", new Matrix4f(camera.getProjectionMatrix()));
        setUniform("modelMatrix", node.getTransform().getTransformationMatrix());
        setUniform("viewMatrix", new Matrix4f(camera.getViewMatrix()));
    }

}
