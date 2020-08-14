package engine.utils.libraryBindings.opengl.fbos;


import engine.utils.libraryBindings.opengl.fbos.attachment.Attachment;
import engine.utils.libraryBindings.opengl.fbos.attachment.MultisampledAttachment;
import engine.utils.libraryBindings.opengl.textures.parameters.MagFilterParameter;
import engine.utils.libraryBindings.opengl.utils.GlBuffer;

import java.util.List;

public class MultisampledFbo implements IFbo {

    private final Fbo fbo;

    public MultisampledFbo(int width, int height) {
        this.fbo = Fbo.create(width, height);
    }

    public void setReadAttachment(int colourAttachment) {
        getFbo().setReadAttachment(colourAttachment);
    }

    public void setDrawAttachments(int... attachments) {
        getFbo().setDrawAttachments(attachments);
    }

    public void addAttachment(MultisampledAttachment attachment) {
        getFbo().addAttachment(attachment);
    }

    public List<Attachment> getAttachments() {
        return getFbo().getAttachments();
    }

    public Attachment getDepthAttachment() {
        return getFbo().getDepthAttachment();
    }

    private Fbo getFbo() {
        return fbo;
    }

    public void blitToScreen() {
        getFbo().blitToScene();
    }

    @Override
    public void blitFbo(IFbo fbo, MagFilterParameter filter, GlBuffer... buffers) {
        getFbo().blitFbo(fbo, filter, buffers);
    }

    @Override
    public int getWidth() {
        return getFbo().getWidth();
    }

    @Override
    public int getHeight() {
        return getFbo().getHeight();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void bind(FboTarget target) {
        getFbo().bind(target);
    }

    @Override
    public void unbind(FboTarget target) {
        getFbo().unbind(target);
    }

    @Override
    public void delete() {
        getFbo().delete();
    }
}
