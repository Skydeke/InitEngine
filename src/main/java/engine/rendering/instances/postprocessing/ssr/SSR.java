package engine.rendering.instances.postprocessing.ssr;

import engine.architecture.system.Pipeline;
import engine.utils.libraryBindings.opengl.fbos.SceneFbo;
import engine.utils.libraryBindings.opengl.textures.ITexture;


/**
 * SREEN SPACE REFLECTION
 */
public class SSR {

    private SSRShader ssrShader;

    private Pipeline pipeline;

    public SSR(Pipeline pipeline) {
        this.pipeline = pipeline;
        ssrShader = new SSRShader();
    }

    public void compute(ITexture worldPos, ITexture worldNorm, ITexture ao) {
        ssrShader.compute(worldPos, worldNorm, ao, SceneFbo.getInstance().getAttachments().get(0).getTexture());
    }

}
