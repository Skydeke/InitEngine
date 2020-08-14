package engine.utils.libraryBindings.opengl.fbos;

import engine.architecture.system.AppContext;
import engine.utils.libraryBindings.opengl.constants.DataType;
import engine.utils.libraryBindings.opengl.constants.FormatType;
import engine.utils.libraryBindings.opengl.fbos.attachment.TextureAttachment;
import engine.utils.libraryBindings.opengl.textures.TextureConfigs;
import engine.utils.libraryBindings.opengl.textures.parameters.MagFilterParameter;
import engine.utils.libraryBindings.opengl.textures.parameters.MinFilterParameter;
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
}
