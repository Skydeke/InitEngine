package engine.rendering.instances.postprocessing.mincolour;

import engine.rendering.RenderOutputData;
import engine.rendering.abstracted.postprocessing.AbstractPostProcessor;
import engine.rendering.abstracted.postprocessing.PostProcessor;
import engine.utils.libraryBindings.maths.joml.Vector3f;
import engine.utils.libraryBindings.maths.utils.Vector3;
import engine.utils.libraryBindings.opengl.shaders.RenderState;
import engine.utils.libraryBindings.opengl.shaders.UniformTextureProperty;
import engine.utils.libraryBindings.opengl.shaders.UniformValueProperty;
import engine.utils.libraryBindings.opengl.textures.ITexture;

public class MinColour extends AbstractPostProcessor implements PostProcessor {

  private static final String FRAG_FILE = "/shaders/postprocessing/mincolour/minColourFrag.glsl";

  private final Vector3f minColour;

  public MinColour(float r, float g, float b) throws Exception {
    super(FRAG_FILE);
    this.minColour = Vector3.of(r, g, b);
    initialize();
  }

  private void initialize() {
    getShadersProgram().addPerRenderUniform(new UniformTextureProperty<RenderOutputData>("textureSampler", 0) {
      @Override
      public ITexture getUniformValue(RenderState<RenderOutputData> state) {
        return state.getInstance().getColour();
      }
    });

    getShadersProgram().addPerRenderUniform(new UniformValueProperty<RenderOutputData>("minColour") {
      @Override
      public Vector3f getUniformValue(RenderState<RenderOutputData> state) {
        return minColour;
      }
    });
  }
}
