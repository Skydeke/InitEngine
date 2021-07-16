package engine.architecture.system;

import engine.architecture.scene.SceneContext;
import engine.utils.libraryBindings.opengl.fbos.SceneFbo;
import engine.utils.libraryBindings.opengl.textures.Texture;
import engine.utils.libraryBindings.opengl.utils.GlUtils;
import glm_.vec2.Vec2;
import glm_.vec4.Vec4;
import imgui.ImGui;
import imgui.MutableProperty0;
import imgui.api.ContextKt;
import imgui.classes.Context;
import imgui.classes.IO;
import imgui.impl.gl.ImplGL3;
import imgui.impl.glfw.ImplGlfw;
import lombok.Getter;
import org.lwjgl.opengl.GL;
import uno.glfw.GlfwWindow;

import java.util.Objects;

public class AppContext {

    private static AppContext instance;
    @Getter
    public SceneContext sceneContext;

    private final ImGui imGui = ImGui.INSTANCE;
    private ImplGL3 implGl3;
    private ImplGlfw implGlfw;
    private IO io;

    private final MutableProperty0<Boolean> showAnotherWindow = new MutableProperty0<>(false);
    private final int[] counter = {0};

    public static AppContext instance() {
        if (instance == null)
            instance = new AppContext();
        return instance;
    }

    public void init(Application game) throws Exception {
        // initialize pipeline from config
        this.sceneContext = game.getContext();
        sceneContext.init();
        sceneContext.loadRenderer();
        // initialize engine implementation
        game.init(Window.instance(), this);
        initUi();
    }

    private void initUi() {
        GL.createCapabilities();
        Context context = new Context();
        ContextKt.setGImGui(context);
        GlfwWindow glfwWindow = GlfwWindow.from(Window.instance().getHandle());
        glfwWindow.makeContextCurrent();
        implGlfw = new ImplGlfw(glfwWindow, false, null);
        implGl3 = new ImplGL3();
        imGui.styleColorsDark(null);
        io = imGui.getIo();
    }

    void update() {
        implGl3.newFrame();
        implGlfw.newFrame();
        imGui.newFrame();

        if (Window.instance().isResized()) {
            System.out.println("Window size changed.");
            sceneContext.setResolution(Window.instance().getResolution());
            Window.instance().setResized(false);
        }

        sceneContext.update();
    }

    void draw(boolean isVisible) {
        if (isVisible) {
            GlUtils.enableDepthTest();
            sceneContext.render();
            GlUtils.disableDepthTest();
            Window.instance().resetViewport();
        }

        SceneFbo.getInstance().blitToScreen();

        //TODO Create a posibility to create your UI in the SimpleApplication class
        //TODO Create Event-System, better Input System


        imGui.text("Hello, world!");                                // Display some text (you can use a format string too)

        imGui.checkbox("Another Window", showAnotherWindow);

        if (imGui.button("Button", new Vec2())) // Buttons return true when clicked (NB: most widgets return true when edited/activated)
            counter[0]++;

        imGui.sameLine(0f, -1f);
        imGui.text("counter = " + counter[0]);

        imGui.text("Application average %.3f ms/frame (%.1f FPS)", 1_000f / io.getFramerate(), io.getFramerate());

        // 2. Show another simple window. In most cases you will use an explicit begin/end pair to name the window.
        if (showAnotherWindow.get()) {
            imGui.begin("Another Window", showAnotherWindow, 0);
            imGui.text("Hello from another window!");
            //Texture renderedScene = SceneFbo.getInstance().getAttachments().get(0).getTexture();
            Texture renderedScene = getSceneContext().getPipeline().getShadowFBO().getDepthAttachment().getTexture();
            renderedScene.bind();
            imGui.image(renderedScene.getId(), new Vec2(1280,
                            720),
                    new Vec2(0, 1),
                    new Vec2(1, 0),
                    new Vec4(1.0f),
                    new Vec4(0.0f));
            if (imGui.button("Close Me", new Vec2()))
                showAnotherWindow.set(false);
            imGui.end();
        }
        imGui.showDemoWindow(new boolean[]{true});

        imGui.render();
        implGl3.renderDrawData(Objects.requireNonNull(imGui.getDrawData()));
    }
}
