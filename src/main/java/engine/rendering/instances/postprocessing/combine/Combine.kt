package engine.rendering.instances.postprocessing.combine

import engine.rendering.RenderOutputData
import engine.rendering.abstracted.postprocessing.AbstractPostProcessor
import engine.rendering.abstracted.postprocessing.PostProcessor
import engine.utils.libraryBindings.opengl.shaders.RenderState
import engine.utils.libraryBindings.opengl.shaders.UniformFloatProperty
import engine.utils.libraryBindings.opengl.shaders.UniformTextureProperty
import engine.utils.libraryBindings.opengl.textures.ITexture
import engine.utils.libraryBindings.opengl.textures.Texture

class Combine() : AbstractPostProcessor(FRAG_FILE), PostProcessor {

    constructor(scalar1: Float, scalar2: Float) : this() {
        this.scalar1 = scalar1
        this.scalar2 = scalar2
    }

    constructor(combination: ITexture) : this() {
        this.combination = combination
    }

    var scalar1: Float = 1f
    var scalar2: Float = 1f
    var combination: ITexture = Texture.NONE

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
        shadersProgram.addPerRenderUniform(object : UniformFloatProperty<RenderOutputData>("scalar1") {
            override fun getUniformValue(state: RenderState<RenderOutputData>): Float {
                return scalar1
            }
        })
        shadersProgram.addPerRenderUniform(object : UniformFloatProperty<RenderOutputData>("scalar2") {
            override fun getUniformValue(state: RenderState<RenderOutputData>): Float {
                return scalar2
            }
        })
    }

    companion object {
        private const val FRAG_FILE = "/shaders/postprocessing/combine/combineFragment.glsl"
    }
}
