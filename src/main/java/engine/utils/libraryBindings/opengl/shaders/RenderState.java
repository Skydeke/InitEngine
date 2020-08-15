package engine.utils.libraryBindings.opengl.shaders;


import engine.architecture.models.Mesh;
import engine.rendering.abstracted.Processable;
import engine.rendering.abstracted.renderers.Renderer;
import engine.rendering.instances.camera.Camera;

public class RenderState<T extends Processable> {

    private final Renderer<T> renderer;
    private final T instance;
    private final Camera camera;
    private final int instanceMeshIdx;
    private Mesh mesh = null;

    public RenderState(Renderer<T> renderer, T instance, Camera camera, int instanceMeshIdx) {
        this.renderer = renderer;
        this.instance = instance;
        this.camera = camera;
        this.instanceMeshIdx = instanceMeshIdx;
        if (instance.getModel() != null)
        this.mesh = instance.getModel().getMeshes()[instanceMeshIdx];
    }

    public RenderState(Renderer<T> renderer, Camera camera) {
        this.renderer = renderer;
        this.instance = null;
        this.camera = camera;
        this.instanceMeshIdx = -1;
        this.mesh = null;
    }

    public Renderer<T> getRenderer() {
        return renderer;
    }

    public Camera getCamera() {
        return camera;
    }

    public T getInstance() {
        return instance;
    }

    public Mesh getMesh(){
        return mesh;
    }

    public boolean hasInstance() {
        return instance != null;
    }
}
