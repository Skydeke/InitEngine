package engine.architecture.models.generators;

import engine.architecture.models.Model;
import engine.utils.libraryBindings.opengl.objects.IVbo;
import engine.utils.libraryBindings.opengl.objects.Vao;

public interface IModelGenerator {

    Vao createVao();

    IVbo createDataVbo();

    IVbo createIndexVbo();

    Model generateModel();

    default IVbo createIndexVbo(int lod) {
        throw new UnsupportedOperationException("Level of detail is not supported for " + getClass().getSimpleName());
    }

}
