package engine.rendering.instances.camera;

import engine.architecture.scene.SceneContext;
import engine.architecture.scene.node.Node;
import engine.architecture.system.GameState;
import engine.architecture.ui.element.UIElement;
import engine.architecture.ui.element.layout.Box;
import engine.architecture.ui.event.mouse.MouseClickEvent;
import engine.rendering.abstracted.camera.CameraProjection;
import engine.utils.libraryWrappers.maths.Angle;
import engine.utils.libraryWrappers.maths.joml.Matrix4f;
import engine.utils.libraryWrappers.maths.joml.Matrix4fc;
import engine.utils.libraryWrappers.maths.joml.Vector2f;
import engine.utils.libraryWrappers.maths.joml.Vector2i;
import engine.utils.libraryWrappers.maths.objects.Transform;
import engine.utils.libraryWrappers.maths.utils.Matrix4;
import engine.utils.property.FloatProperty;
import lombok.Getter;

import static engine.architecture.ui.event.mouse.MouseClickEvent.BUTTON_CLICK;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class Camera extends UIElement {

    private final Matrix4f projectionViewMatrix = Matrix4.createIdentity();
    private final Matrix4f projectionMatrix = Matrix4.createIdentity();
    private final Matrix4f viewMatrix = Matrix4.createIdentity();

    private FloatProperty nearPlaneProperty = new FloatProperty(0.01f);
    private FloatProperty farPlaneProperty = new FloatProperty(10000.0f);
    private FloatProperty fovProperty = new FloatProperty((float) Math.toRadians(85f));

    private CameraController controller = CameraController.NONE;
    private CameraProjection projection = new PerspectiveProjection();

    private Transform transform;

    private boolean enabled = true;
    @Getter
    private SceneContext context;

    public Camera(SceneContext context) {
        this.context = context;
        this.transform = new Transform();
        getTransform().setRotation(Angle.degrees(0), Angle.degrees(0), Angle.degrees(0));
        onEvent(e -> {
            if (e instanceof MouseClickEvent) {
                MouseClickEvent m = (MouseClickEvent) e;

                if (m.getAction() == BUTTON_CLICK) {
                    // PICKING
                    if (m.getKey() == GLFW_MOUSE_BUTTON_LEFT) {
                        Node selected = pick(m.getScreenPos());
                        if (selected != null) {
                            if (selected.isSelected())
                                if ((m.getMods() & GLFW_MOD_CONTROL) == 0)
                                    context.getSelectionManager().clear();
                                else context.getSelectionManager().remove(selected);
                            else {
                                if ((m.getMods() & GLFW_MOD_CONTROL) == 0)
                                    context.getSelectionManager().clear();
                                context.getSelectionManager().addSelection(selected);
                            }
                            e.consume();
                        }
                    }
                }
            }
            if (!e.isConsumed())
                controller.handle(e);
        });
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

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /* ========= TRANSFORM ========= */

    public Transform getTransform() {
        return transform;
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
        Matrix4.ofView(transform.getPosition(), transform.getRotation(), viewMatrix);
    }

    public void updateProjectionMatrix() {
        final Matrix4f matrix = projection.getProjectionMatrix(this);
        projectionMatrix.set(matrix);
    }

    public void updateProjectionViewMatrix() {
        getProjectionMatrix().mul(getViewMatrix(), projectionViewMatrix);
    }

    public Matrix4fc getViewMatrix() {
        return enabled ? viewMatrix : Matrix4.pool.poolAndGive().identity();
    }

    public Matrix4fc getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4fc getProjectionViewMatrix() {
        return projectionViewMatrix;
    }

    private Node pick(Vector2f screenpos) {
        Box sceneBox = context.getParent().getAbsoluteBox();
        Vector2f pos = sceneBox.within(screenpos);
        Vector2i resolution = sceneBox.resolution();

        Vector2i point = new Vector2i((int) (pos.x * resolution.x), (int) ((1 - pos.y) * resolution.y));
        return context.getPicking().pick(point.x, point.y);
    }
}
