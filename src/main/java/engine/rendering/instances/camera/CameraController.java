package engine.rendering.instances.camera;


public abstract class CameraController {

    public static final CameraController NONE = new CameraController() {
        @Override
        public void control(Camera camera) {

        }
    };

    public CameraController() {
    }

    public abstract void control(Camera camera);

}
