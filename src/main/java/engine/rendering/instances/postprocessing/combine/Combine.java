package engine.rendering.instances.postprocessing.combine;

import engine.rendering.RenderOutputData;
import engine.rendering.abstracted.postprocessing.AbstractPostProcessor;
import engine.utils.libraryBindings.opengl.shaders.RenderState;
import engine.utils.libraryBindings.opengl.shaders.UniformFloatProperty;
import engine.utils.libraryBindings.opengl.shaders.UniformTextureProperty;
import engine.utils.libraryBindings.opengl.textures.ITexture;
import engine.utils.libraryBindings.opengl.textures.Texture;
import lombok.Getter;
import lombok.Setter;

public class Combine extends AbstractPostProcessor {

  private static final String FRAG_FILE = "/shaders/postprocessing/combine/combineFragment.glsl";

  @Getter
  private float scalar1 = 1f;
  @Getter
  private float scalar2 = 1f;
  @Getter
  @Setter
  private ITexture combination = Texture.NONE;

  public Combine() throws Exception {
    super(FRAG_FILE);
    initialize();
  }

  public Combine(float scalar1, float scalar2) throws Exception {
    this();
    this.scalar1 = scalar1;
    this.scalar2 = scalar2;
  }

  public Combine(ITexture combination) throws Exception {
    this();
    this.combination = combination;
  }

  private void initialize() {
    getShadersProgram().addPerRenderUniform(new UniformTextureProperty<RenderOutputData>("texture1", 0) {
      @Override
      public ITexture getUniformValue(RenderState<RenderOutputData> state) {
        return state.getInstance().getColour();
      }
    });

    getShadersProgram().addPerRenderUniform(new UniformTextureProperty<RenderOutputData>("texture2", 1) {
      @Override
      public ITexture getUniformValue(RenderState<RenderOutputData> state) {
        return combination;
      }
    });

    getShadersProgram().addPerRenderUniform(new UniformFloatProperty<RenderOutputData>("scalar1") {
      @Override
      public float getUniformValue(RenderState<RenderOutputData> state) {
        return scalar1;
      }
    });

    getShadersProgram().addPerRenderUniform(new UniformFloatProperty<RenderOutputData>("scalar2") {
      @Override
      public float getUniformValue(RenderState<RenderOutputData> state) {
        return scalar2;
      }
    });
  }
}
