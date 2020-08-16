package engine.rendering.instances.postprocessing.radialblur

import engine.rendering.RenderOutputData
import engine.rendering.abstracted.postprocessing.AbstractPostProcessor
import engine.rendering.abstracted.postprocessing.PostProcessor
import engine.utils.lengths.ILength
import engine.utils.lengths.Pixels
import engine.utils.lengths.Proportion
import engine.utils.libraryBindings.maths.utils.Vector2
import engine.utils.libraryBindings.opengl.shaders.*
import engine.utils.libraryBindings.opengl.textures.ITexture

class RadialBlur(var samples: Int = 50, var factor: Float = 2f) : AbstractPostProcessor(FRAG_FILE), PostProcessor {

    var x: ILength = Proportion.of(.5f)
    var y: ILength = Proportion.of(.5f)

    constructor(x: Int, y: Int) : this() {
        setCenter(x, y)
    }

    init {
        shadersProgram.addPerRenderUniform(object : UniformTextureProperty<RenderOutputData>("textureSampler", 0) {
            override fun getUniformValue(state: RenderState<RenderOutputData>): ITexture {
                return state.instance.colour
            }
        })
        shadersProgram.addPerRenderUniform(object : UniformValueProperty<RenderOutputData>("center") {
            override fun getUniformValue(state: RenderState<RenderOutputData>): UniformValue {
                return Vector2.of(x.proportionTo(fbo.width).toFloat() / fbo.width,
                        y.proportionTo(fbo.height).toFloat() / fbo.height)
            }
        })
        shadersProgram.addPerRenderUniform(object : UniformIntProperty<RenderOutputData>("samples") {
            override fun getUniformValue(state: RenderState<RenderOutputData>): Int {
                return samples
            }
        })
        shadersProgram.addPerRenderUniform(object : UniformFloatProperty<RenderOutputData>("factor") {
            override fun getUniformValue(state: RenderState<RenderOutputData>): Float {
                return factor
            }
        })
    }

    fun setCenter(x: Int, y: Int) {
        this.x = Pixels(x)
        this.y = Pixels(y)
    }

    companion object {

        private const val FRAG_FILE = "/shaders/postprocessing/radialblur/radialBlurFrag.glsl"

    }

}
