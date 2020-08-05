package engine.rendering.abstracted;

import engine.architecture.models.Model;

public interface Processable {

    /**
     * Process the spatial
     * Invoked before rendering the spatial object
     */
    void process();

    Model getModel();
}
