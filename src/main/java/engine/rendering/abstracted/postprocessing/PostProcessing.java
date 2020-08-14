package engine.rendering.abstracted.postprocessing;

import engine.rendering.RenderOutputData;
import engine.utils.libraryBindings.maths.joml.Vector2i;
import engine.utils.libraryBindings.opengl.fbos.Fbo;
import engine.utils.libraryBindings.opengl.objects.Vao;
import engine.utils.libraryBindings.opengl.textures.ITexture;
import engine.utils.libraryBindings.opengl.utils.GlUtils;
import engine.utils.libraryBindings.opengl.utils.ModifiableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Class the in charge of dealing with the post
 * processing pipeline, from binding the needed fbo
 * to process the texture by the given post processors
 * and rendering the final image into the screen.
 *
 * @author Saar ----
 * @version 1.0
 * @since 2018-11-4
 */
public class PostProcessing {

    private final List<PostProcessor> postProcessors;
    private final ModifiableList<PostProcessor> modifiablePostProcessors;

    public PostProcessing() {
        this.postProcessors = new ArrayList<>();
        this.modifiablePostProcessors = new ModifiableList<>(postProcessors);
    }

    /**
     * Resize all of the post processors, should be
     * called whenever the size of the window changed
     *
     * @param resolution The width and heigt of the scene
     */
    public void resize(Vector2i resolution) {
        postProcessors.forEach(p -> p.resize(resolution.x, resolution.y));
    }

    public void add(PostProcessor postProcessor) {
        postProcessors.add(postProcessor);
    }

    public void remove(PostProcessor postProcessor) {
        postProcessors.remove(postProcessor);
    }

    /**
     * Processes a texture with all the post processors
     * received earlier. The processed texture can be
     * accessed with getTexture().
     *
     * @param draw the fbo to draw to
     * @param read the fbo to read from
     */
    public void processToFbo(Fbo draw, Fbo read) {
        if (postProcessors.size() == 0) {
            read.blitFbo(draw);
        } else {
            processToFbo(draw, read.getAttachments().get(0).getTexture(),
                    read.getDepthAttachment().getTexture());
        }
    }

    public void processToFbo(Fbo draw, Fbo read, RenderOutputData renderOutputData) {
        if (postProcessors.size() == 0) {
            read.blitFbo(draw);
        } else {
            processToFbo(draw, renderOutputData);
        }
    }

    /**
     * Processes a texture with all the post processors
     * received earlier. The processed texture can be
     * accessed with getTexture().
     *
     * @param fbo          the fbo to process to
     * @param texture      the texture to process
     * @param depthTexture the depth texture use
     */
    public void processToFbo(Fbo fbo, ITexture texture, ITexture depthTexture) {
        process(texture, depthTexture);
        if (postProcessors.size() > 0) {
            postProcessors.get(postProcessors.size() - 1).blitToFbo(fbo);
        }
    }

    public void processToFbo(Fbo fbo, RenderOutputData renderOutputData) {
        process(renderOutputData);
        if (postProcessors.size() > 0) {
            postProcessors.get(postProcessors.size() - 1).blitToFbo(fbo);
        }
    }

    /**
     * Processes a texture with all the post processors
     * received earlier and displays the result to the screen
     *
     * @param fbo the fbo that contains the textures to process
     */
    public void processToScreen(Fbo fbo) {
        if (postProcessors.size() == 0) {
            fbo.blitToScreen();
        } else {
            processToScreen(fbo.getAttachments().get(0).getTexture(),
                    fbo.getDepthAttachment().getTexture());
        }
    }


    /**
     * Processes a texture with all the post processors
     * received earlier and displays the result to the screen
     *
     * @param texture      the colour texture to process
     * @param depthTexture the depth texture to process
     */
    public void processToScreen(ITexture texture, ITexture depthTexture) {
        process(texture, depthTexture);
        if (postProcessors.size() > 0) {
            postProcessors.get(postProcessors.size() - 1).blitToScreen();
        }
    }

    public void process(ITexture texture, ITexture depthTexture) {
        boolean line = GlUtils.isPolygonLines();
        GlUtils.disableDepthTest(); // No depth test is required
        GlUtils.drawPolygonFill();  // The quad should be filled
        GlUtils.disableBlending();  // No blending is required
        GlUtils.disableCulling();   // No culling is required
        Vao.bindIfNone();           // The quad should be drawn without a model
        // and vao cannot be bound to zero
        for (PostProcessor postProcessor : postProcessors) {
            postProcessor.process(new RenderOutputData(texture, null, depthTexture));
            texture = postProcessor.getTexture();
        }

        if (line) GlUtils.drawPolygonLine();
    }

    public void process(RenderOutputData renderOutputData) {
        boolean line = GlUtils.isPolygonLines();
        GlUtils.disableDepthTest(); // No depth test is required
        GlUtils.drawPolygonFill();  // The quad should be filled
        GlUtils.disableBlending();  // No blending is required
        GlUtils.disableCulling();   // No culling is required
        Vao.bindIfNone();           // The quad should be drawn without a model
        // and vao cannot be bound to zero
        ITexture texture = renderOutputData.getColour();
        for (PostProcessor postProcessor : postProcessors) {
            postProcessor.process(new RenderOutputData(texture,
                    renderOutputData.getNormal(), renderOutputData.getDepth()));
            texture = postProcessor.getTexture();
        }

        if (line) GlUtils.drawPolygonLine();
    }

    public ITexture getOutput() {
        if (postProcessors.size() > 0) {
            return postProcessors.get(postProcessors.size() - 1).getTexture();
        } else {
            return null;
        }
    }

    /**
     * Cleans the engine.rendering.renderer, should be called when closing up the program
     */
    public void cleanUp() {
        for (PostProcessor processor : postProcessors) {
            processor.cleanUp();
        }
    }

}
