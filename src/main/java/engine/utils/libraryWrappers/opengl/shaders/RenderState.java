package engine.utils.libraryWrappers.opengl.shaders;


import engine.architecture.models.Mesh;
import engine.architecture.scene.entity.Entity;
import engine.rendering.abstracted.renderers.Renderer;
import engine.rendering.instances.camera.Camera;

public class RenderState<T> {

    private final Renderer<T> renderer;
    private final T instance;
    private final Camera camera;
    private int lastRenderedMesh = 0;

    public RenderState(Renderer<T> renderer, T instance, Camera camera) {
        this.renderer = renderer;
        this.instance = instance;
        this.camera = camera;
    }

    public RenderState(Renderer<T> renderer, Camera camera) {
        this.renderer = renderer;
        this.instance = null;
        this.camera = camera;
    }

    public void incrementMeshRenderingValue() {
        lastRenderedMesh++;
    }

    public int getLastRenderedMesh() {
        return lastRenderedMesh;
    }

    public Mesh getToRenderMesh() {
        if (instance instanceof Entity) {

            return ((Entity) instance).getModel().getMesh(lastRenderedMesh);
        } else {
            return null;
        }
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

    public boolean hasInstance() {
        return instance != null;
    }
}
