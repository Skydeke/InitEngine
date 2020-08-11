package engine.architecture.models;

import engine.utils.libraryBindings.opengl.objects.IVbo;

public interface ILod {

    IVbo current();

    boolean available();

}
