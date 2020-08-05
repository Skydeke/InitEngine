package engine.architecture.system;

import engine.architecture.scene.SceneContext;
import lombok.Getter;

public abstract class Application {

    @Getter
    protected SceneContext context;

    public abstract void init(Window window, AppContext renderer) throws Exception;

    public abstract void update(double duration);

    public abstract void render(AppContext renderer);

    public abstract void cleanUp();

    public abstract boolean isClosed();
}
