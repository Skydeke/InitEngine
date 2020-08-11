package engine.rendering.instances.renderers.generic;

import engine.architecture.scene.light.Light;
import engine.architecture.scene.node.Node;
import engine.rendering.Shader;
import engine.rendering.instances.camera.Camera;
import engine.utils.libraryBindings.maths.joml.Matrix4f;

public class LightOverlayShader extends Shader {

    private static LightOverlayShader instance;

    private LightOverlayShader() {
        super();
        createVertexShader("res/shaders/overlay/overlay_vs.glsl");
        createFragmentShader("res/shaders/overlay/overlay_fs.glsl");
        link();

        addUniform("color");
        addUniform("projectionMatrix");
        addUniform("modelMatrix");
        addUniform("viewMatrix");
    }

    public static LightOverlayShader instance() {
        if (instance == null)
            instance = new LightOverlayShader();
        return instance;
    }

    @Override
    public void updateUniforms(Node node) {
        Light light = (Light) node;
        Camera camera = boundContext.getCamera();
        setUniform("projectionMatrix", new Matrix4f(camera.getProjectionMatrix()));
        setUniform("modelMatrix", light.getTransform().getTransformationMatrix());
        setUniform("viewMatrix", new Matrix4f(camera.getViewMatrix()));
        setUniform("color", light.getColor());
    }
}
