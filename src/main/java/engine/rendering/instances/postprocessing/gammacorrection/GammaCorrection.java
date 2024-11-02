package engine.rendering.instances.postprocessing.gammacorrection;

import engine.rendering.RenderOutputData;
import engine.rendering.abstracted.postprocessing.AbstractPostProcessor;
import engine.utils.libraryBindings.opengl.shaders.RenderState;
import engine.utils.libraryBindings.opengl.shaders.UniformFloatProperty;
import engine.utils.libraryBindings.opengl.shaders.UniformTextureProperty;
import engine.utils.libraryBindings.opengl.textures.ITexture;

public class GammaCorrection extends AbstractPostProcessor {

  private static final String FRAG_FILE = "/shaders/postprocessing/gammacorrection/GammaCorrectionFragment.glsl";
  private float factor;

  public GammaCorrection(float factor) throws Exception {
    super(FRAG_FILE);
    this.factor = factor;
    initialize();
  }

  public GammaCorrection() throws Exception {
    this(2.2f); // Default value
  }

  private void initialize() {
    getShadersProgram().addPerRenderUniform(new UniformTextureProperty<RenderOutputData>("textureSampler", 0) {
      @Override
      public ITexture getUniformValue(RenderState<RenderOutputData> state) {
        return state.getInstance().colour;
      }
    });

    getShadersProgram().addPerRenderUniform(new UniformFloatProperty<RenderOutputData>("factor") {
      @Override
      public float getUniformValue(RenderState<RenderOutputData> state) {
        return factor;
      }
    });
  }
}
