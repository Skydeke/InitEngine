package engine.architecture.system;

import engine.architecture.scene.SceneContext;
import engine.utils.libraryBindings.opengl.fbos.SceneFbo;
import engine.utils.libraryBindings.opengl.textures.Texture;
import engine.utils.libraryBindings.opengl.utils.GlUtils;
import glm_.vec2.Vec2;
import glm_.vec3.Vec3;
import glm_.vec4.Vec4;
import imgui.ImGui;
import imgui.MutableProperty0;
import imgui.WindowFlag;
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

    private final MutableProperty0<Boolean> showShadowMapInfoWindow = new MutableProperty0<>(false);
    private final MutableProperty0<Boolean> showSceneInfoWindow = new MutableProperty0<>(false);
    private final Vec3 v = new Vec3(0, 0.007f, -0.043f);

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

//        imGui.begin("Debug", new boolean[]{true}, WindowFlag.NoTitleBar.i);
        imGui.begin("Debug", new boolean[]{true}, WindowFlag.None.i);
        imGui.text("Hello, world!");                                // Display some text (you can use a format string too)
        if (imGui.treeNode("SSAO")) {
            if (imGui.button("Toggle SSAO", new Vec2()))
                Config.getInstance().setSsao(!Config.getInstance().isSsao());
            if (imGui.button("Increase SSAO-Power", new Vec2()))
                Config.getInstance().setSsaoPower(Config.getInstance().getSsaoPower() + 1);
            if (imGui.button("Decrease SSAO-Power", new Vec2()))
                Config.getInstance().setSsaoPower(Config.getInstance().getSsaoPower() - 1);
            imGui.treePop();
        }
        if (imGui.treeNode("SSR")) {
            if (imGui.button("Toggle SSR", new Vec2()))
                Config.getInstance().setSsr(!Config.getInstance().isSsr());
            imGui.treePop();
        }
        imGui.text("FPS: Application average %.3f ms/frame (%.1f FPS)", 1_000f / io.getFramerate(), io.getFramerate());
        imGui.checkbox("See Shadow-Map-Texture", showShadowMapInfoWindow);
        imGui.checkbox("See Scene-Texture", showSceneInfoWindow);

        // 2. Show another simple window. In most cases you will use an explicit begin/end pair to name the window.
        if (showShadowMapInfoWindow.get()) {
            imGui.begin("Shadow-Map Window", showShadowMapInfoWindow, 0);
            imGui.text("This is the Shadow-Map window!");
            Texture renderedScene = getSceneContext().getPipeline().getShadowFBO().getDepthAttachment().getTexture();
            renderedScene.bind();
            imGui.image(renderedScene.getId(), new Vec2(650,
                            360),
                    new Vec2(0, 1),
                    new Vec2(1, 0),
                    new Vec4(1.0f),
                    new Vec4(0.0f));
            imGui.end();
        }

        if (showSceneInfoWindow.get()) {
            imGui.begin("Scene Window", showSceneInfoWindow, 0);
            imGui.text("This is the Scene window!");
            Texture renderedScene = SceneFbo.getInstance().getAttachments().get(0).getTexture();
            renderedScene.bind();
            imGui.image(renderedScene.getId(), new Vec2(650,
                            360),
                    new Vec2(0, 1),
                    new Vec2(1, 0),
                    new Vec4(1.0f),
                    new Vec4(0.0f));
            imGui.end();
        }

        imGui.render();
        implGl3.renderDrawData(Objects.requireNonNull(imGui.getDrawData()));
    }
}
