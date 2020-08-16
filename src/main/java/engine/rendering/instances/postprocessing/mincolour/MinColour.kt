package engine.rendering.instances.postprocessing.mincolour

import engine.rendering.RenderOutputData
import engine.rendering.abstracted.postprocessing.AbstractPostProcessor
import engine.rendering.abstracted.postprocessing.PostProcessor
import engine.utils.libraryBindings.maths.joml.Vector3f
import engine.utils.libraryBindings.maths.utils.Vector3
import engine.utils.libraryBindings.opengl.shaders.RenderState
import engine.utils.libraryBindings.opengl.shaders.UniformTextureProperty
import engine.utils.libraryBindings.opengl.shaders.UniformValueProperty
import engine.utils.libraryBindings.opengl.textures.ITexture

class MinColour constructor(r: Float, g: Float, b: Float)
    : AbstractPostProcessor(FRAG_FILE), PostProcessor {

    val minColour: Vector3f = Vector3.of(r, g, b)

    init {
        shadersProgram.addPerRenderUniform(object : UniformTextureProperty<RenderOutputData>("textureSampler", 0) {
            override fun getUniformValue(state: RenderState<RenderOutputData>): ITexture {
                return state.instance.colour
            }
        })
        shadersProgram.addPerRenderUniform(object : UniformValueProperty<RenderOutputData>("minColour") {
            override fun getUniformValue(state: RenderState<RenderOutputData>): Vector3f {
                return minColour
            }
        })
    }

    companion object {

        private const val FRAG_FILE = "/shaders/postprocessing/mincolour/minColourFrag.glsl"

    }

}
