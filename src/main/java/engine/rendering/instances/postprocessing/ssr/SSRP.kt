package engine.rendering.instances.postprocessing.ssr

import engine.architecture.scene.SceneContext
import engine.architecture.scene.entity.Entity
import engine.architecture.system.AppContext
import engine.architecture.system.Config
import engine.rendering.RenderOutputData
import engine.rendering.abstracted.postprocessing.AbstractPostProcessor
import engine.utils.libraryBindings.opengl.fbos.SceneFbo
import engine.utils.libraryBindings.opengl.shaders.*
import engine.utils.libraryBindings.opengl.textures.ITexture
import engine.utils.libraryBindings.opengl.textures.Texture

class SSRP() : AbstractPostProcessor(FRAG_FILE) {

    init {
        shadersProgram.addPerRenderUniform(object : UniformValueProperty<RenderOutputData>("resolution") {
            override fun getUniformValue(state: RenderState<RenderOutputData>): UniformValue {
                return AppContext.instance().sceneContext.resolution
            }
        })
        shadersProgram.addPerRenderUniform(object : UniformValueProperty<RenderOutputData>("viewMatrix") {
            override fun getUniformValue(state: RenderState<RenderOutputData>): UniformValue {
                return AppContext.instance().sceneContext.camera.viewMatrix
            }
        })
        shadersProgram.addPerRenderUniform(object : UniformValueProperty<RenderOutputData>("projectionMatrix") {
            override fun getUniformValue(state: RenderState<RenderOutputData>): UniformValue {
                return AppContext.instance().sceneContext.camera.projectionMatrix
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
                return Texture.NONE
            }
        })
        shadersProgram.addPerInstanceUniform(object : UniformTextureProperty<RenderOutputData>("outImage", 3) {
            override fun getUniformValue(state: RenderState<RenderOutputData>): ITexture? {
                return SceneFbo.getInstance().attachments[0].texture
            }
        })
    }

    companion object {
        private const val FRAG_FILE = "/shaders/postprocessing/ssr/ssr_cs.glsl"
    }

}
