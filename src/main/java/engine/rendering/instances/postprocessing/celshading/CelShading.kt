package engine.rendering.instances.postprocessing.celshading

import engine.rendering.RenderOutputData
import engine.rendering.abstracted.postprocessing.AbstractPostProcessor
import engine.rendering.abstracted.postprocessing.PostProcessor
import engine.utils.libraryBindings.opengl.shaders.RenderState
import engine.utils.libraryBindings.opengl.shaders.UniformTextureProperty
import engine.utils.libraryBindings.opengl.textures.ITexture


class CelShading : AbstractPostProcessor(FRAG_FILE), PostProcessor {

    init {
        shadersProgram.addPerRenderUniform(object : UniformTextureProperty<RenderOutputData>("textureSampler", 0) {
            override fun getUniformValue(state: RenderState<RenderOutputData>): ITexture {
                return state.instance.colour
            }
        })
    }

    companion object {
        private const val FRAG_FILE = "/shaders/postprocessing/celshading/CelShadingFragment.glsl"
    }
}
