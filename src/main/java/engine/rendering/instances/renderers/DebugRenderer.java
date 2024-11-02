package engine.rendering.instances.renderers;

import engine.architecture.scene.SceneContext;
import engine.architecture.scene.entity.Entity;
import engine.architecture.scene.node.Node;
import engine.rendering.abstracted.renderers.Renderer3D;
import engine.utils.libraryBindings.maths.joml.FrustumIntersection;
import engine.utils.libraryBindings.maths.joml.Vector3f;
import engine.utils.libraryBindings.maths.utils.Matrix4;
import engine.utils.libraryBindings.opengl.shaders.RenderState;
import engine.utils.libraryBindings.opengl.shaders.ShadersProgram;
import engine.utils.libraryBindings.opengl.shaders.UniformValue;
import engine.utils.libraryBindings.opengl.shaders.UniformValueProperty;
import lombok.Getter;
import lombok.var;

public class DebugRenderer extends Renderer3D<Entity> {

    private final ShadersProgram<Entity> shadersProgram;
    private final FrustumIntersection frustumIntersection = new FrustumIntersection();

    private static final String VERT_FILE = "res/shaders/overlay/overlay_vs.glsl";
    private static final String FRAG_FILE = "res/shaders/overlay/overlay_fs.glsl";

    @Getter
    public static final DebugRenderer instance;

    static {
        try {
            instance = new DebugRenderer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private DebugRenderer() throws Exception {
        this.shadersProgram = ShadersProgram.create(VERT_FILE, FRAG_FILE);

        shadersProgram.addPerInstanceUniform(new UniformValueProperty<Entity>("color") {
            @Override
            public UniformValue getUniformValue(RenderState<Entity> state) {
                return new Vector3f(1f, 0f, 0f);
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformValueProperty<Entity>("projectionMatrix") {
            @Override
            public UniformValue getUniformValue(RenderState<Entity> state) {
                return getContext().getCamera().getProjectionMatrix();
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformValueProperty<Entity>("modelMatrix") {
            @Override
            public UniformValue getUniformValue(RenderState<Entity> state) {
                return state.getInstance().getTransform().getTransformationMatrix();
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformValueProperty<Entity>("viewMatrix") {
            @Override
            public UniformValue getUniformValue(RenderState<Entity> state) {
                return getContext().getCamera().getViewMatrix();
            }
        });
    }

    @Override
    public void render(SceneContext context) {
        if (renderList.isEmpty()) return;

        shadersProgram.bind();

        RenderState<Entity> renderState = new RenderState<>(this, context.getCamera());
        shadersProgram.updatePerRenderUniforms(renderState);

        for (var model : renderList.keySet()) {
            for (int i = 0; i < model.getMeshes().length; i++) {
                model.bindAndConfigure(i);

                for (Entity entity : renderList.get(model)) {
                    frustumIntersection.set(context.getCamera().getProjectionViewMatrix().mul(
                            entity.getTransform().getTransformationMatrix(), Matrix4.pool.poolAndGive()));

                    if (!checkRenderPass(entity)) continue;

                    RenderState<Entity> instanceState = new RenderState<>(this, entity, context.getCamera(), i);
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

        RenderState<Entity> renderState = new RenderState<>(this, context.getCamera());
        shadersProgram.updatePerRenderUniforms(renderState);

        for (var model : renderList.keySet()) {
            for (int i = 0; i < model.getMeshes().length; i++) {
                model.bindAndConfigure(i);

                for (Entity entity : renderList.get(model)) {
                    frustumIntersection.set(context.getCamera().getProjectionViewMatrix().mul(
                            entity.getTransform().getTransformationMatrix(), Matrix4.pool.poolAndGive()));

                    if (!checkRenderPass(entity)) continue;

                    RenderState<Entity> instanceState = new RenderState<>(this, entity, context.getCamera(), i);
                    shadersProgram.updatePerInstanceUniforms(instanceState);
                    model.render(instanceState, i);
                }
                model.unbind(i);
            }
        }

        shadersProgram.unbind();
    }

    private boolean checkRenderPass(Entity entity) {
        return !checkClippingCulling(entity.getTransform().getPosition()) &&
                frustumIntersection.testAab(entity.getModel().getBounds().getMin(),
                        entity.getModel().getBounds().getMax());
    }

    @Override
    public void cleanUp() {
        shadersProgram.delete();
    }
}