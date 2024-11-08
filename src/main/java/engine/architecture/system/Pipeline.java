package engine.architecture.system;

import engine.architecture.scene.SceneContext;
import engine.rendering.RenderOutputData;
import engine.rendering.abstracted.postprocessing.PostProcessing;
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
import engine.utils.libraryBindings.opengl.constants.DataType;
import engine.utils.libraryBindings.opengl.constants.FormatType;
import engine.utils.libraryBindings.opengl.fbos.Fbo;
import engine.utils.libraryBindings.opengl.fbos.FboTarget;
import engine.utils.libraryBindings.opengl.fbos.SceneFbo;
import engine.utils.libraryBindings.opengl.fbos.attachment.TextureAttachment;
import engine.utils.libraryBindings.opengl.objects.ClipPlane;
import engine.utils.libraryBindings.opengl.textures.TextureConfigs;
import engine.utils.libraryBindings.opengl.textures.parameters.MagFilterParameter;
import engine.utils.libraryBindings.opengl.textures.parameters.MinFilterParameter;
import engine.utils.libraryBindings.opengl.textures.parameters.WrapParameter;
import engine.utils.libraryBindings.opengl.utils.GlBuffer;
import engine.utils.libraryBindings.opengl.utils.GlUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Pipeline {

    private final List<Renderer3D> renderers3D = new ArrayList<>();
    private final List<Renderer2D> renderers2D = new ArrayList<>();
    private final List<Renderer> lateRenderers = new ArrayList<>();
    private final List<Renderer> renderers = new ArrayList<>();
    @Getter
    private SceneContext context;
    // render component buffer (albedo, position, normal, etc)
    @Getter
    private Fbo pbrFBO;
    // fbo needed to calculate shadow factor for main lighting pass
    @Getter
    private Fbo shadowFBO;
    @Getter
    private final PostProcessing postProcessing = new PostProcessing();
    /**
     * PASSES
     **/
    @Getter
    private PBRDeferredShader lightingPass;
    // screen space ambient occlusion pass
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
        /*
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
        pbrFBO = Fbo.create(getContext().getResolution().x, getContext().getResolution().y);
        TextureConfigs posConfigs = new TextureConfigs(FormatType.RGBA32F, FormatType.RGBA, DataType.FLOAT);
        posConfigs.magFilter = MagFilterParameter.LINEAR;
        posConfigs.minFilter = MinFilterParameter.LINEAR;
        pbrFBO.addAttachment(TextureAttachment.ofColour(0, posConfigs));
        TextureConfigs normalConfigs = new TextureConfigs(FormatType.RGBA32F, FormatType.RGBA, DataType.FLOAT);
        normalConfigs.magFilter = MagFilterParameter.LINEAR;
        normalConfigs.minFilter = MinFilterParameter.LINEAR;
        pbrFBO.addAttachment(TextureAttachment.ofColour(1, normalConfigs));
        TextureConfigs albedoConfigs = new TextureConfigs(FormatType.RGBA16F, FormatType.RGBA, DataType.FLOAT);
        albedoConfigs.magFilter = MagFilterParameter.LINEAR;
        albedoConfigs.minFilter = MinFilterParameter.LINEAR;
        pbrFBO.addAttachment(TextureAttachment.ofColour(2, albedoConfigs));
        pbrFBO.addAttachment(SceneFbo.getInstance().getAttachments().get(0));
        pbrFBO.addAttachment(SceneFbo.getInstance().getDepthAttachment());
        pbrFBO.unbind();

        context.setOutputData(new RenderOutputData(
                SceneFbo.getInstance().getAttachments().get(0).getTexture(),
                pbrFBO.getAttachments().get(1).getTexture(),
                SceneFbo.getInstance().getDepthAttachment().getTexture(),
                pbrFBO.getAttachments().get(0).getTexture()
        ));

        shadowFBO = Fbo.create(Config.instance().getShadowBufferWidth(), Config.instance().getShadowBufferHeight());
        TextureConfigs shadowConfigs = new TextureConfigs(FormatType.DEPTH_COMPONENT24, FormatType.DEPTH_COMPONENT, DataType.FLOAT);
        shadowConfigs.minFilter = MinFilterParameter.NEAREST;
        shadowConfigs.magFilter = MagFilterParameter.NEAREST;
        shadowConfigs.wrapS = WrapParameter.REPEAT;
        shadowConfigs.wrapT = WrapParameter.REPEAT;
        shadowFBO.addAttachment(TextureAttachment.ofDepth(shadowConfigs));
        shadowFBO.unbind();



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
        postProcessing.resize(getContext().getResolution());
    }

    public void draw() {
        context.getScene().processEffects(this);
        // call sub-render method
        // that will populate the scene texture
        //Makes sure that all GameObjects are processes and queued for rendering in the correct renderers.
        context.getScene().process();
        context.setClipPlane(ClipPlane.NONE);
        checkForChanges();
        if (isAnyChange()){
            GlUtils.clear(GlBuffer.COLOUR, GlBuffer.DEPTH, GlBuffer.STENCIL);


            if (Config.instance().isShadows()) {
                shadowFBO.bind(FboTarget.DRAW_FRAMEBUFFER);
                GlUtils.clear(GlBuffer.DEPTH);
                ShadowRenderer.getInstance().render(context);
            }

            pbrFBO.bind(FboTarget.DRAW_FRAMEBUFFER);
            GlUtils.clear(GlBuffer.COLOUR, GlBuffer.DEPTH, GlBuffer.STENCIL);
            // render scenegraph to obtain geometry data in the pbrFBO buffers
            for (Renderer r : renderers) {
                r.render(context);
            }


            // calculate ssao
            if (Config.instance().isSsao())
                ssaoPass.compute(
                        pbrFBO.getAttachments().get(0).getTexture(),
                        pbrFBO.getAttachments().get(1).getTexture());

            // using buffer data to compute lit color
            lightingPass.compute(
                    pbrFBO.getAttachments().get(2).getTexture(),
                    pbrFBO.getAttachments().get(0).getTexture(),
                    pbrFBO.getAttachments().get(1).getTexture(),
                    shadowFBO.getDepthAttachment().getTexture(),
                    ssaoPass.getTargetTexture().getTexture(),
                    SceneFbo.getInstance().getAttachments().get(0).getTexture());

            // calculate reflections
            if (Config.instance().isSsr())
                ssrPass.compute(
                        pbrFBO.getAttachments().get(0).getTexture(),
                        pbrFBO.getAttachments().get(1).getTexture(),
                        ssaoPass.getTargetTexture().getTexture());
            pbrFBO.unbind(FboTarget.DRAW_FRAMEBUFFER);

            SceneFbo.getInstance().bind(FboTarget.DRAW_FRAMEBUFFER);
            postProcessing.processToFbo(SceneFbo.getInstance(), context.getOutputData());
            for (Renderer lateRenderer : lateRenderers) {
                lateRenderer.render(context);
            }
            // reset viewport to window size
            Window.instance().resetViewport();

            if (Config.instance().isDebugLayer()) {
                DebugRenderer.getInstance().render(context);
            }

            for (Renderer2D r : renderers2D) {
                r.render(context);
            }
            SceneFbo.getInstance().unbind();
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

    public SSAO getSsaoPass() {
        return ssaoPass;
    }
}