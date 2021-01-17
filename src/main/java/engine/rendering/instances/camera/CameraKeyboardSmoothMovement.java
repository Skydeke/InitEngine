package engine.rendering.instances.camera;

import engine.architecture.system.Time;
import engine.utils.libraryBindings.maths.joml.Vector3f;
import engine.utils.libraryBindings.maths.utils.Vector3;

public class CameraKeyboardSmoothMovement extends CameraController {

    private final Vector3f acceleration = Vector3.of(1);
    private final Vector3f maxVelocity = Vector3.of(100);
    private final Vector3f velocity = Vector3.create();

    @Override
    public void control(Camera camera) {
        Vector3f deltaAcceleration = Vector3.create();
//        onEvent(e -> {
//                    if (e instanceof KeyboardEvent) {
//                        switch (((KeyboardEvent) e).getKey()) {
//                            case GLFW.GLFW_KEY_W:
//                                deltaAcceleration.add(0, 0, -acceleration.z);
//                                break;
//                            case GLFW.GLFW_KEY_A:
//                                deltaAcceleration.add(-acceleration.x, 0, 0);
//                                break;
//                            case GLFW.GLFW_KEY_S:
//                                deltaAcceleration.add(0, 0, +acceleration.z);
//                                break;
//                            case GLFW.GLFW_KEY_D:
//                                deltaAcceleration.add(+acceleration.x, 0, 0);
//                                break;
//                            case GLFW.GLFW_KEY_SPACE:
//                                deltaAcceleration.add(0, +acceleration.y, 0);
//                                break;
//                            case GLFW.GLFW_KEY_LEFT_SHIFT:
//                                deltaAcceleration.add(0, -acceleration.y, 0);
//                                break;
//                        }
//                    }
//                }
//        );
        deltaAcceleration.rotate(camera.getTransform().getRotation());
        velocity.mul(.99f).add(deltaAcceleration).min(maxVelocity);
        Vector3f deltaPosition = Vector3.mul(velocity, Time.getDelta());
        camera.getTransform().getPosition().add(deltaPosition);
    }

    public void setMaxVelocity(float speed) {
        this.maxVelocity.set(speed);
    }

    public void setMaxVelocity(float xAxis, float yAxis, float zAxis) {
        this.maxVelocity.set(xAxis, yAxis, zAxis);
    }

    public void setAcceleration(float acceleration) {
        this.acceleration.set(acceleration);
    }

    public void setAcceleration(float xAxis, float yAxis, float zAxis) {
        this.acceleration.set(xAxis, yAxis, zAxis);
    }
}
