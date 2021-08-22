package engine.rendering.instances.camera;

import engine.architecture.componentsystem.GameComponent;
import engine.architecture.scene.SceneContext;
import engine.architecture.system.AppContext;
import engine.architecture.system.GameState;
import engine.rendering.abstracted.camera.CameraProjection;
import engine.utils.libraryBindings.maths.Angle;
import engine.utils.libraryBindings.maths.joml.Matrix4f;
import engine.utils.libraryBindings.maths.joml.Matrix4fc;
import engine.utils.libraryBindings.maths.utils.Matrix4;
import engine.utils.property.FloatProperty;
import lombok.Getter;

public class Camera extends GameComponent {

    private final Matrix4f projectionViewMatrix = Matrix4.createIdentity();
    private final Matrix4f projectionMatrix = Matrix4.createIdentity();
    private final Matrix4f viewMatrix = Matrix4.createIdentity();

    private final FloatProperty nearPlaneProperty = new FloatProperty(0.01f);
    private final FloatProperty farPlaneProperty = new FloatProperty(10000.0f);
    private final FloatProperty fovProperty = new FloatProperty((float) Math.toRadians(85f));

    private CameraController controller = CameraController.NONE;
    private CameraProjection projection = new PerspectiveProjection();

    @Getter
    private final SceneContext context;

    public Camera(SceneContext context) {
        this.context = context;
        getTransform().setRotation(Angle.degrees(0), Angle.degrees(0), Angle.degrees(0));
    }

    public void reflect(float h) {
        final float height = getTransform().getPosition().y - h;
        getTransform().getPosition().y -= height * 2;
        getTransform().getRotation().x *= -1;
        getTransform().getRotation().z *= -1;
    }

    public void reflect() {
        getTransform().getPosition().y *= -1;
        getTransform().getRotation().x *= -1;
        getTransform().getRotation().z *= -1;
    }

    public void update() {
        if (GameState.getCurrent() == GameState.GAME) {
            controller.control(this);
        }
        AppContext.instance().getSceneContext().getScene()
                .getSky().getTransform().setPosition(
                        getTransform().getPosition().x, getTransform().getPosition().y, getTransform().getPosition().z);

        updateProjectionMatrix();
        updateViewMatrix();
        updateProjectionViewMatrix();
    }

    public void setController(CameraController controller) {
        this.controller = controller == null ? CameraController.NONE : controller;
    }

    public void setProjection(CameraProjection projection) {
        this.projection = projection;
        updateProjectionMatrix();
    }


    /* ========= PLANES and FOV ========= */

    /**
     * Returns the near plane property of the camera
     *
     * @return the near plane property of the camera
     */
    public FloatProperty nearPlaneProperty() {
        return nearPlaneProperty;
    }

    public float getNearPlane() {
        return nearPlaneProperty().getValue();
    }

    public void setNearPlane(float nearPlane) {
        nearPlaneProperty().setValue(nearPlane);
    }

    /**
     * Returns the far plane property of the camera
     *
     * @return the far plane property of the camera
     */
    public FloatProperty farPlaneProperty() {
        return farPlaneProperty;
    }

    public float getFarPlane() {
        return farPlaneProperty.getValue();
    }

    public void setFarPlane(float farPlane) {
        farPlaneProperty().setValue(farPlane);
    }

    /**
     * Returns the field of view property of the camera
     *
     * @return the field of view property of the camera
     */
    public FloatProperty fovProperty() {
        return fovProperty;
    }

    public float getFov() {
        return fovProperty().getValue();
    }

    public void setFov(float fov) {
        fovProperty().setValue(fov);
    }

    /* ========= MATRICES ========= */

    public void updateViewMatrix() {
        Matrix4.ofView(getTransform().getPosition(), getTransform().getRotation(), viewMatrix);
    }

    public void updateProjectionMatrix() {
        final Matrix4f matrix = projection.getProjectionMatrix(this);
        projectionMatrix.set(matrix);
    }

    public void updateProjectionViewMatrix() {
        getProjectionMatrix().mul(getViewMatrix(), projectionViewMatrix);
    }

    public Matrix4fc getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4fc getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4fc getProjectionViewMatrix() {
        return projectionViewMatrix;
    }
}
