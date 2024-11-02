package engine.rendering.instances.renderers.shadow;

import engine.architecture.scene.SceneContext;
import engine.architecture.scene.entity.Entity;
import engine.architecture.scene.light.LightManager;
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

public class ShadowRenderer extends Renderer3D<Entity> {

    private final ShadersProgram<Entity> shadersProgram;
    private final FrustumIntersection frustumIntersection = new FrustumIntersection();

    private static final String VERT_FILE = "res/shaders/shadow/shadow_vs.glsl";
    private static final String FRAG_FILE = "res/shaders/shadow/shadow_fs.glsl";

    @Getter
    public static final ShadowRenderer instance;

    static {
        try {
            instance = new ShadowRenderer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ShadowRenderer() throws Exception {
        this.shadersProgram = ShadersProgram.create(VERT_FILE, FRAG_FILE);

        shadersProgram.addPerRenderUniform(new UniformValueProperty<Entity>("lightSpaceMatrix") {
            @Override
            public UniformValue getUniformValue(RenderState<Entity> state) {
                return LightManager.getSun().getLightSpaceMatrix();
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformValueProperty<Entity>("modelMatrix") {
            @Override
            public UniformValue getUniformValue(RenderState<Entity> state) {
                return state.getInstance().getTransform().getTransformationMatrix();
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
                frustumIntersection.testAab(
                        entity.getModel().getBounds().getMin().mul(entity.getTransform().getScale(), new Vector3f()),
                        entity.getModel().getBounds().getMax().mul(entity.getTransform().getScale(), new Vector3f()));
    }

    @Override
    public void cleanUp() {
        shadersProgram.delete();
    }
}