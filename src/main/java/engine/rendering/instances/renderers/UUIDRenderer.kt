package engine.rendering.instances.renderers

import engine.architecture.scene.SceneContext
import engine.architecture.scene.entity.Entity
import engine.architecture.scene.node.Node
import engine.rendering.abstracted.renderers.Renderer3D
import engine.utils.libraryBindings.maths.joml.FrustumIntersection
import engine.utils.libraryBindings.maths.joml.Vector3f
import engine.utils.libraryBindings.maths.utils.Matrix4
import engine.utils.libraryBindings.opengl.shaders.RenderState
import engine.utils.libraryBindings.opengl.shaders.ShadersProgram
import engine.utils.libraryBindings.opengl.shaders.UniformValue
import engine.utils.libraryBindings.opengl.shaders.UniformValueProperty

internal class UUIDRenderer : Renderer3D<Entity>() {

    private val shadersProgram: ShadersProgram<Entity>
    private val frustumIntersection: FrustumIntersection = FrustumIntersection()


    init {
        this.shadersProgram = ShadersProgram.create(
            VERT_FILE,
            FRAG_FILE
        )

        shadersProgram.addPerInstanceUniform(object : UniformValueProperty<Entity>("color") {
            override fun getUniformValue(state: RenderState<Entity>): UniformValue {
//                return Picking.getUUIDColor(state.instance.uuid)
                return Vector3f(0f,0f,0f)
            }
        })
        shadersProgram.addPerRenderUniform(object : UniformValueProperty<Entity>("projectionMatrix") {
            override fun getUniformValue(state: RenderState<Entity>): UniformValue {
                return getContext().camera.projectionMatrix
            }
        })
        shadersProgram.addPerInstanceUniform(object : UniformValueProperty<Entity>("modelMatrix") {
            override fun getUniformValue(state: RenderState<Entity>): UniformValue {
                return state.instance.transform.transformationMatrix
            }
        })
        shadersProgram.addPerRenderUniform(object : UniformValueProperty<Entity>("viewMatrix") {
            override fun getUniformValue(state: RenderState<Entity>): UniformValue {
                return getContext().camera.viewMatrix
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
            for (i in model.meshes.indices) {
                model.bindAndConfigure(i)
                for (entity in renderList[model]!!){
                    frustumIntersection.set(context.camera.projectionViewMatrix.mul(
                        entity.transform.transformationMatrix, Matrix4.pool.poolAndGive()))
                    if (!checkRenderPass(entity) && condition.isvalid(entity)) {
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
                frustumIntersection.testAab(entity.model.bounds.min, entity.model.bounds.max)
    }

    override fun cleanUp() {
        shadersProgram.delete()
    }

    companion object {

        private const val VERT_FILE = "res/shaders/picking/UUID_vs.glsl"
        private const val FRAG_FILE = "res/shaders/picking/UUID_fs.glsl"

        @JvmStatic
        val instance: UUIDRenderer =
            UUIDRenderer()

    }
}
