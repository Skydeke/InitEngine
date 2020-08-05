package engine.rendering.abstracted.renderers;

import engine.architecture.models.Model;
import engine.architecture.scene.SceneContext;
import engine.architecture.system.AppContext;
import engine.rendering.abstracted.Processable;

import java.util.HashMap;
import java.util.LinkedList;

public abstract class Renderer<T extends Processable> implements IRenderer {

    protected final HashMap<Model, LinkedList<T>> renderList = new HashMap<>();

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
//        System.out.println("Processing Entity " + renderList.size());
        if (toProcess != null){
            if (renderList.get(toProcess.getModel()) == null){
                renderList.put(toProcess.getModel(), new LinkedList<>());
                renderList.get(toProcess.getModel()).add(toProcess);
//            System.out.println("Created new List and added one T");
                return;
            }
            if (renderList.get(toProcess.getModel()) != null && !renderList.get(toProcess.getModel()).contains(toProcess)) {
                LinkedList<T> list = renderList.get(toProcess.getModel());
                if (list != null) {
                    list.add(toProcess);
//                System.out.println("Found List and added one T");
                }
            }
        }
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
