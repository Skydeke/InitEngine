package engine.utils.libraryBindings.opengl.fbos.attachment;


import engine.utils.libraryBindings.opengl.fbos.Fbo;
import engine.utils.libraryBindings.opengl.textures.Texture;

public interface Attachment {

    int getAttachmentPoint();

    AttachmentType getAttachmentType();

    Texture getTexture();

    void init(Fbo fbo);

    void resize(int w, int h);

    void delete();

}
