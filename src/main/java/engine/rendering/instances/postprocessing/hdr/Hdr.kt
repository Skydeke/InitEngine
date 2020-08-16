package engine.rendering.instances.postprocessing.hdr

import engine.rendering.RenderOutputData
import engine.rendering.abstracted.postprocessing.AbstractPostProcessor
import engine.utils.libraryBindings.opengl.shaders.RenderState
import engine.utils.libraryBindings.opengl.shaders.UniformFloatProperty
import engine.utils.libraryBindings.opengl.shaders.UniformTextureProperty
import engine.utils.libraryBindings.opengl.textures.ITexture


class Hdr(var gamma: Float = 2.2f, var exposure: Float = 1.0f) : AbstractPostProcessor(FRAG_FILE) {

    init {
        shadersProgram.addPerRenderUniform(object : UniformTextureProperty<RenderOutputData>("textureSampler", 0) {
            override fun getUniformValue(state: RenderState<RenderOutputData>): ITexture {
                return state.instance.colour
            }
        })
        shadersProgram.addPerRenderUniform(object : UniformFloatProperty<RenderOutputData>("gamma") {
            override fun getUniformValue(state: RenderState<RenderOutputData>): Float {
                return gamma
            }
        })
        shadersProgram.addPerRenderUniform(object : UniformFloatProperty<RenderOutputData>("exposure") {
            override fun getUniformValue(state: RenderState<RenderOutputData>): Float {
                return exposure
            }
        })
    }

    companion object {
        private const val FRAG_FILE = "/shaders/postprocessing/hdr/HdrFragment.glsl"
    }

}
