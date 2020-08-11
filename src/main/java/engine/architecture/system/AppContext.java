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
import engine.utils.libraryBindings.opengl.utils.GlBuffer;
import engine.utils.libraryBindings.opengl.utils.GlUtils;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class AppContext {

    private static AppContext instance;
    @Getter
    public SceneContext sceneContext;
    @Getter
    private RootElement root;
    @Setter
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
        try {

            this.sceneContext = game.getContext();
            sceneContext.init();
            sceneContext.loadRenderer();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Render engine class does not exist: " + Config.instance().getRenderEngine());
            System.exit(-1);
        } catch (InstantiationException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            System.err.println("Render engine does not take SceneContext in its constructor: " + Config.instance().getRenderEngine());
            System.exit(-1);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        // initialize engine implementation
        game.init(Window.instance(), this);
        // replace with application load protocol
        __init__ui();
    }


    // TODO: create application loader
    // TEMPORARY FOR TESTING
    private void __init__ui() {

        Optional<SceneViewport> sceneViewport = Optional.empty();
        sceneViewport = Optional.of(new SceneViewport(sceneContext));

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
        if (sceneViewport.isPresent()) {
            root.addChildren(sceneViewport.get());
            sceneViewport.get().setBox(new Box(0.3f, 0.1f, 0.65f, 0.8f));
        }

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

    public void update() {
        if (Window.instance().isResized()) {
            root.recalculateAbsolutePositions();
            Window.instance().setResized(false);
        }
        sceneContext.update();
        root.update();
        elementManager.update();
    }

    public void draw() {
        Window.instance().setBlending(false);
        sceneContext.render();
        Window.instance().setBlending(true);
        GlUtils.disableDepthTest();
        GlUtils.clear(GlBuffer.COLOUR);
        Window.instance().resetViewport();
        renderElement.render();
        GlUtils.enableDepthTest();
    }

    public void setRenderElement(UIElement renderElement) {
        root.setActivated(false);
        root.getChildren().forEach(e -> e.setActivated(false));
        this.renderElement = renderElement;
        this.renderElement.setActivated(true);
        this.renderElement.getChildren().forEach(e -> e.setActivated(true));
        this.renderElement.recalculateAbsolutePositions();
        this.renderElement.update();
    }

    public void resetRenderElement() {
        setRenderElement(root);
    }
}
