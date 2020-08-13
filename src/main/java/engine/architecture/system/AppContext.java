package engine.architecture.system;

import engine.architecture.scene.SceneContext;
import engine.architecture.ui.element.ElementManager;
import engine.architecture.ui.element.RootElement;
import engine.architecture.ui.element.UIElement;
import engine.architecture.ui.element.button.Button;
import engine.architecture.ui.element.button.ButtonSettings;
import engine.architecture.ui.element.layout.Box;
import engine.architecture.ui.element.viewport.SceneViewport;
import engine.architecture.ui.element.viewport.VerticalViewport;
import engine.utils.libraryBindings.maths.joml.Vector4i;
import engine.utils.libraryBindings.opengl.utils.GlUtils;
import lombok.Getter;

import java.util.Collections;
import java.util.Optional;

public class AppContext {

    private static AppContext instance;
    @Getter
    public SceneContext sceneContext;
    @Getter
    private RootElement root;
    @Getter
    private UIElement renderElement;
    @Getter
    private ElementManager elementManager;

    public static AppContext instance() {
        if (instance == null)
            instance = new AppContext();
        return instance;
    }

    public void init(Application game) throws Exception {

        root = new RootElement();
        renderElement = root;
        elementManager = ElementManager.instance();

        elementManager.init(this);

        // initialize pipeline from config
        this.sceneContext = game.getContext();
        sceneContext.init();
        sceneContext.loadRenderer();
        // initialize engine implementation
        game.init(Window.instance(), this);
        // replace with application load protocol
        __init__ui();
    }


    // TODO: create application loader
    // TEMPORARY FOR TESTING
    private void __init__ui() {

        Optional<SceneViewport> sceneViewport = Optional.of(new SceneViewport(sceneContext));

        ButtonSettings bs = new ButtonSettings();
//        bs.setButtonColor(new Color(0x6060FF));
//        bs.setClickColor(new Color(0x60FFFF));
//        bs.setHoverColor(new Color(0xFFFFFF));
        bs.setRounding(new Vector4i(5));

        Button button = new Button(bs), up = new Button(bs), down = new Button(bs), ssrToggle = new Button(bs);
        button.addListener(e -> Config.instance().setSsao(!Config.instance().isSsao()));
        button.addListener(e -> System.out.println("Pressed SSAO button"));
        up.addListener(e -> Config.instance().setSsaoPower(Config.instance().getSsaoPower() + 1));
        up.addListener(e -> System.out.println("Pressed SSAO UP button"));
        down.addListener(e -> Config.instance().setSsaoPower(Config.instance().getSsaoPower() - 1));
        down.addListener(e -> System.out.println("Pressed SSAO DOWN button"));
        ssrToggle.addListener(e -> Config.instance().setSsr(!Config.instance().isSsr()));
        ssrToggle.addListener(e -> System.out.println("Pressed SSR button"));

//        VerticalViewport pv = new VerticalViewport(40, 100, 100);
//        VerticalViewport p2 = new VerticalViewport(40, 100, 100);
//        VerticalViewport p3 = new VerticalViewport(40, 100, 100);
        VerticalViewport viewport = new VerticalViewport(40, 100, 160, button, up, down, ssrToggle);
        root.addChildren(viewport);//), pv, p2, p3);
        viewport.setBox(new Box(0.05f, 0.25f, 0.2f, 0.25f));
//        pv.setBox(new Box(0.8f, 0.3f, 0.15f, 0.5f));
//        p2.setBox(new Box(0.5f, 0.3f, 0.15f, 0.5f));
//        p3.setBox(new Box(0.3f, 0.3f, 0.15f, 0.5f));

        root.addChildren(sceneViewport.get());
        sceneViewport.get().setBox(new Box(0.3f, 0.1f, 0.65f, 0.8f));
//        sceneViewport.get().setConstraints(new UIConstraints(new PercentageConstraint(0.4f), new CenterConstraint(),
//                new PercentageConstraint(0.6f),
//                new PercentageConstraint(0.8f)));
//        sceneViewport.get().recalculateAbsolutePositions();//One time Positioning
//        sceneViewport.get().setConstraints(null);

//        Panel p = new Panel();
//        p.setColor(new Color(0, 0, 250));
//        p.setConstraints(new UIConstraints(new CenterConstraint(),
//                new CenterConstraint(),
//                new PercentageConstraint(1f),
//                new PercentageConstraint(1f)));
//        Panel d = new Panel();
//        d.setColor(new Color(0, 250, 0));
//        d.setConstraints(new UIConstraints(new CenterConstraint(),
//                new CenterConstraint(),
//                new PercentageConstraint(0.8f),
//                new PercentageConstraint(0.8f)));
//        p.addChild(d);
//        root.addChild(p);
        root.recalculateAbsolutePositions();
    }

    void update() {
        if (Window.instance().isResized()) {
            System.out.println("Window size changed.");
            root.recalculateAbsolutePositions();
            Window.instance().setResized(false);
        }
        sceneContext.update();
        root.update();
        elementManager.update();
    }

    void draw(boolean isVisible) {
        if (isVisible){
            sceneContext.render();
            Window.instance().resetViewport();
            GlUtils.disableDepthTest();
            Collections.reverse(renderElement.getChildren());
            renderElement.render();
            Collections.reverse(renderElement.getChildren());
            GlUtils.enableDepthTest();
        }
    }

    public void setRenderElement(UIElement renderElement) {
        root.setActivated(false);
        this.renderElement = renderElement;
        this.renderElement.setActivated(true);
        this.renderElement.recalculateAbsolutePositions();
    }

    public void resetRenderElement() {
        setRenderElement(root);
    }
}
