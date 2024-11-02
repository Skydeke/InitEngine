package engine.rendering.instances.postprocessing.celshading;

import engine.rendering.RenderOutputData;
import engine.rendering.abstracted.postprocessing.AbstractPostProcessor;
import engine.utils.libraryBindings.opengl.shaders.RenderState;
import engine.utils.libraryBindings.opengl.shaders.UniformTextureProperty;
import engine.utils.libraryBindings.opengl.textures.ITexture;

public class CelShading extends AbstractPostProcessor {

  private static final String FRAG_FILE = "/shaders/postprocessing/celshading/CelShadingFragment.glsl";

  public CelShading() throws Exception {
    super(FRAG_FILE);

    getShadersProgram().addPerRenderUniform(new UniformTextureProperty<RenderOutputData>("textureSampler", 0) {
      @Override
      public ITexture getUniformValue(RenderState<RenderOutputData> state) {
        return state.getInstance().getColour();
      }
    });
  }
}
