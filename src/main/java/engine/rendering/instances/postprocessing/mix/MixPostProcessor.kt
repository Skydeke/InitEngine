package engine.rendering.instances.postprocessing.mix

import engine.rendering.RenderOutputData
import engine.rendering.abstracted.postprocessing.AbstractPostProcessor
import engine.rendering.abstracted.postprocessing.PostProcessor
import engine.utils.libraryBindings.opengl.shaders.RenderState
import engine.utils.libraryBindings.opengl.shaders.UniformFloatProperty
import engine.utils.libraryBindings.opengl.shaders.UniformTextureProperty
import engine.utils.libraryBindings.opengl.textures.ITexture
import engine.utils.libraryBindings.opengl.textures.Texture

class MixPostProcessor() : AbstractPostProcessor(FRAG_FILE), PostProcessor {

    constructor(scalar: Float) : this() {
        this.scalar = scalar
    }

    constructor(combination: Texture) : this() {
        this.combination = combination
    }

    var scalar: Float = .5f
    var combination: Texture = Texture.NONE

    init {
        shadersProgram.addPerRenderUniform(object : UniformTextureProperty<RenderOutputData>("texture1", 0) {
            override fun getUniformValue(state: RenderState<RenderOutputData>): ITexture {
                return state.instance.colour
            }
        })
        shadersProgram.addPerRenderUniform(object : UniformTextureProperty<RenderOutputData>("texture2", 1) {
            override fun getUniformValue(state: RenderState<RenderOutputData>): ITexture {
                return combination
            }
        })
        shadersProgram.addPerRenderUniform(object : UniformFloatProperty<RenderOutputData>("scalar") {
            override fun getUniformValue(state: RenderState<RenderOutputData>): Float {
                return scalar
            }
        })
    }

    companion object {
        private const val FRAG_FILE = "/shaders/postprocessing/mix/MixFragment.glsl"
    }
}
