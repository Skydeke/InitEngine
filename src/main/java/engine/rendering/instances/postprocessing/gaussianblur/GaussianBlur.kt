package engine.rendering.instances.postprocessing.gaussianblur

import engine.rendering.RenderOutputData
import engine.rendering.abstracted.postprocessing.AbstractPostProcessor
import engine.rendering.abstracted.postprocessing.PostProcessor
import engine.utils.libraryBindings.opengl.constants.DataType
import engine.utils.libraryBindings.opengl.constants.FormatType
import engine.utils.libraryBindings.opengl.fbos.Fbo
import engine.utils.libraryBindings.opengl.fbos.FboTarget
import engine.utils.libraryBindings.opengl.fbos.attachment.TextureAttachment
import engine.utils.libraryBindings.opengl.shaders.RenderState
import engine.utils.libraryBindings.opengl.shaders.UniformIntProperty
import engine.utils.libraryBindings.opengl.shaders.UniformTextureProperty
import engine.utils.libraryBindings.opengl.shaders.old.Uniform
import engine.utils.libraryBindings.opengl.textures.ITexture
import engine.utils.libraryBindings.opengl.textures.TextureConfigs
import engine.utils.libraryBindings.opengl.textures.parameters.WrapParameter
import engine.utils.libraryBindings.opengl.utils.GlBuffer
import engine.utils.libraryBindings.opengl.utils.GlUtils

class GaussianBlur(private var stages: Int, private val scale: Float) : AbstractPostProcessor(FRAG_FILE),
    PostProcessor {

    private val verticalBlur = Uniform.createBool(shadersProgram, "verticalBlur")

    init {
        shadersProgram.addPerRenderUniform(object : UniformTextureProperty<RenderOutputData>("textureSampler", 0) {
            override fun getUniformValue(state: RenderState<RenderOutputData>): ITexture {
                return state.instance.colour
            }
        })
        shadersProgram.addPerRenderUniform(object : UniformIntProperty<RenderOutputData>("width") {
            override fun getUniformValue(state: RenderState<RenderOutputData>): Int {
                return fbo.width
            }
        })
        shadersProgram.addPerRenderUniform(object : UniformIntProperty<RenderOutputData>("height") {
            override fun getUniformValue(state: RenderState<RenderOutputData>): Int {
                return fbo.height
            }
        })
    }


    override fun createFbo(width: Int, height: Int): Fbo {
        val fbo = Fbo.create((width / scale).toInt(), (height / scale).toInt())
        fbo.addAttachment(
            TextureAttachment.ofColour(0, TextureConfigs(
                FormatType.RGB8, FormatType.RGB, DataType.U_BYTE)
            ))
        fbo.attachments[0].texture.functions
                .borderColour(0f, 0f, 0f, 0f)
                .wrapS(WrapParameter.CLAMP_TO_BORDER)
                .wrapT(WrapParameter.CLAMP_TO_BORDER)
        return fbo
    }

    override fun process(renderOutputData: RenderOutputData) {
        fbo.bind(FboTarget.DRAW_FRAMEBUFFER)
        GlUtils.clear(GlBuffer.COLOUR)
        shadersProgram.bind()

        shadersProgram.updatePerRenderUniforms(RenderState(null, renderOutputData, null, 0))
        shadersProgram.updatePerInstanceUniforms(RenderState(null, renderOutputData, null, 0))

        verticalBlur.load(true)
        draw()
        texture.bind(0)
        for (i in 1 until stages) {
            draw()
        }
        verticalBlur.load(false)
        for (i in 0 until stages) {
            draw()
        }

        shadersProgram.unbind()
    }

    companion object {
        private const val FRAG_FILE = "/shaders/postprocessing/gaussianblur/gaussianBlurFragment.glsl"
    }


}
