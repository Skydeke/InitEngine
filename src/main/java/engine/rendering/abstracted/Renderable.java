package engine.rendering.abstracted;

import engine.architecture.models.Model;
import engine.utils.libraryWrappers.opengl.constants.RenderMode;

public interface Renderable {

    /**
     * Process the spatial
     * Invoked before rendering the spatial object
     */
    void process();

    void render(RenderMode renderMode);

    Model getModel();
}
