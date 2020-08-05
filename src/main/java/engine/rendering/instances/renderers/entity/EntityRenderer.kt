package engine.rendering.instances.renderers.entity

import engine.architecture.scene.SceneContext
import engine.architecture.scene.entity.Entity
import engine.architecture.scene.node.Node
import engine.rendering.abstracted.renderers.Renderer3D
import engine.utils.libraryWrappers.maths.joml.Vector3f
import engine.utils.libraryWrappers.opengl.shaders.*
import engine.utils.libraryWrappers.opengl.textures.TextureObject
import engine.utils.libraryWrappers.opengl.utils.GlUtils

internal class EntityRenderer : Renderer3D<Entity>() {

    private val shadersProgram: ShadersProgram<Entity>

    init {
        this.shadersProgram = ShadersProgram.create(VERT_FILE, FRAG_FILE)

        shadersProgram.addPerMeshUniform(object : UniformValueProperty<Entity>("albedoConst") {
            override fun getUniformValue(state: RenderState<Entity>): Vector3f {
                return Vector3f(
                    state.toRenderMesh.material.diffuseColor.x,
                    state.toRenderMesh.material.diffuseColor.y,
                    state.toRenderMesh.material.diffuseColor.z
                )
            }
        })
        shadersProgram.addPerMeshUniform(object : UniformTextureProperty<Entity>("albedoTex",  0) {
            override fun getUniformValue(state: RenderState<Entity>): TextureObject {
                return state.toRenderMesh.material.getDiffuseTexture();
            }
        })
        shadersProgram.addPerMeshUniform(object : UniformBooleanProperty<Entity>("isAlbedoMapped") {
            override fun getUniformValue(state: RenderState<Entity>): Boolean {
                return state.toRenderMesh.material.hasTexture();
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

        GlUtils.enableCulling()
        GlUtils.enableDepthTest()
        GlUtils.enableDepthMasking()
        GlUtils.enableAlphaBlending()

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

        private const val VERT_FILE = "res/shaders/entity/entity_vs.glsl"
        private const val FRAG_FILE = "res/shaders/entity/entity_fs.glsl"

        @JvmStatic
        val instance: EntityRenderer = EntityRenderer()

    }
}
