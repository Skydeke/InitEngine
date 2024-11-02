package engine.rendering;

import engine.architecture.models.Model;
import engine.rendering.abstracted.Processable;
import engine.utils.libraryBindings.opengl.textures.ITexture;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class RenderOutputData implements Processable {
    public ITexture colour;
    public ITexture normal;
    public ITexture depth;
    public ITexture position;

    @Override
    public void process() {
        // No implementation provided in the Kotlin code
    }

    @Override
    public Model getModel() {
        return null;
    }
}
