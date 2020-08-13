package engine.architecture.scene;

import engine.architecture.system.Config;
import engine.architecture.system.Window;
import engine.utils.libraryBindings.opengl.fbos.FrameBufferObject;
import engine.utils.libraryBindings.opengl.textures.TextureObject;
import engine.utils.libraryBindings.opengl.textures.TextureTarget;

import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;

public class SceneFbo extends FrameBufferObject {

    private static SceneFbo instance = new SceneFbo();

    private SceneFbo() {
        TextureTarget target;
        if (Config.instance().getMultisamples() > 0)
            target = TextureTarget.TEXTURE_2D_MULTISAMPLE;
        else
            target = TextureTarget.TEXTURE_2D;
        addAttatchments(new TextureObject(target, Window.instance().getResolution())
                        .allocateImage2D(GL_RGBA16F, GL_RGBA)
                        .bilinearFilter(),
                new TextureObject(target, Window.instance().getResolution())
                        .allocateDepth()
                        .bilinearFilter());
    }

    public static SceneFbo getInstance() {
        if (instance != null)
            return instance;
        else
            return instance = new SceneFbo();
    }
}
