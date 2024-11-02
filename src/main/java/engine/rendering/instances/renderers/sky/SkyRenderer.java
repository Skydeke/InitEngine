package engine.rendering.instances.renderers.sky;

import engine.architecture.scene.SceneContext;
import engine.architecture.scene.light.LightManager;
import engine.architecture.scene.node.Node;
import engine.rendering.abstracted.renderers.Renderer3D;
import engine.utils.libraryBindings.opengl.shaders.RenderState;
import engine.utils.libraryBindings.opengl.shaders.ShadersProgram;
import engine.utils.libraryBindings.opengl.shaders.UniformFloatProperty;
import engine.utils.libraryBindings.opengl.shaders.UniformValue;
import engine.utils.libraryBindings.opengl.shaders.UniformValueProperty;
import lombok.Getter;
import lombok.var;

public class SkyRenderer extends Renderer3D<Sky> {

    private final ShadersProgram<Sky> shadersProgram;

    private static final String VERT_FILE = "res/shaders/sky/sky_vert.glsl";
    private static final String FRAG_FILE = "res/shaders/sky/sky_frag.glsl";

    @Getter
    public static final SkyRenderer instance;

    static {
        try {
            instance = new SkyRenderer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SkyRenderer() throws Exception {
        this.shadersProgram = ShadersProgram.create(VERT_FILE, FRAG_FILE);

        shadersProgram.addPerRenderUniform(new UniformValueProperty<Sky>("viewProjectionMatrix") {
            @Override
            public UniformValue getUniformValue(RenderState<Sky> state) {
                return getContext().getCamera().getProjectionViewMatrix();
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformValueProperty<Sky>("modelMatrix") {
            @Override
            public UniformValue getUniformValue(RenderState<Sky> state) {
                return state.getInstance().getTransform().getTransformationMatrix();
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformFloatProperty<Sky>("scale") {
            @Override
            public float getUniformValue(RenderState<Sky> state) {
                return state.getInstance().getTransform().getScale().y;
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformValueProperty<Sky>("sun_direction") {
            @Override
            public UniformValue getUniformValue(RenderState<Sky> state) {
                return LightManager.getSun().getTransform().getEulerAngles();
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformFloatProperty<Sky>("sun_intensity") {
            @Override
            public float getUniformValue(RenderState<Sky> state) {
                return LightManager.getSun().getIntensity();
            }
        });
    }

    @Override
    public void render(SceneContext context) {
        if (renderList.isEmpty()) return;

        shadersProgram.bind();

        RenderState<Sky> renderState = new RenderState<>(this, context.getCamera());
        shadersProgram.updatePerRenderUniforms(renderState);

        for (var model : renderList.keySet()) {
            for (int i = 0; i < model.getMeshes().length; i++) {
                model.bindAndConfigure(i);

                for (Sky entity : renderList.get(model)) {
                    RenderState<Sky> instanceState = new RenderState<>(this, entity, context.getCamera(), i);
                    shadersProgram.updatePerInstanceUniforms(instanceState);
                    model.render(instanceState, i);
                }
                model.unbind(i);
            }
        }

        shadersProgram.unbind();
    }

    @Override
    public void render(SceneContext context, Node.Condition condition) {
        if (renderList.isEmpty()) return;

        shadersProgram.bind();

        RenderState<Sky> renderState = new RenderState<>(this, context.getCamera());
        shadersProgram.updatePerRenderUniforms(renderState);

        for (var model : renderList.keySet()) {
            for (int i = 0; i < model.getMeshes().length; i++) {
                model.bindAndConfigure(i);

                for (Sky entity : renderList.get(model)) {
                    RenderState<Sky> instanceState = new RenderState<>(this, entity, context.getCamera(), i);
                    shadersProgram.updatePerInstanceUniforms(instanceState);
                    model.render(instanceState, i);
                }
                model.unbind(i);
            }
        }

        shadersProgram.unbind();
    }

    @Override
    public void cleanUp() {
        shadersProgram.delete();
    }
}
