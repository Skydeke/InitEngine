package engine.rendering.instances.renderers.sky

import engine.architecture.scene.SceneContext
import engine.architecture.scene.node.Node
import engine.rendering.abstracted.renderers.Renderer3D
import engine.utils.libraryWrappers.opengl.shaders.*
import engine.utils.libraryWrappers.opengl.utils.GlUtils

internal class SkyRenderer : Renderer3D<Sky>() {

    private val shadersProgram: ShadersProgram<Sky>

    init {
        this.shadersProgram = ShadersProgram.create(VERT_FILE, FRAG_FILE)

        shadersProgram.addPerRenderUniform(object : UniformValueProperty<Sky>("viewProjectionMatrix") {
            override fun getUniformValue(state: RenderState<Sky>): UniformValue {
                return getContext().camera.projectionViewMatrix
            }
        })
        shadersProgram.addPerInstanceUniform(object : UniformValueProperty<Sky>("modelMatrix") {
            override fun getUniformValue(state: RenderState<Sky>): UniformValue {
                return state.instance.transform.transformationMatrix
            }
        })
        shadersProgram.addPerInstanceUniform(object : UniformFloatProperty<Sky>("scale") {
            override fun getUniformValue(state: RenderState<Sky>): Float {
                return state.instance.transform.scale.y
            }
        })
    }

    override fun render(context: SceneContext) {
        if (renderList.size < 1) {
            return
        }
        shadersProgram.bind()

        val renderState = RenderState<Sky>(this, context.camera)
        shadersProgram.updatePerRenderUniforms(renderState)

        for (entity in renderList) {
            val instanceState = RenderState<Sky>(this, entity, context.camera)
            shadersProgram.updatePerInstanceUniforms(instanceState)
            GlUtils.disableCulling()
            entity.model.render(instanceState, shadersProgram)
            GlUtils.enableCulling()
        }

        shadersProgram.unbind()
    }

    override fun render(context: SceneContext, condition: Node.Condition) {
        if (renderList.size < 1) {
            return
        }
        shadersProgram.bind()

        val renderState = RenderState<Sky>(this, context.camera)
        shadersProgram.updatePerRenderUniforms(renderState)

        for (entity in renderList) {
            if (entity.isActivated && condition.isvalid(entity)) {
                val instanceState = RenderState<Sky>(this, entity, context.camera)
                shadersProgram.updatePerInstanceUniforms(instanceState)
                GlUtils.disableCulling()
                entity.model.render(instanceState, shadersProgram)
                GlUtils.enableCulling()
            }
        }
        shadersProgram.unbind()
    }

    override fun cleanUp() {
        shadersProgram.delete()
    }

    companion object {

        private const val VERT_FILE = "res/shaders/sky/sky_vert.glsl"
        private const val FRAG_FILE = "res/shaders/sky/sky_frag.glsl"

        @JvmStatic
        val instance: SkyRenderer = SkyRenderer()

    }
}
