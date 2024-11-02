package engine.rendering.instances.postprocessing.mix;

import engine.rendering.RenderOutputData;
import engine.rendering.abstracted.postprocessing.AbstractPostProcessor;
import engine.rendering.abstracted.postprocessing.PostProcessor;
import engine.utils.libraryBindings.opengl.shaders.RenderState;
import engine.utils.libraryBindings.opengl.shaders.UniformFloatProperty;
import engine.utils.libraryBindings.opengl.shaders.UniformTextureProperty;
import engine.utils.libraryBindings.opengl.textures.ITexture;
import engine.utils.libraryBindings.opengl.textures.Texture;

public class MixPostProcessor extends AbstractPostProcessor {

  private static final String FRAG_FILE = "/shaders/postprocessing/mix/MixFragment.glsl";

  private float scalar = 0.5f;
  private Texture combination = Texture.NONE;

  public MixPostProcessor() throws Exception {
    super(FRAG_FILE);
    initialize();
  }

  public MixPostProcessor(float scalar) throws Exception {
    this();
    this.scalar = scalar;
  }

  public MixPostProcessor(Texture combination) throws Exception {
    this();
    this.combination = combination;
  }

  private void initialize() {
    getShadersProgram().addPerRenderUniform(new UniformTextureProperty<RenderOutputData>("texture1", 0) {
      @Override
      public ITexture getUniformValue(RenderState<RenderOutputData> state) {
        return state.getInstance().colour;
      }
    });

    getShadersProgram().addPerRenderUniform(new UniformTextureProperty<RenderOutputData>("texture2", 1) {
      @Override
      public ITexture getUniformValue(RenderState<RenderOutputData> state) {
        return combination;
      }
    });

    getShadersProgram().addPerRenderUniform(new UniformFloatProperty<RenderOutputData>("scalar") {
      @Override
      public float getUniformValue(RenderState<RenderOutputData> state) {
        return scalar;
      }
    });
  }
}
