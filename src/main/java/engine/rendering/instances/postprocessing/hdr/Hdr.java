package engine.rendering.instances.postprocessing.hdr;

import engine.rendering.RenderOutputData;
import engine.rendering.abstracted.postprocessing.AbstractPostProcessor;
import engine.utils.libraryBindings.opengl.shaders.RenderState;
import engine.utils.libraryBindings.opengl.shaders.UniformFloatProperty;
import engine.utils.libraryBindings.opengl.shaders.UniformTextureProperty;
import engine.utils.libraryBindings.opengl.textures.ITexture;

public class Hdr extends AbstractPostProcessor {

  private static final String FRAG_FILE = "/shaders/postprocessing/hdr/HdrFragment.glsl";
  private float gamma;
  private float exposure;

  public Hdr(float gamma, float exposure) throws Exception {
    super(FRAG_FILE);
    this.gamma = gamma;
    this.exposure = exposure;
    initialize();
  }

  public Hdr() throws Exception {
    this(2.2f, 1.0f); // Default values
  }

  private void initialize() {
    getShadersProgram().addPerRenderUniform(new UniformTextureProperty<RenderOutputData>("textureSampler", 0) {
      @Override
      public ITexture getUniformValue(RenderState<RenderOutputData> state) {
        return state.getInstance().colour;
      }
    });

    getShadersProgram().addPerRenderUniform(new UniformFloatProperty<RenderOutputData>("gamma") {
      @Override
      public float getUniformValue(RenderState<RenderOutputData> state) {
        return gamma;
      }
    });

    getShadersProgram().addPerRenderUniform(new UniformFloatProperty<RenderOutputData>("exposure") {
      @Override
      public float getUniformValue(RenderState<RenderOutputData> state) {
        return exposure;
      }
    });
  }
}
