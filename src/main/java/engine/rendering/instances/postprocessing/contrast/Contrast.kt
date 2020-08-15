package engine.rendering.instances.postprocessing.contrast

import engine.rendering.RenderOutputData
import engine.rendering.abstracted.postprocessing.AbstractPostProcessor
import engine.utils.libraryBindings.opengl.shaders.RenderState
import engine.utils.libraryBindings.opengl.shaders.UniformFloatProperty
import engine.utils.libraryBindings.opengl.shaders.UniformTextureProperty
import engine.utils.libraryBindings.opengl.textures.ITexture
import engine.utils.libraryBindings.opengl.textures.Texture

class Contrast(var factor: Float = 1.4f) : AbstractPostProcessor(FRAG_FILE) {

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
        private const val FRAG_FILE = "/shaders/postprocessing/contrast/ContrastFragment.glsl"
    }

}
