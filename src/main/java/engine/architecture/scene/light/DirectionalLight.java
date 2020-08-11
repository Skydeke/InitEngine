package engine.architecture.scene.light;

import engine.architecture.system.AppContext;
import engine.utils.libraryBindings.maths.joml.Matrix4f;
import engine.utils.libraryBindings.maths.joml.Vector3f;
import lombok.Getter;
import lombok.Setter;

public class DirectionalLight extends Light {

    @Getter
    @Setter
    private Vector3f ambientLight;

    @Getter
    private Matrix4f lightProjection;
    @Getter
    private Matrix4f lightView;

    private float size = 100f;

    public DirectionalLight() {
        super();
        lightProjection = new Matrix4f().ortho(
                -size, size, -size, size, -size, size
        );
        lightView = new Matrix4f().lookAt(
                0f, 5f, -5f,
                0, 0, 0,
                0, 1, 0
        );
        this.ambientLight = new Vector3f(0.01f);
    }

    @Override
    public void update() {
        super.update();
        Vector3f rot = new Vector3f(getTransform().getEulerAngles()).normalize();
        lightView = new Matrix4f().lookAt(
                rot, getTransform().getPosition(),
                new Vector3f(0, 1, 0)
        );
        lightView.translate(-AppContext.instance().getSceneContext().getCamera().getTransform().getPosition().x,
                0,
                AppContext.instance().getSceneContext().getCamera().getTransform().getPosition().z);
    }

    public Matrix4f getLightSpaceMatrix() {
        return new Matrix4f(lightProjection).mul(lightView);
    }

}
