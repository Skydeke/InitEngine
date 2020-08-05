package engine.architecture.models;

import engine.utils.libraryWrappers.opengl.objects.IVbo;

public interface ILod {

    IVbo current();

    boolean available();

}
