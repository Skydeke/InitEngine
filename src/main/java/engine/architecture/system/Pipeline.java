package engine.architecture.system;

import engine.architecture.scene.SceneContext;
import engine.architecture.scene.SceneFbo;
import engine.rendering.abstracted.renderers.Renderer;
import engine.rendering.abstracted.renderers.Renderer2D;
import engine.rendering.abstracted.renderers.Renderer3D;
import engine.rendering.instances.postprocessing.ssao.SSAO;
import engine.rendering.instances.postprocessing.ssr.SSR;
import engine.rendering.instances.renderers.DebugRenderer;
import engine.rendering.instances.renderers.entity.EntityRenderer;
import engine.rendering.instances.renderers.pbr.PBRDeferredShader;
import engine.rendering.instances.renderers.pbr.PBRRenderer;
import engine.rendering.instances.renderers.shadow.ShadowRenderer;
import engine.rendering.instances.renderers.sky.SkyRenderer;
import engine.utils.libraryWrappers.maths.joml.Vector2i;
import engine.utils.libraryWrappers.opengl.fbos.FrameBufferObject;
import engine.utils.libraryWrappers.opengl.textures.TextureObject;
import engine.utils.libraryWrappers.opengl.utils.GlBuffer;
import engine.utils.libraryWrappers.opengl.utils.GlUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;

public class Pipeline {

    private final List<Renderer3D> renderers3D = new ArrayList<>();
    private final List<Renderer2D> renderers2D = new ArrayList<>();
    private final List<Renderer> lateRenderers = new ArrayList<>();
    private final List<Renderer> renderers = new ArrayList<>();
    @Getter
    private SceneContext context;
    // render component buffer (albedo, position, normal, etc)
    @Getter
    private FrameBufferObject pbrFBO;
    // fbo needed to calculate shadow factor for main lighting pass
    @Getter
    private FrameBufferObject shadowFBO;
    /**
     * PASSES
     **/
    @Getter
    private PBRDeferredShader lightingPass;
    // screen space ambient occlusion pass
    @Getter
    private SSAO ssaoPass;
    // screen space reflection pass
    @Getter
    private SSR ssrPass;

    private boolean anyChange = true;
    /**
     * Base class of the pipeline which provides the following functionality:
     * 1. Overlay rendering (outlines, debug scene objects)
     * 2. Render target texture and depth target texture
     *
     * @param context 3D context for pipleline to run for
     */
    public Pipeline(SceneContext context) {
        this.context = context;
        int target;
        if (Config.instance().getMultisamples() > 0)
            target = GL_TEXTURE_2D_MULTISAMPLE;
        else
            target = GL_TEXTURE_2D;
        /**
         * Channels:
         * layout (location = 0) out vec4 pos_vbo
         * layout (location = 1) out vec4 norm_vbo;
         * layout (location = 2) out vec4 albedo_vbo;
         * + depth buffer
         *
         * __0__________8__________16_________24__________
         * |0| pos.x    | pos.y    | pos.z    | roughness|
         * |1| normal.r | normal.g | normal.b | metalness|
         * |2| albedo.rg| albedo.ba|          |          |
         * |_|__________|__________|__________|__________|
         *
         */
        pbrFBO = new FrameBufferObject();
        pbrFBO.addAttatchments(
                new TextureObject(
                        target, getResolution())
                        .allocateImage2D(GL_RGBA32F, GL_RGBA)
                        .bilinearFilter(),
                new TextureObject(
                        target, getResolution())
                        .allocateImage2D(GL_RGBA32F, GL_RGBA)
                        .bilinearFilter(),
                new TextureObject(
                        target, getResolution())
                        .allocateImage2D(GL_RGBA16F, GL_RGBA)
                        .bilinearFilter(),
                SceneFbo.getInstance().getAttachment(0),
                SceneFbo.getInstance().getDepthAttachment());
        shadowFBO = new FrameBufferObject();
        shadowFBO.addAttatchments(new TextureObject(
                GL_TEXTURE_2D, Config.instance().getShadowBufferWidth(),
                Config.instance().getShadowBufferHeight())
                .allocateDepth()
                .wrap()
                .nofilter());

        lightingPass = new PBRDeferredShader();
        ssaoPass = new SSAO(this);
        ssrPass = new SSR(this);

        addRenderer(EntityRenderer.getInstance());
        addRenderer(PBRRenderer.getInstance());

        addLateRenderer(SkyRenderer.getInstance());
    }

    // explicitly update the resolution of pipeline fields
    public void resize() {
        SceneFbo.getInstance().resize(getContext().getResolution().x, getContext().getResolution().y);
        pbrFBO.resize(getContext().getResolution().x,
                getContext().getResolution().y);
        ssaoPass.resize();
    }

    public Vector2i getResolution() {
        return context.getResolution();
    }

    public void draw() {

        // call sub-render method
        // that will populate the scene texture
        context.getScene().process();
        checkForChanges();
        GlUtils.clear(GlBuffer.COLOUR, GlBuffer.DEPTH, GlBuffer.STENCIL);


        if (Config.instance().isShadows()) {
            shadowFBO.bind();
            Window.instance().resizeViewport(Config.instance().getShadowBufferSize());
            GlUtils.clear(GlBuffer.DEPTH);
            ShadowRenderer.getInstance().render(context);
        }

        pbrFBO.bind(() -> {
            GlUtils.clear(GlBuffer.COLOUR, GlBuffer.DEPTH, GlBuffer.STENCIL);
            Window.instance().resizeViewport(context.getResolution());
            // render scenegraph to obtain geometry data in the pbrFBO buffers
            for (Renderer r : renderers) {
                r.render(context);
            }
        });


        // calculate ssao
        if (Config.instance().isSsao())
            ssaoPass.compute(
                    pbrFBO.getAttachment(0),
                    pbrFBO.getAttachment(1));

        // using buffer data to compute lit color
        lightingPass.compute(
                pbrFBO.getAttachment(2),
                pbrFBO.getAttachment(0),
                pbrFBO.getAttachment(1),
                shadowFBO.getDepthAttachment(),
                ssaoPass.getTargetTexture(),
                pbrFBO.getAttachment(3));

        // calculate reflections
        if (Config.instance().isSsr())
            ssrPass.compute(
                    pbrFBO.getAttachment(0),
                    pbrFBO.getAttachment(1),
                    ssaoPass.getTargetTexture());

        pbrFBO.bind(() -> {
            for (Renderer lateRenderer : lateRenderers) {
                lateRenderer.render(context);
            }
        });
        // reset viewport to window size
        Window.instance().resetViewport();

        if (Config.instance().isDebugLayer()) {
            SceneFbo.getInstance().bind(() -> {
                Window.instance().resizeViewport(getResolution());
                DebugRenderer.getInstance().render(context);
                Window.instance().resetViewport();
            });
        }
        finish();
    }

    /**
     * Returns whether any change occurred, if anything rendered it will be true
     *
     * @return true if any new changed happened in the screen, false otherwise
     */
    boolean isAnyChange() {
        return anyChange;
        // Improvement: check if camera view matrix changed
    }

    void setAnyChange(boolean b) {
        anyChange = b;
    }

    private void checkForChanges() {
        anyChange = false;
        for (Renderer renderer : renderers) {
            anyChange |= renderer.anyProcessed();
        }
    }

    private void addRenderer(Renderer3D<?> renderer) {
        if (!renderers.contains(renderer)) {
            renderers.add(renderer);
            renderers3D.add(renderer);
        }
    }

    private void addRenderer(Renderer2D<?> renderer) {
        if (!renderers.contains(renderer)) {
            renderers.add(renderer);
            renderers2D.add(renderer);
        }
    }

    private void addLateRenderer(Renderer<?> renderer) {
        if (!renderers.contains(renderer)) {
            renderers.add(renderer);
            lateRenderers.add(renderer);
        }
    }

    private void finish() {
        renderers.forEach(Renderer::finish);
    }
}