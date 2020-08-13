package engine.rendering.instances.postprocessing.ssao;

import engine.architecture.system.Pipeline;
import engine.utils.libraryBindings.opengl.textures.TextureObject;
import engine.utils.libraryBindings.opengl.textures.TextureTarget;
import lombok.Getter;

import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL30.GL_R16F;

/**
 * SCREEN-SPACED AMBIENT OCCLUSION
 * <p>
 * Quickly approximates shadow facets based on
 * position and normal screen data
 * <p>
 * Parameters (found in Config)
 * * ssao - boolean: enable/disables ssao
 * * ssaoSamples - int: number of samples per pixel
 * * ssaoRadius - float: length of sample vector
 * * ssaoPower - float: strength of effect
 */
public class SSAO {

    @Getter
    private TextureObject targetTexture;
    private TextureObject preBlur;

    private SSAOBlurShader ssaoBlurShader;
    private SSAOShader ssaoShader;

    private Pipeline pipeline;

    public SSAO(Pipeline pipeline) {
        this.preBlur = new TextureObject(
                TextureTarget.TEXTURE_2D, pipeline.getResolution())
                .allocateImage2D(GL_R16F, GL_RED)
                .bilinearFilter();
        this.targetTexture = new TextureObject(
                TextureTarget.TEXTURE_2D, pipeline.getResolution())
                .allocateImage2D(GL_R16F, GL_RED)
                .bilinearFilter();
        this.ssaoBlurShader = new SSAOBlurShader();
        this.ssaoShader = new SSAOShader();
        this.pipeline = pipeline;
    }

    /**
     * Computes SSAO based on current config and input attatchments.
     * Once returned, final SSAO texture can be retrieved with
     * getTargetTexture()
     *
     * @param worldPos World position RGBA32F
     * @param normal   World normal RGBA32F
     */
    public void compute(TextureObject worldPos, TextureObject normal) {
        ssaoShader.compute(worldPos, normal, preBlur);
        ssaoBlurShader.compute(preBlur, targetTexture);
    }

    public void resize() {
        targetTexture.resize(pipeline.getResolution());
        preBlur.resize(pipeline.getResolution());
    }
}
