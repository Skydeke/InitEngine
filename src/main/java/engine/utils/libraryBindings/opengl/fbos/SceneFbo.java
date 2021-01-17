package engine.utils.libraryBindings.opengl.fbos;

import engine.architecture.system.AppContext;
import engine.architecture.system.Window;
import engine.utils.libraryBindings.opengl.constants.DataType;
import engine.utils.libraryBindings.opengl.constants.FormatType;
import engine.utils.libraryBindings.opengl.fbos.attachment.TextureAttachment;
import engine.utils.libraryBindings.opengl.textures.TextureConfigs;
import engine.utils.libraryBindings.opengl.textures.parameters.MagFilterParameter;
import engine.utils.libraryBindings.opengl.textures.parameters.MinFilterParameter;
import engine.utils.libraryBindings.opengl.utils.GlBuffer;
import org.lwjgl.opengl.GL30;

public class SceneFbo extends Fbo{

    private static SceneFbo instance = new SceneFbo(GL30.glGenFramebuffers(), AppContext.instance().getSceneContext().getResolution().x,
            AppContext.instance().getSceneContext().getResolution().y);

    public static SceneFbo getInstance() {
        return instance;
    }

    SceneFbo(int id, int width, int height) {
        super(id, width, height);
        TextureConfigs sceneConfigs = new TextureConfigs(FormatType.RGBA16F, FormatType.RGBA, DataType.FLOAT);
        sceneConfigs.magFilter = MagFilterParameter.LINEAR;
        sceneConfigs.minFilter = MinFilterParameter.LINEAR;
        addAttachment(TextureAttachment.ofColour(3, sceneConfigs));
        TextureConfigs dConfigs = new TextureConfigs(FormatType.DEPTH_COMPONENT24, FormatType.DEPTH_COMPONENT, DataType.FLOAT);
        dConfigs.magFilter = MagFilterParameter.LINEAR;
        dConfigs.minFilter = MinFilterParameter.LINEAR;
        addAttachment(TextureAttachment.ofDepth(dConfigs));
        unbind();
    }

    public void blitToScreen() {
        this.bind(FboTarget.READ_FRAMEBUFFER);
        GL30.glBindFramebuffer(FboTarget.DRAW_FRAMEBUFFER.get(), 0);
        blitFramebuffer(0, 0, AppContext.instance().getSceneContext().getResolution().x,
                AppContext.instance().getSceneContext().getResolution().y, 0, 0, Window.instance().getWidth(), Window.instance().getHeight(),
                MagFilterParameter.NEAREST, GlBuffer.COLOUR, GlBuffer.DEPTH, GlBuffer.STENCIL);
    }
}
