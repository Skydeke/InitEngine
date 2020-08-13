package engine.architecture.ui.element.panel;

import engine.architecture.models.Model;
import engine.architecture.models.ModelGenerator;
import engine.architecture.system.AppContext;
import engine.architecture.system.Window;
import engine.architecture.ui.element.UIElement;
import engine.architecture.ui.element.layout.Box;
import engine.rendering.abstracted.Processable;
import engine.utils.Color;
import engine.utils.libraryBindings.maths.joml.Vector4i;
import engine.utils.libraryBindings.opengl.shaders.*;
import engine.utils.libraryBindings.opengl.textures.TextureObject;
import engine.utils.libraryBindings.opengl.utils.GlUtils;
import lombok.Getter;
import lombok.Setter;

public class Panel extends UIElement implements Processable {

    private final static String VERT_FILE = "res/shaders/gui/panel_vs.glsl";
    private final static String FRAG_FILE = "res/shaders/gui/panel_fs.glsl";
    private static ShadersProgram<Panel> shadersProgram;
    private Model model = ModelGenerator.generateSquare();

    @Getter
    private boolean isBuffer = false;
    // defines color of panel in UI
    @Setter
    @Getter
    private Color color;
    @Getter
    private TextureObject imageBuffer;
    @Setter
    @Getter
    private boolean isImage = false;
    @Getter
    @Setter
    private boolean scissor = true;
    // defines corner rounding for each corner in pixels
    // x: top-left, y: top-right, z: bottom-left, w: bottom-right
    @Getter
    private Vector4i rounding;

    private Panel(Color color, Vector4i rounding) {
        super();
        relativeBox = new Box(0, 0, 1, 1);
        absoluteBox = new Box(0, 0, 1, 1);
        this.color = color;
        this.rounding = rounding;

        try {
            if (shadersProgram == null) {
                Panel.shadersProgram = ShadersProgram.create(VERT_FILE, FRAG_FILE);
                setupUniforms();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Panel() {
        this(new Color(0x3f3f3f), new Vector4i(0, 0, 0, 0));
    }

    @Override
    public void render() {
        boolean isWrongFillMode = false;
        if (GlUtils.isPolygonLines()) {
            GlUtils.drawPolygonFill();
            isWrongFillMode = true;
        }
        shadersProgram.bind();
        RenderState<Panel> instanceState = new RenderState<Panel>(null, this, AppContext.instance().getSceneContext().getCamera(), 0);
        getModel().bindAndConfigure(0);
        shadersProgram.updatePerInstanceUniforms(instanceState);
        if (isScissor()) {
            Window.instance().setScissor(getAbsoluteBox());
            getModel().render(instanceState, 0);
            super.render();
            Window.instance().disableScissor();
        } else {
            getModel().render(instanceState, 0);
            super.render();
        }
        getModel().unbind(0);
        shadersProgram.unbind();

        if (isWrongFillMode) {
            GlUtils.drawPolygonLine();
        }
    }

    @Override
    public void cleanup() {
        shadersProgram.delete();
        super.cleanup();
    }

    private void setupUniforms() {
        shadersProgram.addPerInstanceUniform(new UniformFloatProperty<>("box.x") {
            @Override
            public float getUniformValue(RenderState<Panel> state) {
                return state.getInstance().getAbsoluteBox().getX();
            }
        });
        shadersProgram.addPerInstanceUniform(new UniformFloatProperty<>("box.y") {
            @Override
            public float getUniformValue(RenderState<Panel> state) {
                return state.getInstance().getAbsoluteBox().getY();
            }
        });
        shadersProgram.addPerInstanceUniform(new UniformFloatProperty<>("box.width") {
            @Override
            public float getUniformValue(RenderState<Panel> state) {
                return state.getInstance().getAbsoluteBox().getWidth();
            }
        });
        shadersProgram.addPerInstanceUniform(new UniformFloatProperty<>("box.height") {
            @Override
            public float getUniformValue(RenderState<Panel> state) {
                return state.getInstance().getAbsoluteBox().getHeight();
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformValueProperty<>("resolution") {
            @Override
            public UniformValue getUniformValue(RenderState<Panel> state) {
                return state.getInstance().getAbsoluteBox().resolution();
            }
        });
        shadersProgram.addPerInstanceUniform(new UniformValueProperty<>("rounding") {
            @Override
            public UniformValue getUniformValue(RenderState<Panel> state) {
                return state.getInstance().getRounding();
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformTextureProperty<>("texture", 0) {
            @Override
            public TextureObject getUniformValue(RenderState<Panel> state) {
                return state.getInstance().getImageBuffer();
            }
        });
        shadersProgram.addPerInstanceUniform(new UniformBooleanProperty<>("isTexture") {
            @Override
            public boolean getUniformValue(RenderState<Panel> state) {
                return state.getInstance().getImageBuffer() != null;
            }
        });
        shadersProgram.addPerInstanceUniform(new UniformBooleanProperty<>("isDepth") {
            @Override
            public boolean getUniformValue(RenderState<Panel> state) {
                return state.getInstance().getImageBuffer() != null && state.getInstance().getImageBuffer().isDepth();
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformValueProperty<>("color") {
            @Override
            public UniformValue getUniformValue(RenderState<Panel> state) {
                return state.getInstance().getColor().getColor();
            }
        });
        shadersProgram.addPerInstanceUniform(new UniformBooleanProperty<>("isBuffer") {
            @Override
            public boolean getUniformValue(RenderState<Panel> state) {
                return state.getInstance().isBuffer;
            }
        });
    }


    public void setImageBuffer(TextureObject texture, boolean isFBOFlipped){
        imageBuffer = texture;
        isImage = true;
        isBuffer = isFBOFlipped;
    }

    public void setRounding(Vector4i rounding) {
        setRounding(rounding.x, rounding.y, rounding.z, rounding.w);
    }

    public void setRounding(int rounding) {
        this.rounding.x = rounding;
        this.rounding.y = rounding;
        this.rounding.z = rounding;
        this.rounding.w = rounding;
    }

    public void setRounding(int r1, int r2, int r3, int r4) {
        this.rounding.x = r1;
        this.rounding.y = r2;
        this.rounding.z = r3;
        this.rounding.w = r4;
    }

    @Override
    public void process() {

    }

    @Override
    public Model getModel() {
        return model;
    }
}
