package engine.rendering.abstracted;

import engine.architecture.models.ILod;
import engine.utils.libraryWrappers.opengl.constants.RenderMode;

public interface Renderable {

    void bind(ILod lod);

    void render(RenderMode renderMode);
}
