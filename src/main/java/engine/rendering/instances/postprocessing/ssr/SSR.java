package engine.rendering.instances.postprocessing.ssr;

import engine.architecture.scene.SceneFbo;
import engine.architecture.system.Pipeline;
import engine.utils.libraryBindings.opengl.textures.TextureObject;


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

    public void compute(TextureObject worldPos, TextureObject worldNorm, TextureObject ao) {
        ssrShader.compute(worldPos, worldNorm, ao, SceneFbo.getInstance().getAttachment(0));
    }

}
