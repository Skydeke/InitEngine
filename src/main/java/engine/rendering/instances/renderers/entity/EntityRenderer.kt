package engine.rendering.instances.renderers.entity

import engine.architecture.scene.SceneContext
import engine.architecture.scene.entity.Entity
import engine.architecture.scene.node.Node
import engine.rendering.abstracted.renderers.Renderer3D
import engine.utils.libraryBindings.maths.joml.FrustumIntersection
import engine.utils.libraryBindings.maths.joml.Vector3f
import engine.utils.libraryBindings.maths.utils.Matrix4
import engine.utils.libraryBindings.opengl.shaders.*
import engine.utils.libraryBindings.opengl.textures.ITexture

internal class EntityRenderer : Renderer3D<Entity>() {

    private val shadersProgram: ShadersProgram<Entity>
    private val frustumIntersection: FrustumIntersection = FrustumIntersection()

    init {
        this.shadersProgram = ShadersProgram.create(VERT_FILE, FRAG_FILE)

        shadersProgram.addPerInstanceUniform(object : UniformValueProperty<Entity>("albedoConst") {
            override fun getUniformValue(state: RenderState<Entity>): Vector3f {
                return Vector3f(
                    state.mesh.material.diffuseColor.x,
                    state.mesh.material.diffuseColor.y,
                    state.mesh.material.diffuseColor.z
                )
            }
        })
        shadersProgram.addPerInstanceUniform(object : UniformTextureProperty<Entity>("albedoTex", 0) {
            override fun getUniformValue(state: RenderState<Entity>): ITexture? {
                return state.mesh.material.diffuseTexture
            }
        })
        shadersProgram.addPerInstanceUniform(object : UniformBooleanProperty<Entity>("isAlbedoMapped") {
            override fun getUniformValue(state: RenderState<Entity>): Boolean {
                return state.mesh.material.hasTexture()
            }
        })

        shadersProgram.addPerInstanceUniform(object : UniformTextureProperty<Entity>("normalMap", 1) {
            override fun getUniformValue(state: RenderState<Entity>): ITexture? {
                return state.mesh.material.normalTexture
            }
        })
        shadersProgram.addPerInstanceUniform(object : UniformBooleanProperty<Entity>("map_normal") {
            override fun getUniformValue(state: RenderState<Entity>): Boolean {
                return state.mesh.material.isUseNormalTex
            }
        })


        shadersProgram.addPerInstanceUniform(object : UniformValueProperty<Entity>("projectionMatrix") {
            override fun getUniformValue(state: RenderState<Entity>): UniformValue {
                return getContext().camera.projectionMatrix
            }
        })
        shadersProgram.addPerInstanceUniform(object : UniformValueProperty<Entity>("modelMatrix") {
            override fun getUniformValue(state: RenderState<Entity>): UniformValue {
                return state.instance.transform.transformationMatrix
            }
        })
        shadersProgram.addPerInstanceUniform(object : UniformValueProperty<Entity>("viewMatrix") {
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
                for (entity in renderList[model]!!) {
                    frustumIntersection.set(
                        context.camera.projectionViewMatrix.mul(
                            entity.transform.transformationMatrix, Matrix4.pool.poolAndGive()
                        )
                    )
                    if (!checkRenderPass(entity) && entity.isActivated) {
                        continue
                    }
                    val instanceState = RenderState<Entity>(this, entity, context.camera, i)
                    shadersProgram.updatePerInstanceUniforms(instanceState)
                    model.render(instanceState, i)
                }
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
                for (entity in renderList[model]!!) {
                    frustumIntersection.set(
                        context.camera.projectionViewMatrix.mul(
                            entity.transform.transformationMatrix, Matrix4.pool.poolAndGive()
                        )
                    )
                    if (!checkRenderPass(entity) && entity.isActivated) {
                        continue
                    }
                    val instanceState = RenderState<Entity>(this, entity, context.camera, i)
                    shadersProgram.updatePerInstanceUniforms(instanceState)
                    model.render(instanceState, i)
                }
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

        private const val VERT_FILE = "res/shaders/entity/entity_vs.glsl"
        private const val FRAG_FILE = "res/shaders/entity/entity_fs.glsl"

        @JvmStatic
        val instance: EntityRenderer = EntityRenderer()

    }
}
