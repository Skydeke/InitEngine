package engine.rendering.abstracted.renderers;

import engine.architecture.scene.SceneContext;
import engine.architecture.system.AppContext;

import java.util.LinkedList;
import java.util.List;

public abstract class Renderer<T> implements IRenderer {

    protected final List<T> renderList = new LinkedList<>();

    /**
     * Returns the context of the current render
     *
     * @return the context of the current render
     */
    public static SceneContext getContext() {
        return AppContext.instance().getSceneContext();
    }

    /**
     * Process a T that will be rendered once when calling {@link Renderer#render(SceneContext)}
     *
     * @param toProcess the T to render
     */
    public void process(T toProcess) {
        if (toProcess != null && !renderList.contains(toProcess))
            renderList.add(toProcess);
    }

    /**
     * Returns whether any objects have been processed to render
     *
     * @return true is the render list is not empty false otherwise
     */
    public boolean anyProcessed() {
        return renderList.size() > 0;
    }

    @Override
    public void finish() {
        this.renderList.clear();
    }

    /**
     * Clean up the engine.rendering.renderer
     */
    public abstract void cleanUp();

    @Override
    public void delete() {
        cleanUp();
    }
}
