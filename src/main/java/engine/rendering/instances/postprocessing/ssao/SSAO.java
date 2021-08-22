package engine.rendering.instances.postprocessing.ssao;

import engine.architecture.system.Pipeline;
import engine.utils.libraryBindings.opengl.constants.DataType;
import engine.utils.libraryBindings.opengl.constants.FormatType;
import engine.utils.libraryBindings.opengl.fbos.attachment.TextureAttachment;
import engine.utils.libraryBindings.opengl.textures.ITexture;
import engine.utils.libraryBindings.opengl.textures.TextureConfigs;
import engine.utils.libraryBindings.opengl.textures.parameters.MagFilterParameter;
import engine.utils.libraryBindings.opengl.textures.parameters.MinFilterParameter;
import lombok.Getter;

import static engine.rendering.abstracted.renderers.Renderer.getContext;

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
    public TextureAttachment targetTexture;
    private TextureAttachment preBlur;

    private SSAOBlurShader ssaoBlurShader;
    private SSAOShader ssaoShader;

    private Pipeline pipeline;

    public SSAO(Pipeline pipeline) {
        TextureConfigs preBlurConfigs = new TextureConfigs(FormatType.R16F, FormatType.RED, DataType.FLOAT);
        preBlurConfigs.magFilter = MagFilterParameter.LINEAR;
        preBlurConfigs.minFilter = MinFilterParameter.LINEAR;
        preBlur = TextureAttachment.ofColour(0, preBlurConfigs);
        preBlur.resize(getContext().getResolution().x, getContext().getResolution().y);
        TextureConfigs targetConfigs = new TextureConfigs(FormatType.R16F, FormatType.RED, DataType.FLOAT);
        preBlurConfigs.magFilter = MagFilterParameter.LINEAR;
        preBlurConfigs.minFilter = MinFilterParameter.LINEAR;
        targetTexture = TextureAttachment.ofColour(0, targetConfigs);
        targetTexture.resize(getContext().getResolution().x, getContext().getResolution().y);
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
    public void compute(ITexture worldPos, ITexture normal) {
        ssaoShader.compute(worldPos, normal, preBlur.getTexture());
        ssaoBlurShader.compute(preBlur.getTexture(), targetTexture.getTexture());
    }

    public void resize() {
        targetTexture.resize(getContext().getResolution().x, getContext().getResolution().y);
        preBlur.resize(getContext().getResolution().x, getContext().getResolution().y);
    }
}
