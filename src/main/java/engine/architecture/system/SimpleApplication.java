package engine.architecture.system;


import engine.architecture.scene.SceneContext;

public abstract class SimpleApplication extends Application {

    private boolean closed;

    protected SimpleApplication() {
        context = new SceneContext();
    }


    @Override
    public final void init(Window window, AppContext renderer) throws Exception {
        onInit(window, renderer);
    }


    public final void update(double timeDelta) {
        onUpdate(timeDelta);
    }

    @Override
    public final void render(AppContext renderer) {
        onRender(renderer);
    }

    /**
     * Cleanup the game, delete all the objects
     */
    @Override
    public void cleanUp() {
        context.cleanup();
        onCleanUp();
    }

    /**
     * Returns whether the game is closed by self shut down
     *
     * @return true if the game is closed, false otherwise
     */
    public boolean isClosed() {
        return closed;
    }

    protected void setClosed(boolean closed) {
        this.closed = closed;
    }

    protected final void closeGame() {
        this.closed = true;
    }

    /**
     * Initialize the game, load all the objects necessary for the game
     *
     * @param window   the window that will be used during the whole run
     * @param renderer the engine.rendering.renderer that will be used during the whole run
     * @throws Exception loading files might throw some exceptions
     */
    public abstract void onInit(Window window, AppContext renderer) throws Exception;

    /**
     * Update the game, as well as getting the input
     *
     * @param timeDelta time since last update
     */
    public abstract void onUpdate(double timeDelta);

    /**
     * Render the scene
     *
     * @param renderer the engine.rendering.renderer to use
     */
    public abstract void onRender(AppContext renderer);

    /**
     * Cleanup the game, delete all the objects
     */
    public void onCleanUp() {

    }
}
