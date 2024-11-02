package engine.rendering.instances.postprocessing.contrast;

import engine.rendering.RenderOutputData;
import engine.rendering.abstracted.postprocessing.AbstractPostProcessor;
import engine.utils.libraryBindings.opengl.shaders.RenderState;
import engine.utils.libraryBindings.opengl.shaders.UniformFloatProperty;
import engine.utils.libraryBindings.opengl.shaders.UniformTextureProperty;
import engine.utils.libraryBindings.opengl.textures.ITexture;

public class Contrast extends AbstractPostProcessor {

  private static final String FRAG_FILE = "/shaders/postprocessing/contrast/ContrastFragment.glsl";

  private float factor;

  public Contrast() throws Exception {
    this(1.4f); // Default factor
  }

  public Contrast(float factor) throws Exception {
    super(FRAG_FILE);
    this.factor = factor;
    initialize();
  }

  private void initialize() {
    getShadersProgram().addPerRenderUniform(new UniformTextureProperty<RenderOutputData>("textureSampler", 0) {
      @Override
      public ITexture getUniformValue(RenderState<RenderOutputData> state) {
        return state.getInstance().getColour();
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
