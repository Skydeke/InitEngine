package engine.ui.element.panel;

import engine.architecture.scene.SceneContext;
import engine.architecture.scene.SceneFbo;
import engine.architecture.system.AppContext;
import engine.architecture.system.Window;
import engine.ui.element.ElementManager;
import engine.ui.event.KeyboardEvent;
import engine.ui.event.ResizeEvent;
import engine.ui.layout.Box;
import engine.utils.Color;
import engine.utils.libraryWrappers.maths.joml.Vector2i;
import engine.utils.libraryWrappers.opengl.utils.GlUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class ScenePanel extends Panel {

    private SceneContext context;


    public ScenePanel(SceneContext _context) {

        super();
        this.context = _context;
        context.setParent(this);

        setDrawBorder(false);
        setBorderColor(new Color(0x202020));
        setBorderSize(2);

        setImageBuffer(SceneFbo.getInstance().getAttachment(0));
        isBuffer = true;
//        try {
//            setImageBuffer(Texture2D.of("images/icon.png"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        setImageBuffer(context.getPipeline().getPbrFBO().getAttachment(2));
//        setImageBuffer(context.getPicking().getUUIDmap().getAttachment(0));

        setImage(true);

        onEvent(e -> {

            if (e instanceof ResizeEvent) {
                Vector2i resolution = getAbsoluteBox().resolution();
                resolution.x -= 2 * getBorderSize();
                resolution.y -= 2 * getBorderSize();
                context.setResolution(resolution);
                e.consume();
            } else if (e instanceof KeyboardEvent) {
                context.getCamera().handle(e);

                if (((KeyboardEvent) e).getKey() == GLFW_KEY_F5 &&
                        ((KeyboardEvent) e).getAction() == KeyboardEvent.KEY_PRESSED) {
                    GlUtils.drawPolygonLine();
                }
                if (((KeyboardEvent) e).getKey() == GLFW_KEY_F6 &&
                        ((KeyboardEvent) e).getAction() == KeyboardEvent.KEY_PRESSED) {
                    GlUtils.drawPolygonFill();
                }
                /** F11: TOGGLE FULLSCREEN  **/
                if (((KeyboardEvent) e).getKey() == GLFW_KEY_F11 &&
                        ((KeyboardEvent) e).getAction() == KeyboardEvent.KEY_PRESSED) {

                    screenShot();
                    // set fullscreen
                    if (!isAttached()) {
                        setAttached(true);
                        AppContext.instance().setRenderElement(this);
                        ElementManager.instance().setFocused(this.getViewport());
                        if (setAbsoluteBox(new Box(0, 0, 1, 1))) {
                            setDrawBorder(false);
                            context.setResolution(Window.instance().getResolution());
                            layoutChildren();
                        }
                    }

                    // unset fullscreen
                    else {
                        setDrawBorder(true);
                        AppContext.instance().resetRenderElement();
                        ElementManager.instance().resetFocused();
                        setAttached(false);
                        layoutChildren();
                    }
                    e.consume();
                }

            }

            // pass to scene context if not consumed
            if (!e.isConsumed())
                context.handle(e);
        });

    }

    private void screenShot() {
        int WIDTH = SceneFbo.getInstance().getAttachment(0).getWidth();
        int HEIGHT = SceneFbo.getInstance().getAttachment(0).getHeight();
        //Creating an rbg array of total pixels
        int[] pixels = new int[WIDTH * HEIGHT];
        int bindex;
        // allocate space for RBG pixels
        ByteBuffer fb = ByteBuffer.allocateDirect(WIDTH * HEIGHT * 3);

        // grab a copy of the current frame contents as RGB
        GL11.glReadPixels(0, 0, WIDTH, HEIGHT, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, fb);

        BufferedImage imageIn = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        // convert RGB data in ByteBuffer to integer array
        for (int i = 0; i < pixels.length; i++) {
            bindex = i * 3;
            pixels[i] =
                    ((fb.get(bindex) << 16)) +
                            ((fb.get(bindex + 1) << 8)) +
                            ((fb.get(bindex + 2) << 0));
        }
        //Allocate colored pixel to buffered Image
        imageIn.setRGB(0, 0, WIDTH, HEIGHT, pixels, 0, WIDTH);

        //Creating the transformation direction (horizontal)
        AffineTransform at = AffineTransform.getScaleInstance(1, 1);
        at.translate(0, imageIn.getHeight(null));

        //Applying transformation
        AffineTransformOp opRotated = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage imageOut = opRotated.filter(imageIn, null);

        try {//Try to create image, else show exception.

            ImageIO.write(imageOut, "png", new FileOutputStream("test.png"));
        } catch (Exception e) {
            System.out.println("ScreenShot() exception: " + e);
        }
    }
}
