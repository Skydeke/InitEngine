package engine.rendering.instances.renderers.pbr

import engine.architecture.scene.SceneContext
import engine.architecture.scene.entity.Entity
import engine.architecture.scene.node.Node
import engine.rendering.abstracted.renderers.Renderer3D
import engine.utils.libraryBindings.maths.joml.FrustumIntersection
import engine.utils.libraryBindings.maths.joml.Vector3f
import engine.utils.libraryBindings.maths.utils.Matrix4
import engine.utils.libraryBindings.opengl.shaders.*
import engine.utils.libraryBindings.opengl.textures.TextureObject
import engine.utils.libraryBindings.opengl.utils.GlUtils

internal class PBRRenderer : Renderer3D<PBRModel>() {

    private val shadersProgram: ShadersProgram<PBRModel>
    private val frustumIntersection: FrustumIntersection = FrustumIntersection()


    init {
        this.shadersProgram = ShadersProgram.create(VERT_FILE, FRAG_FILE)

        shadersProgram.addPerInstanceUniform(object : UniformBooleanProperty<PBRModel>("map_albedo") {
            override fun getUniformValue(state: RenderState<PBRModel>): Boolean {
                return state.instance.material.isAlbedoMapped
            }
        })
        shadersProgram.addPerInstanceUniform(object : UniformTextureProperty<PBRModel>("albedoMap", 0) {
            override fun getUniformValue(state: RenderState<PBRModel>): TextureObject {
                return state.instance.material.getAlbedoMap()
            }
        })
        shadersProgram.addPerInstanceUniform(object : UniformValueProperty<PBRModel>("albedoConst") {
            override fun getUniformValue(state: RenderState<PBRModel>): Vector3f {
                return state.instance.material.albedoConst
            }
        })


        shadersProgram.addPerInstanceUniform(object : UniformBooleanProperty<PBRModel>("map_normal") {
            override fun getUniformValue(state: RenderState<PBRModel>): Boolean {
                return state.instance.material.isNormalMapped
            }
        })
        shadersProgram.addPerInstanceUniform(object : UniformTextureProperty<PBRModel>("normalMap", 1) {
            override fun getUniformValue(state: RenderState<PBRModel>): TextureObject {
                return state.instance.material.getNormalMap()
            }
        })


        shadersProgram.addPerInstanceUniform(object : UniformBooleanProperty<PBRModel>("map_roughness") {
            override fun getUniformValue(state: RenderState<PBRModel>): Boolean {
                return state.instance.material.isRoughnessMapped
            }
        })
        shadersProgram.addPerInstanceUniform(object : UniformTextureProperty<PBRModel>("roughnessMap", 2) {
            override fun getUniformValue(state: RenderState<PBRModel>): TextureObject {
                return state.instance.material.getRoughnessMap()
            }
        })
        shadersProgram.addPerInstanceUniform(object : UniformFloatProperty<PBRModel>("roughnessConst") {
            override fun getUniformValue(state: RenderState<PBRModel>): Float {
                return state.instance.material.roughnessConst
            }
        })


        shadersProgram.addPerInstanceUniform(object : UniformBooleanProperty<PBRModel>("map_metal") {
            override fun getUniformValue(state: RenderState<PBRModel>): Boolean {
                return state.instance.material.isMetalMapped
            }
        })
        shadersProgram.addPerInstanceUniform(object : UniformTextureProperty<PBRModel>("metalMap", 3) {
            override fun getUniformValue(state: RenderState<PBRModel>): TextureObject {
                return state.instance.material.getMetalMap()
            }
        })
        shadersProgram.addPerInstanceUniform(object : UniformFloatProperty<PBRModel>("metalConst") {
            override fun getUniformValue(state: RenderState<PBRModel>): Float {
                return state.instance.material.metalConst
            }
        })




        shadersProgram.addPerRenderUniform(object : UniformValueProperty<PBRModel>("projectionMatrix") {
            override fun getUniformValue(state: RenderState<PBRModel>): UniformValue {
                return getContext().camera.projectionMatrix
            }
        })
        shadersProgram.addPerInstanceUniform(object : UniformValueProperty<PBRModel>("modelMatrix") {
            override fun getUniformValue(state: RenderState<PBRModel>): UniformValue {
                return state.instance.transform.transformationMatrix
            }
        })
        shadersProgram.addPerRenderUniform(object : UniformValueProperty<PBRModel>("viewMatrix") {
            override fun getUniformValue(state: RenderState<PBRModel>): UniformValue {
                return getContext().camera.viewMatrix
            }
        })
        shadersProgram.addPerInstanceUniform(object : UniformFloatProperty<PBRModel>("UVscale") {
            override fun getUniformValue(state: RenderState<PBRModel>): Float {
                return state.instance.uVscalar
            }
        })
    }

    override fun render(context: SceneContext) {
        if (renderList.size < 1) {
            return
        }
        shadersProgram.bind()

        GlUtils.disableBlending()

        val renderState = RenderState<PBRModel>(this, context.camera)
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
                    val instanceState = RenderState<PBRModel>(this, entity, context.camera, i)
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

        val renderState = RenderState<PBRModel>(this, context.camera)
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
                    val instanceState = RenderState<PBRModel>(this, entity, context.camera, i)
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

        private const val VERT_FILE = "res/shaders/pbr/pbr_vs.glsl"
        private const val FRAG_FILE = "res/shaders/pbr/pbr_fs.glsl"

        @JvmStatic
        val instance: PBRRenderer = PBRRenderer()

    }
}
