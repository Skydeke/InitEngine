package engine.rendering.instances.camera;

import engine.architecture.ui.element.UIElement;

public abstract class CameraController extends UIElement {

    public static final CameraController NONE = new CameraController() {
        @Override
        public void control(Camera camera) {

        }
    };

    public CameraController() {
    }

    public abstract void control(Camera camera);

}
