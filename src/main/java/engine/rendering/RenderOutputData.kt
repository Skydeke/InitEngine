package engine.rendering

import engine.architecture.models.Model
import engine.rendering.abstracted.Processable
import engine.utils.libraryBindings.opengl.textures.ITexture

data class RenderOutputData(
    val colour: ITexture,
    val normal: ITexture,
    val depth: ITexture
) : Processable {
    override fun process() {
    }

    override fun getModel(): Model? {
        return null
    }
}
