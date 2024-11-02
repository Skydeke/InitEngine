package engine.rendering.instances.postprocessing.ssr;

import engine.architecture.system.AppContext;
import engine.architecture.system.Config;
import engine.rendering.RenderOutputData;
import engine.rendering.abstracted.postprocessing.AbstractPostProcessor;
import engine.utils.libraryBindings.maths.joml.Matrix4f;
import engine.utils.libraryBindings.opengl.shaders.*;
import engine.utils.libraryBindings.opengl.textures.ITexture;

public class SSRP extends AbstractPostProcessor {

  private static final String CS_FILE = "/shaders/postprocessing/ssr/ssr_cs.glsl";

  public SSRP() throws Exception {
    super(CS_FILE);
    initialize();
  }

  private void initialize() {
    getShadersProgram().addPerRenderUniform(new UniformValueProperty<RenderOutputData>("resolution") {
      @Override
      public UniformValue getUniformValue(RenderState<RenderOutputData> state) {
        return AppContext.instance().sceneContext.getResolution();
      }
    });

    getShadersProgram().addPerRenderUniform(new UniformValueProperty<RenderOutputData>("viewMatrix") {
      @Override
      public UniformValue getUniformValue(RenderState<RenderOutputData> state) {
        return new Matrix4f(AppContext.instance().sceneContext.getCamera().getViewMatrix());
      }
    });

    getShadersProgram().addPerRenderUniform(new UniformValueProperty<RenderOutputData>("projectionMatrix") {
      @Override
      public UniformValue getUniformValue(RenderState<RenderOutputData> state) {
        return new Matrix4f(AppContext.instance().sceneContext.getCamera().getProjectionMatrix());
      }
    });

    getShadersProgram().addPerRenderUniform(new UniformIntProperty<RenderOutputData>("raymarchSteps") {
      @Override
      public int getUniformValue(RenderState<RenderOutputData> state) {
        return Config.instance().getSsrRaymarchSteps();
      }
    });

    getShadersProgram().addPerRenderUniform(new UniformIntProperty<RenderOutputData>("binarySearchSteps") {
      @Override
      public int getUniformValue(RenderState<RenderOutputData> state) {
        return Config.instance().getSsrBinarySearchSteps();
      }
    });

    getShadersProgram().addPerRenderUniform(new UniformFloatProperty<RenderOutputData>("rayStepLen") {
      @Override
      public float getUniformValue(RenderState<RenderOutputData> state) {
        return Config.instance().getSsrRayStepLen();
      }
    });

    getShadersProgram().addPerRenderUniform(new UniformFloatProperty<RenderOutputData>("falloffExp") {
      @Override
      public float getUniformValue(RenderState<RenderOutputData> state) {
        return Config.instance().getSsrFalloff();
      }
    });

    getShadersProgram().addPerRenderUniform(new UniformIntProperty<RenderOutputData>("sampleCount") {
      @Override
      public int getUniformValue(RenderState<RenderOutputData> state) {
        return Config.instance().getSsrSamples();
      }
    });

    getShadersProgram().addPerRenderUniform(new UniformBooleanProperty<RenderOutputData>("ssao") {
      @Override
      public boolean getUniformValue(RenderState<RenderOutputData> state) {
        return Config.instance().isSsao();
      }
    });

    getShadersProgram().addPerInstanceUniform(new UniformTextureProperty<RenderOutputData>("positionImage", 0) {
      @Override
      public ITexture getUniformValue(RenderState<RenderOutputData> state) {
        return state.getInstance().position;
      }
    });

    getShadersProgram().addPerInstanceUniform(new UniformTextureProperty<RenderOutputData>("normalImage", 1) {
      @Override
      public ITexture getUniformValue(RenderState<RenderOutputData> state) {
        return state.getInstance().normal;
      }
    });

    getShadersProgram().addPerInstanceUniform(new UniformTextureProperty<RenderOutputData>("aoImage", 2) {
      @Override
      public ITexture getUniformValue(RenderState<RenderOutputData> state) {
        return AppContext.instance().sceneContext.pipeline.getSsaoPass().targetTexture.getTexture();
      }
    });

    getShadersProgram().addPerInstanceUniform(new UniformTextureProperty<RenderOutputData>("lastFrameImage", 3) {
      @Override
      public ITexture getUniformValue(RenderState<RenderOutputData> state) {
        return AppContext.instance().sceneContext.getOutputData().colour;
      }
    });
  }
}
