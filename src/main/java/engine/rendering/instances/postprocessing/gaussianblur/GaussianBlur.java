package engine.rendering.instances.postprocessing.gaussianblur;

import com.sun.org.apache.xpath.internal.operations.Bool;

import engine.rendering.RenderOutputData;
import engine.rendering.abstracted.postprocessing.AbstractPostProcessor;
import engine.rendering.abstracted.postprocessing.PostProcessor;
import engine.utils.libraryBindings.opengl.constants.DataType;
import engine.utils.libraryBindings.opengl.constants.FormatType;
import engine.utils.libraryBindings.opengl.fbos.Fbo;
import engine.utils.libraryBindings.opengl.fbos.FboTarget;
import engine.utils.libraryBindings.opengl.fbos.attachment.TextureAttachment;
import engine.utils.libraryBindings.opengl.shaders.RenderState;
import engine.utils.libraryBindings.opengl.shaders.UniformIntProperty;
import engine.utils.libraryBindings.opengl.shaders.UniformTextureProperty;
import engine.utils.libraryBindings.opengl.shaders.old.Uniform;
import engine.utils.libraryBindings.opengl.textures.ITexture;
import engine.utils.libraryBindings.opengl.textures.TextureConfigs;
import engine.utils.libraryBindings.opengl.textures.parameters.WrapParameter;
import engine.utils.libraryBindings.opengl.utils.GlBuffer;
import engine.utils.libraryBindings.opengl.utils.GlUtils;

public class GaussianBlur extends AbstractPostProcessor implements PostProcessor {

  private static final String FRAG_FILE = "/shaders/postprocessing/gaussianblur/gaussianBlurFragment.glsl";

  private final int stages;
  private final float scale;
  private final Uniform<Boolean> verticalBlur;

  public GaussianBlur(int stages, float scale) throws Exception {
    super(FRAG_FILE);
    this.stages = stages;
    this.scale = scale;
    this.verticalBlur = Uniform.createBool(getShadersProgram(), "verticalBlur");
    initialize();
  }

  private void initialize() {
    getShadersProgram().addPerRenderUniform(new UniformTextureProperty<RenderOutputData>("textureSampler", 0) {
      @Override
      public ITexture getUniformValue(RenderState<RenderOutputData> state) {
        return state.getInstance().getColour();
      }
    });

    getShadersProgram().addPerRenderUniform(new UniformIntProperty<RenderOutputData>("width") {
      @Override
      public int getUniformValue(RenderState<RenderOutputData> state) {
        return getFbo().getWidth();
      }
    });

    getShadersProgram().addPerRenderUniform(new UniformIntProperty<RenderOutputData>("height") {
      @Override
      public int getUniformValue(RenderState<RenderOutputData> state) {
        return getFbo().getHeight();
      }
    });
  }

  @Override
  public Fbo createFbo(int width, int height) {
    Fbo fbo = Fbo.create((int) (width / scale), (int) (height / scale));
    fbo.addAttachment(
        TextureAttachment.ofColour(0, new TextureConfigs(FormatType.RGB8, FormatType.RGB, DataType.U_BYTE)));

    fbo.getAttachments().get(0).getTexture().getFunctions()
        .borderColour(0f, 0f, 0f, 0f)
        .wrapS(WrapParameter.CLAMP_TO_BORDER)
        .wrapT(WrapParameter.CLAMP_TO_BORDER);
    return fbo;
  }

  @Override
  public void process(RenderOutputData renderOutputData) {
    getFbo().bind(FboTarget.DRAW_FRAMEBUFFER);
    GlUtils.clear(GlBuffer.COLOUR);
    getShadersProgram().bind();

    getShadersProgram().updatePerRenderUniforms(new RenderState<>(null, renderOutputData, null, 0));
    getShadersProgram().updatePerInstanceUniforms(new RenderState<>(null, renderOutputData, null, 0));

    verticalBlur.load(true);
    draw();
    getTexture().bind(0);

    for (int i = 1; i < stages; i++) {
      draw();
    }

    verticalBlur.load(false);
    for (int i = 0; i < stages; i++) {
      draw();
    }

    getShadersProgram().unbind();
  }
}
