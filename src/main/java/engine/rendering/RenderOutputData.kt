package engine.rendering

import engine.utils.libraryWrappers.opengl.textures.TextureObject

data class RenderOutputData(
    val colour: TextureObject,
    val normal: TextureObject,
    val depth: TextureObject
)

