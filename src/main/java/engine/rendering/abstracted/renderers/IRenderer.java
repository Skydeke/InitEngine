package engine.rendering.abstracted.renderers;

import engine.architecture.scene.SceneContext;
import engine.architecture.scene.node.Node;

public interface IRenderer {

    /**
     * Render to the scene using the render context
     *
     * @param context the render context
     */
    void render(SceneContext context);

    void render(SceneContext context, Node.Condition condition);

    /**
     * Finish the rendering process
     * Invoked after finished rendering the scene
     */
    void finish();

    /**
     * Delete the engine.rendering.renderer
     * Invoked when the program closes
     */
    void delete();

}
