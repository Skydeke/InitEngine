package engine.rendering

import engine.architecture.models.Model
import engine.rendering.abstracted.Renderable
import engine.utils.libraryWrappers.opengl.constants.RenderMode
import engine.utils.libraryWrappers.opengl.textures.TextureObject

data class RenderOutputData(
    val colour: TextureObject,
    val normal: TextureObject,
    val depth: TextureObject
) : Renderable {
    override fun process() {
    }

    override fun render(renderMode: RenderMode?) {
    }

    override fun getModel(): Model? {
        return null;
    }
}
