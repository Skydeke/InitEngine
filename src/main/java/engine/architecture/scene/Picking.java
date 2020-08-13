package engine.architecture.scene;

import engine.architecture.scene.node.Node;
import engine.architecture.system.Window;
import engine.rendering.instances.renderers.UUIDRenderer;
import engine.utils.libraryBindings.maths.joml.Vector3f;
import engine.utils.libraryBindings.opengl.constants.FormatType;
import engine.utils.libraryBindings.opengl.fbos.FrameBufferObject;
import engine.utils.libraryBindings.opengl.textures.TextureObject;
import engine.utils.libraryBindings.opengl.textures.TextureTarget;
import lombok.Getter;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Picking {

    private SceneContext context;
    @Getter
    private FrameBufferObject UUIDmap;

    public Picking(SceneContext context) {
        this.context = context;
        this.UUIDmap = new FrameBufferObject();
        UUIDmap.addAttatchments(new TextureObject(
                        TextureTarget.TEXTURE_2D, Window.instance().getWidth(),
                        Window.instance().getHeight())
                        .allocateImage2D(FormatType.RGBA16F, FormatType.RGBA)
                        .nofilter(),
                new TextureObject(
                        TextureTarget.TEXTURE_2D, Window.instance().getWidth(),
                        Window.instance().getHeight())
                        .allocateDepth()
                        .bilinearFilter());
    }

    public static Vector3f getUUIDColor(int UUID) {

        int r = (UUID & 0x000000FF) >> 0;
        int g = (UUID & 0x0000FF00) >> 8;
        int b = (UUID & 0x00FF0000) >> 16;

        return new Vector3f((float) r / 255f, (float) g / 255f, (float) b / 255f);
    }

    public Node pick(int x, int y) {

        UUIDmap.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        Window.instance().resizeViewport(context.getResolution());
        UUIDRenderer.getInstance().render(context, e -> !e.isSelected());
        Window.instance().resetViewport();
        ByteBuffer rgb = ByteBuffer.allocateDirect(4);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glReadPixels(x, y, 1, 1,
                GL_RGBA, GL_UNSIGNED_BYTE, rgb);
        UUIDmap.unbind();

        int r, g, b;
        r = rgb.get(0);
        g = (int) rgb.get(1) << 8;
        b = (int) rgb.get(2) << 16;

        int ID = r + b + g;

        for (Node node : context.getScene().collect())
            if (node.UUID == ID) {
//                System.out.println("Identified picked Node!");
                return node;
            }

        return null;
    }
}
