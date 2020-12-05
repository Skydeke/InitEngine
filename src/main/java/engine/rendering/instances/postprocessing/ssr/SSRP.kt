package engine.rendering.instances.postprocessing.ssr

import engine.architecture.system.AppContext
import engine.architecture.system.Config
import engine.rendering.RenderOutputData
import engine.rendering.abstracted.postprocessing.AbstractPostProcessor
import engine.utils.libraryBindings.maths.joml.Matrix4f
import engine.utils.libraryBindings.opengl.shaders.*
import engine.utils.libraryBindings.opengl.textures.ITexture

class SSRP() : AbstractPostProcessor(CS_FILE) {

    init {
        shadersProgram.addPerRenderUniform(object : UniformValueProperty<RenderOutputData>("resolution") {
            override fun getUniformValue(state: RenderState<RenderOutputData>): UniformValue {
                return AppContext.instance().sceneContext.resolution
            }
        })
        shadersProgram.addPerRenderUniform(object : UniformValueProperty<RenderOutputData>("viewMatrix") {
            override fun getUniformValue(state: RenderState<RenderOutputData>): UniformValue {
                return Matrix4f(AppContext.instance().sceneContext.camera.viewMatrix)
            }
        })
        shadersProgram.addPerRenderUniform(object : UniformValueProperty<RenderOutputData>("projectionMatrix") {
            override fun getUniformValue(state: RenderState<RenderOutputData>): UniformValue {
                return Matrix4f(AppContext.instance().sceneContext.camera.projectionMatrix)
            }
        })

        shadersProgram.addPerRenderUniform(object : UniformIntProperty<RenderOutputData>("raymarchSteps") {
            override fun getUniformValue(state: RenderState<RenderOutputData>): Int {
                return Config.instance().ssrRaymarchSteps
            }
        })
        shadersProgram.addPerRenderUniform(object : UniformIntProperty<RenderOutputData>("binarySearchSteps") {
            override fun getUniformValue(state: RenderState<RenderOutputData>): Int {
                return Config.instance().ssrBinarySearchSteps
            }
        })
        shadersProgram.addPerRenderUniform(object : UniformFloatProperty<RenderOutputData>("rayStepLen") {
            override fun getUniformValue(state: RenderState<RenderOutputData>): Float {
                return Config.instance().ssrRayStepLen
            }
        })
        shadersProgram.addPerRenderUniform(object : UniformFloatProperty<RenderOutputData>("falloffExp") {
            override fun getUniformValue(state: RenderState<RenderOutputData>): Float {
                return Config.instance().ssrFalloff
            }
        })
        shadersProgram.addPerRenderUniform(object : UniformIntProperty<RenderOutputData>("sampleCount") {
            override fun getUniformValue(state: RenderState<RenderOutputData>): Int {
                return Config.instance().ssrSamples
            }
        })

        shadersProgram.addPerRenderUniform(object : UniformBooleanProperty<RenderOutputData>("ssao") {
            override fun getUniformValue(state: RenderState<RenderOutputData>): Boolean {
                return false
//                return Config.instance().isSsao
            }
        })

        shadersProgram.addPerInstanceUniform(object : UniformTextureProperty<RenderOutputData>("positionImage", 0) {
            override fun getUniformValue(state: RenderState<RenderOutputData>): ITexture? {
                return state.instance.position
            }
        })
        shadersProgram.addPerInstanceUniform(object : UniformTextureProperty<RenderOutputData>("normalImage", 1) {
            override fun getUniformValue(state: RenderState<RenderOutputData>): ITexture? {
                return state.instance.normal
            }
        })
        shadersProgram.addPerInstanceUniform(object : UniformTextureProperty<RenderOutputData>("aoImage", 2) {
            override fun getUniformValue(state: RenderState<RenderOutputData>): ITexture? {
                return AppContext.instance().sceneContext.pipeline.ssaoPass.targetTexture.texture;
            }
        })
        shadersProgram.addPerInstanceUniform(object : UniformTextureProperty<RenderOutputData>("lastFrameImage", 3) {
            override fun getUniformValue(state: RenderState<RenderOutputData>): ITexture? {
                return AppContext.instance().sceneContext.outputData.colour
            }
        })
    }

    companion object {
        private const val CS_FILE = "/shaders/postprocessing/ssr/ssr_fs.glsl"
    }

}
