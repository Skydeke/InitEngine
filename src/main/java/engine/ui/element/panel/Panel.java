package engine.ui.element.panel;

import engine.architecture.models.Model;
import engine.architecture.models.ModelGenerator;
import engine.architecture.system.AppContext;
import engine.architecture.system.Config;
import engine.architecture.system.Window;
import engine.rendering.abstracted.Renderable;
import engine.ui.element.UIElement;
import engine.ui.layout.Box;
import engine.utils.Color;
import engine.utils.libraryWrappers.maths.joml.Vector4i;
import engine.utils.libraryWrappers.opengl.constants.RenderMode;
import engine.utils.libraryWrappers.opengl.shaders.*;
import engine.utils.libraryWrappers.opengl.textures.TextureObject;
import engine.utils.libraryWrappers.opengl.utils.GlUtils;
import lombok.Getter;
import lombok.Setter;

public class Panel extends UIElement implements Renderable {

    private final static String VERT_FILE = "res/shaders/gui/panel_vs.glsl";
    private final static String FRAG_FILE = "res/shaders/gui/panel_fs.glsl";
    private static ShadersProgram<Panel> shadersProgram;
    private Model model = ModelGenerator.generateSquare();

    @Getter
    protected boolean isBuffer = false;
    // defines color of panel in UI
    @Setter
    @Getter
    private Color color;
    @Setter
    @Getter
    private TextureObject imageBuffer;
    @Setter
    @Getter
    private boolean isImage = false;
    @Setter
    private boolean drawBorder;
    @Setter
    private Color borderColor;
    @Getter
    @Setter
    private int borderSize;
    @Getter
    @Setter
    private boolean scissor = true;
    // defines corner rounding for each corner in pixels
    // x: top-left, y: top-right, z: bottom-left, w: bottom-right
    @Setter
    @Getter
    private Vector4i rounding;

    private Panel(Color color, Vector4i rounding) {
        super();
        relativeBox = new Box(0, 0, 1, 1);
        absoluteBox = new Box(0, 0, 1, 1);
        this.color = color;
        this.rounding = rounding;

        drawBorder = false;
        borderColor = new Color(0x202020);
        borderSize = 0;

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
        shadersProgram.addPerInstanceUniform(new UniformFloatProperty<Panel>("box.x") {
            @Override
            public float getUniformValue(RenderState<Panel> state) {
                return state.getInstance().getAbsoluteBox().getX();
            }
        });
        shadersProgram.addPerInstanceUniform(new UniformFloatProperty<Panel>("box.y") {
            @Override
            public float getUniformValue(RenderState<Panel> state) {
                return state.getInstance().getAbsoluteBox().getY();
            }
        });
        shadersProgram.addPerInstanceUniform(new UniformFloatProperty<Panel>("box.width") {
            @Override
            public float getUniformValue(RenderState<Panel> state) {
                return state.getInstance().getAbsoluteBox().getWidth();
            }
        });
        shadersProgram.addPerInstanceUniform(new UniformFloatProperty<Panel>("box.height") {
            @Override
            public float getUniformValue(RenderState<Panel> state) {
                return state.getInstance().getAbsoluteBox().getHeight();
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformValueProperty<Panel>("resolution") {
            @Override
            public UniformValue getUniformValue(RenderState<Panel> state) {
                return state.getInstance().getAbsoluteBox().resolution();
            }
        });
        shadersProgram.addPerInstanceUniform(new UniformValueProperty<Panel>("rounding") {
            @Override
            public UniformValue getUniformValue(RenderState<Panel> state) {
                return state.getInstance().getRounding();
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformTextureProperty<Panel>("texture", 0) {
            @Override
            public TextureObject getUniformValue(RenderState<Panel> state) {
                return state.getInstance().getImageBuffer();
            }
        });
        shadersProgram.addPerInstanceUniform(new UniformBooleanProperty<Panel>("isTexture") {
            @Override
            public boolean getUniformValue(RenderState<Panel> state) {
                return state.getInstance().getImageBuffer() != null;
            }
        });
        shadersProgram.addPerInstanceUniform(new UniformBooleanProperty<Panel>("isDepth") {
            @Override
            public boolean getUniformValue(RenderState<Panel> state) {
                return state.getInstance().getImageBuffer() != null && state.getInstance().getImageBuffer().isDepth();
            }
        });
        shadersProgram.addPerInstanceUniform(new UniformFloatProperty<Panel>("multisamples") {
            @Override
            public float getUniformValue(RenderState<Panel> state) {
                return state.getInstance().getImageBuffer() != null && state.getInstance().getImageBuffer().isMultisample() ? Config.instance().getMultisamples() : 0;
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformValueProperty<Panel>("color") {
            @Override
            public UniformValue getUniformValue(RenderState<Panel> state) {
                return state.getInstance().getColor().getColor();
            }
        });
        shadersProgram.addPerInstanceUniform(new UniformBooleanProperty<Panel>("isBuffer") {
            @Override
            public boolean getUniformValue(RenderState<Panel> state) {
                return state.getInstance().isBuffer;
            }
        });


        shadersProgram.addPerInstanceUniform(new UniformBooleanProperty<Panel>("border") {
            @Override
            public boolean getUniformValue(RenderState<Panel> state) {
                return state.getInstance().drawBorder;
            }
        });
        shadersProgram.addPerInstanceUniform(new UniformIntProperty<Panel>("borderSize") {
            @Override
            public int getUniformValue(RenderState<Panel> state) {
                return state.getInstance().borderSize;
            }
        });
        shadersProgram.addPerInstanceUniform(new UniformValueProperty<Panel>("borderColor") {
            @Override
            public UniformValue getUniformValue(RenderState<Panel> state) {
                return state.getInstance().borderColor.getColor();
            }
        });
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
    public void render(RenderMode renderMode) {

    }

    @Override
    public Model getModel() {
        return model;
    }
}
