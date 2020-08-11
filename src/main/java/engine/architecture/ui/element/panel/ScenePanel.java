package engine.architecture.ui.element.panel;

import engine.architecture.scene.SceneContext;
import engine.architecture.scene.SceneFbo;
import engine.architecture.system.AppContext;
import engine.architecture.ui.constraints.CenterConstraint;
import engine.architecture.ui.constraints.PercentageConstraint;
import engine.architecture.ui.constraints.UIConstraints;
import engine.architecture.ui.element.ElementManager;
import engine.architecture.ui.element.layout.LayoutType;
import engine.architecture.ui.event.InputManager;
import engine.architecture.ui.event.KeyboardEvent;
import engine.architecture.ui.event.ResizeEvent;
import engine.utils.libraryBindings.maths.joml.Vector2i;
import engine.utils.libraryBindings.opengl.utils.GlUtils;
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
    private boolean isFullDisplay = false;

    public ScenePanel(SceneContext _context) {

        super();
        this.context = _context;
        context.setParent(this);

        setImageBuffer(SceneFbo.getInstance().getAttachment(0), true);
//        try {
//            setImageBuffer(Texture2D.of("images/icon.png"), false);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        setImageBuffer(context.getPipeline().getPbrFBO().getAttachment(2), true);
//        setImageBuffer(context.getPicking().getUUIDmap().getAttachment(0), true);

        setImage(true);

        onEvent(e -> {

            if (e instanceof ResizeEvent) {
                Vector2i resolution = getAbsoluteBox().resolution();
                context.setResolution(resolution);
                e.consume();
            } else if (e instanceof KeyboardEvent) {
                context.getCamera().handle(e);

                if (InputManager.instance().isKeyPressed(GLFW_KEY_F5)) {
                    GlUtils.drawPolygonLine();
                }
                if (InputManager.instance().isKeyPressed(GLFW_KEY_F6)) {
                    GlUtils.drawPolygonFill();
                }
                /* F11: TOGGLE FULLSCREEN  **/
                if (InputManager.instance().isKeyPressed(GLFW_KEY_F11)) {
                    if (isFullDisplay) {
                        // unset fullscreen
                        setAlignType(LayoutType.RELATIVE_TO_PARENT);
//                        setConstraints(null);//Deactivate Constraints cause the ViewportLayout cant handle them.
                        ElementManager.instance().resetFocused();
                        AppContext.instance().resetRenderElement();
                        getParent().recalculateAbsolutePositions();
                        context.setResolution(getAbsoluteBox().resolution());
                        isFullDisplay = false;
                        e.consume();
                    } else {
                        // set fullscreen
                        ElementManager.instance().setFocused(this);
                        AppContext.instance().setRenderElement(this);
                        setAlignType(LayoutType.ABSOLUTE);
                        setConstraints(new UIConstraints()
                                .x(new CenterConstraint())
                                .y(new CenterConstraint())
                                .w(new PercentageConstraint(1f))
                                .h(new PercentageConstraint(1f)));
                        context.setResolution(getAbsoluteBox().resolution());
                        getParent().recalculateAbsolutePositions();
                        isFullDisplay = true;
                        e.consume();
                    }
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
