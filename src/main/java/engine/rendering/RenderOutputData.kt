package engine.rendering

import engine.architecture.models.Model
import engine.rendering.abstracted.Processable
import engine.utils.libraryWrappers.opengl.textures.TextureObject

data class RenderOutputData(
    val colour: TextureObject,
    val normal: TextureObject,
    val depth: TextureObject
) : Processable {
    override fun process() {
    }

    override fun getModel(): Model? {
        return null
    }
}
