package engine.rendering.instances.renderers.shadow

import engine.architecture.scene.SceneContext
import engine.architecture.scene.entity.Entity
import engine.architecture.scene.light.LightManager
import engine.architecture.scene.node.Node
import engine.rendering.abstracted.renderers.Renderer3D
import engine.utils.libraryBindings.maths.joml.FrustumIntersection
import engine.utils.libraryBindings.maths.joml.Vector3f
import engine.utils.libraryBindings.maths.utils.Matrix4
import engine.utils.libraryBindings.opengl.shaders.RenderState
import engine.utils.libraryBindings.opengl.shaders.ShadersProgram
import engine.utils.libraryBindings.opengl.shaders.UniformValue
import engine.utils.libraryBindings.opengl.shaders.UniformValueProperty

internal class ShadowRenderer : Renderer3D<Entity>() {

    private val shadersProgram: ShadersProgram<Entity>
    private val frustumIntersection: FrustumIntersection = FrustumIntersection()

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

        for (model in renderList.keys) {
            for (i in model.meshes.indices) {
                model.bindAndConfigure(i)
                for (entity in renderList[model]!!){
                    frustumIntersection.set(context.camera.projectionViewMatrix.mul(
                        entity.transform.transformationMatrix, Matrix4.pool.poolAndGive()))
                    if (!checkRenderPass(entity)) {
                        continue
                    }
                    val instanceState = RenderState<Entity>(this, entity, context.camera, i)
                    shadersProgram.updatePerInstanceUniforms(instanceState)
                    model.render(instanceState, i)
                }
                model.unbind(i);
            }
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

        for (model in renderList.keys) {
            for (i in 0..model.meshes.size) {
                model.bindAndConfigure(i)
                for (entity in renderList[model]!!){
                    frustumIntersection.set(context.camera.projectionViewMatrix.mul(
                        entity.transform.transformationMatrix, Matrix4.pool.poolAndGive()))
                    if (!checkRenderPass(entity)) {
                        continue
                    }
                    val instanceState = RenderState<Entity>(this, entity, context.camera, i)
                    shadersProgram.updatePerInstanceUniforms(instanceState)
                    model.render(instanceState, i)
                }
                model.unbind(i);
            }
        }

        shadersProgram.unbind()
    }

    private fun checkRenderPass(entity: Entity): Boolean {
        return !checkClippingCulling(entity.transform.position) &&
                frustumIntersection.testAab(entity.model.bounds.min.mul(entity.transform.scale, Vector3f()), entity.model.bounds.max.mul(entity.transform.scale, Vector3f()))
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
