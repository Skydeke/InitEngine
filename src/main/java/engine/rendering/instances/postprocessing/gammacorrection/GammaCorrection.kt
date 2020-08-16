package engine.rendering.instances.postprocessing.gammacorrection

import engine.rendering.RenderOutputData
import engine.rendering.abstracted.postprocessing.AbstractPostProcessor
import engine.utils.libraryBindings.opengl.shaders.RenderState
import engine.utils.libraryBindings.opengl.shaders.UniformFloatProperty
import engine.utils.libraryBindings.opengl.shaders.UniformTextureProperty
import engine.utils.libraryBindings.opengl.textures.ITexture

class GammaCorrection(var factor: Float = 2.2f) : AbstractPostProcessor(FRAG_FILE) {

    init {
        shadersProgram.addPerRenderUniform(object : UniformTextureProperty<RenderOutputData>("textureSampler", 0) {
            override fun getUniformValue(state: RenderState<RenderOutputData>): ITexture {
                return state.instance.colour
            }
        })
        shadersProgram.addPerRenderUniform(object : UniformFloatProperty<RenderOutputData>("factor") {
            override fun getUniformValue(state: RenderState<RenderOutputData>): Float {
                return factor
            }
        })
    }

    companion object {
        private const val FRAG_FILE = "/shaders/postprocessing/gammacorrection/GammaCorrectionFragment.glsl"
    }

}
