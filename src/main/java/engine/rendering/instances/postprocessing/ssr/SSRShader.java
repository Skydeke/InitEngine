package engine.rendering.instances.postprocessing.ssr;

import engine.architecture.system.Config;
import engine.rendering.Shader;
import engine.utils.libraryBindings.maths.joml.Matrix4f;
import engine.utils.libraryBindings.opengl.textures.TextureObject;

import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_READ_WRITE;
import static org.lwjgl.opengl.GL30.*;

public class SSRShader extends Shader {

    SSRShader() {
        createComputeShader("res/shaders/ssr/ssr_cs.glsl");
        link();

        addUniform("resolution");
        addUniform("viewMatrix");
        addUniform("projectionMatrix");

        addUniform("raymarchSteps");
        addUniform("binarySearchSteps");
        addUniform("rayStepLen");
        addUniform("falloffExp");
        addUniform("sampleCount");
    }


    public void compute(TextureObject worldPos, TextureObject worldNorm,
                        TextureObject ao, TextureObject out) {
        bind();

        setUniform("resolution", boundContext.getResolution());
        setUniform("viewMatrix", new Matrix4f(boundContext.getCamera().getViewMatrix()));
        setUniform("projectionMatrix", new Matrix4f(boundContext.getCamera().getProjectionMatrix()));

        setUniform("raymarchSteps", Config.instance().getSsrRaymarchSteps());
        setUniform("binarySearchSteps", Config.instance().getSsrBinarySearchSteps());
        setUniform("rayStepLen", Config.instance().getSsrRayStepLen());
        setUniform("falloffExp", Config.instance().getSsrFalloff());
        setUniform("sampleCount", Config.instance().getSsrSamples());

        setUniform("ssao", Config.instance().isSsao() ? 1 : 0);

        bindImage(0, worldPos.getId(), GL_READ_ONLY, GL_RGBA32F);
        bindImage(1, worldNorm.getId(), GL_READ_ONLY, GL_RGBA32F);
        bindImage(2, ao.getId(), GL_READ_ONLY, GL_R16F);
        bindImage(3, out.getId(), GL_READ_WRITE, GL_RGBA16F);

        compute(16, 16);
        unbind();

    }
}
