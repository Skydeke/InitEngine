package engine.architecture.ui.element.viewport;

import engine.architecture.scene.SceneContext;
import engine.architecture.ui.element.panel.ScenePanel;
import lombok.Getter;


public class SceneViewport extends Viewport {

    @Getter
    private SceneContext context;

    public SceneViewport(SceneContext sceneContext) {
        this(sceneContext, new ViewportSettings());
    }

    public SceneViewport(SceneContext sceneContext, ViewportSettings vs) {
        super(vs);
        this.context = sceneContext;

        ScenePanel panel = new ScenePanel(sceneContext);
        setMainPanel(panel);
    }


}
