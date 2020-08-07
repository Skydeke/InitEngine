package engine.architecture.scene;

import engine.architecture.scene.node.Scenegraph;
import engine.architecture.system.Pipeline;
import engine.architecture.ui.element.UIElement;
import engine.architecture.ui.event.mouse.MouseMoveEvent;
import engine.rendering.Shader;
import engine.rendering.instances.camera.Camera;
import engine.utils.libraryWrappers.maths.joml.Vector2i;
import engine.utils.libraryWrappers.opengl.objects.ClipPlane;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;

/**
 * Contains high level engine context for rendering scenes
 * and GUI layers, updating modules, and events
 */

public class SceneContext extends UIElement {

    @Getter
    public Camera camera;
    @Getter
    @Setter
    public ClipPlane clipPlane = ClipPlane.NONE;
    // scenegraph of 3D scene
    @Getter
    protected Scenegraph scene;
    @Getter
    private Picking picking;
    // uses scene to create texture of 3D scene
    @Getter
    private Pipeline pipeline;
    @Getter
    private SelectionManager selectionManager;
    @Getter
    private Vector2i resolution;

    public SceneContext() {
        super();
    }


    /**
     * Reflective method for loading custom render pipeline
     * Will load whichever class of Pipeline is specified in
     * /res/config.properties: "renderEngine"
     *
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public void loadRenderer() throws
            ClassNotFoundException,
            InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
//        this.pipeline = SceneContext.class
//                .getClassLoader()
//                .loadClass(Config.instance().getRenderEngine())
//                .asSubclass(Pipeline.class)
//                .getConstructor(SceneContext.class)
//                .newInstance(this);
        pipeline = new Pipeline(this);
    }

    public void update() {
        // instance update

        // scene UI update
        super.update();

        // now update scene with proper inputs reaching the
        scene.update();
        camera.update();
    }

    /**
     * Contractually you must assign scene context for
     * shader class to be known for rendering information
     */
    public void render() {
        Shader.setBoundContext(this);
        pipeline.draw();
    }

    public void setResolution(Vector2i size) {
        if (!resolution.equals(size)) {
            this.resolution.x = size.x;
            this.resolution.y = size.y;
            pipeline.resize();
            picking.getUUIDmap().resize(size.x, size.y);
        }
    }

    public float getAspectRatio() {
        return (float) resolution.x / (float) resolution.y;
    }

    public void init() {
        this.camera = new Camera(this);
        this.scene = new Scenegraph(camera);
        this.picking = new Picking(this);
        this.selectionManager = new SelectionManager();
        this.resolution = new Vector2i(0, 0);
        onEvent(e -> {
            if (!(e instanceof MouseMoveEvent))
                camera.handle(e);
        });
    }
}
