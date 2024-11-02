package engine.rendering.instances.postprocessing.radialblur;

import engine.rendering.RenderOutputData;
import engine.rendering.abstracted.postprocessing.AbstractPostProcessor;
import engine.rendering.abstracted.postprocessing.PostProcessor;
import engine.utils.lengths.ILength;
import engine.utils.lengths.Pixels;
import engine.utils.lengths.Proportion;
import engine.utils.libraryBindings.maths.utils.Vector2;
import engine.utils.libraryBindings.opengl.shaders.*;
import engine.utils.libraryBindings.opengl.textures.ITexture;

public class RadialBlur extends AbstractPostProcessor {

  private static final String FRAG_FILE = "/shaders/postprocessing/radialblur/radialBlurFrag.glsl";

  private int samples = 50;
  private float factor = 2f;

  private ILength x = Proportion.of(0.5f);
  private ILength y = Proportion.of(0.5f);

  public RadialBlur() throws Exception {
    super(FRAG_FILE);
    initialize();
  }

  public RadialBlur(int x, int y) throws Exception {
    this();
    setCenter(x, y);
  }

  private void initialize() {
    getShadersProgram().addPerRenderUniform(new UniformTextureProperty<RenderOutputData>("textureSampler", 0) {
      @Override
      public ITexture getUniformValue(RenderState<RenderOutputData> state) {
        return state.getInstance().getColour();
      }
    });

    getShadersProgram().addPerRenderUniform(new UniformValueProperty<RenderOutputData>("center") {
      @Override
      public UniformValue getUniformValue(RenderState<RenderOutputData> state) {
        return Vector2.of(x.proportionTo(getFbo().getWidth()) / getFbo().getWidth(),
            y.proportionTo(getFbo().getHeight()) / getFbo().getHeight());
      }
    });

    getShadersProgram().addPerRenderUniform(new UniformIntProperty<RenderOutputData>("samples") {
      @Override
      public int getUniformValue(RenderState<RenderOutputData> state) {
        return samples;
      }
    });

    getShadersProgram().addPerRenderUniform(new UniformFloatProperty<RenderOutputData>("factor") {
      @Override
      public float getUniformValue(RenderState<RenderOutputData> state) {
        return factor;
      }
    });
  }

  public void setCenter(int x, int y) {
    this.x = new Pixels(x);
    this.y = new Pixels(y);
  }

  // Getters and Setters for samples and factor if needed
  public int getSamples() {
    return samples;
  }

  public void setSamples(int samples) {
    this.samples = samples;
  }

  public float getFactor() {
    return factor;
  }

  public void setFactor(float factor) {
    this.factor = factor;
  }
}
