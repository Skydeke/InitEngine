package engine.rendering.instances.renderers.shadow

import engine.architecture.scene.SceneContext
import engine.architecture.scene.entity.Entity
import engine.architecture.scene.light.LightManager
import engine.architecture.scene.node.Node
import engine.rendering.abstracted.renderers.Renderer3D
import engine.utils.libraryWrappers.opengl.shaders.RenderState
import engine.utils.libraryWrappers.opengl.shaders.ShadersProgram
import engine.utils.libraryWrappers.opengl.shaders.UniformValue
import engine.utils.libraryWrappers.opengl.shaders.UniformValueProperty

internal class ShadowRenderer : Renderer3D<Entity>() {

    private val shadersProgram: ShadersProgram<Entity>

    init {
        this.shadersProgram = ShadersProgram.create(VERT_FILE, FRAG_FILE)
        shadersProgram.addPerRenderUniform(object : UniformValueProperty<Entity>("lightSpaceMatrix") {
            override fun getUniformValue(state: RenderState<Entity>): UniformValue {
                return LightManager.getSun().lightSpaceMatrix
            }
        })
        shadersProgram.addPerInstanceUniform(object : UniformValueProperty<Entity>("modelMatrix") {
            override fun getUniformValue(state: RenderState<Entity>): UniformValue {
                return state.instance.transform.transformationMatrix
            }
        })
    }

    override fun render(context: SceneContext) {
        if (renderList.size < 1) {
            return
        }
        shadersProgram.bind()
        val renderState = RenderState<Entity>(this, context.camera)
        shadersProgram.updatePerRenderUniforms(renderState)

        for (entity in renderList) {
            val instanceState = RenderState<Entity>(this, entity, context.camera)
            shadersProgram.updatePerInstanceUniforms(instanceState)
            entity.model.render(instanceState, shadersProgram)
        }
        shadersProgram.unbind()
    }

    override fun render(context: SceneContext, condition: Node.Condition) {
        if (renderList.size < 1) {
            return
        }
        shadersProgram.bind()
        val renderState = RenderState<Entity>(this, context.camera)
        shadersProgram.updatePerRenderUniforms(renderState)

        for (entity in renderList) {
            if (entity.isActivated && condition.isvalid(entity)) {
                val instanceState = RenderState<Entity>(this, entity, context.camera)
                shadersProgram.updatePerInstanceUniforms(instanceState)
                entity.model.render(instanceState, shadersProgram)
            }
        }
        shadersProgram.unbind()
    }

    override fun cleanUp() {
        shadersProgram.delete()
    }

    companion object {

        private const val VERT_FILE = "res/shaders/shadow/shadow_vs.glsl"
        private const val FRAG_FILE = "res/shaders/shadow/shadow_fs.glsl"

        @JvmStatic
        val instance: ShadowRenderer = ShadowRenderer()

    }
}
