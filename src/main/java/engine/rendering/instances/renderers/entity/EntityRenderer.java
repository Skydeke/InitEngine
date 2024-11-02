package engine.rendering.instances.renderers.entity;

import engine.architecture.scene.SceneContext;
import engine.architecture.scene.entity.Entity;
import engine.architecture.scene.node.Node;
import engine.rendering.abstracted.renderers.Renderer3D;
import engine.utils.libraryBindings.maths.joml.FrustumIntersection;
import engine.utils.libraryBindings.maths.joml.Vector3f;
import engine.utils.libraryBindings.maths.utils.Matrix4;
import engine.utils.libraryBindings.opengl.shaders.*;
import engine.utils.libraryBindings.opengl.textures.ITexture;
import lombok.Getter;
import lombok.var;

@Getter
public class EntityRenderer extends Renderer3D<Entity> {

    private final ShadersProgram<Entity> shadersProgram;
    private final FrustumIntersection frustumIntersection = new FrustumIntersection();

    private static final String VERT_FILE = "res/shaders/entity/entity_vs.glsl";
    private static final String FRAG_FILE = "res/shaders/entity/entity_fs.glsl";

    @Getter
    public static final EntityRenderer instance;

    static {
        try {
            instance = new EntityRenderer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private EntityRenderer() throws Exception {
        this.shadersProgram = ShadersProgram.create(VERT_FILE, FRAG_FILE);

        shadersProgram.addPerInstanceUniform(new UniformValueProperty<Entity>("albedoConst") {
            @Override
            public Vector3f getUniformValue(RenderState<Entity> state) {
                return new Vector3f(
                        state.getMesh().getMaterial().getDiffuseColor().x,
                        state.getMesh().getMaterial().getDiffuseColor().y,
                        state.getMesh().getMaterial().getDiffuseColor().z
                );
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformTextureProperty<Entity>("albedoTex", 0) {
            @Override
            public ITexture getUniformValue(RenderState<Entity> state) {
                return state.getMesh().getMaterial().getDiffuseTexture();
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformBooleanProperty<Entity>("isAlbedoMapped") {
            @Override
            public boolean getUniformValue(RenderState<Entity> state) {
                return state.getMesh().getMaterial().hasTexture();
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformTextureProperty<Entity>("normalMap", 1) {
            @Override
            public ITexture getUniformValue(RenderState<Entity> state) {
                return state.getMesh().getMaterial().getNormalTexture();
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformBooleanProperty<Entity>("map_normal") {
            @Override
            public boolean getUniformValue(RenderState<Entity> state) {
                return state.getMesh().getMaterial().isUseNormalTex();
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
        if (renderList.isEmpty()) {
            return;
        }
        shadersProgram.bind();

        RenderState<Entity> renderState = new RenderState<>(this, context.getCamera());
        shadersProgram.updatePerRenderUniforms(renderState);

        for (var model : renderList.keySet()) {
            for (int i = 0; i < model.getMeshes().length; i++) {
                model.bindAndConfigure(i);
                for (var entity : renderList.get(model)) {
                    frustumIntersection.set(
                            context.getCamera().getProjectionViewMatrix().mul(
                                    entity.getTransform().getTransformationMatrix(), Matrix4.pool.poolAndGive()
                            )
                    );
                    if (!checkRenderPass(entity)) {
                        continue;
                    }
                    RenderState<Entity> instanceState = new RenderState<>(this, entity, context.getCamera(), i);
                    shadersProgram.updatePerInstanceUniforms(instanceState);
                    model.render(instanceState, i);
                }
            }
        }

        shadersProgram.unbind();
    }

    @Override
    public void render(SceneContext context, Node.Condition condition) {
        if (renderList.isEmpty()) {
            return;
        }
        shadersProgram.bind();

        RenderState<Entity> renderState = new RenderState<>(this, context.getCamera());
        shadersProgram.updatePerRenderUniforms(renderState);

        for (var model : renderList.keySet()) {
            for (int i = 0; i < model.getMeshes().length; i++) {
                model.bindAndConfigure(i);
                for (var entity : renderList.get(model)) {
                    frustumIntersection.set(
                            context.getCamera().getProjectionViewMatrix().mul(
                                    entity.getTransform().getTransformationMatrix(), Matrix4.pool.poolAndGive()
                            )
                    );
                    if (!checkRenderPass(entity)) {
                        continue;
                    }
                    RenderState<Entity> instanceState = new RenderState<>(this, entity, context.getCamera(), i);
                    shadersProgram.updatePerInstanceUniforms(instanceState);
                    model.render(instanceState, i);
                }
            }
        }

        shadersProgram.unbind();
    }

    private boolean checkRenderPass(Entity entity) {
        return !checkClippingCulling(entity.getTransform().getPosition()) &&
                frustumIntersection.testAab(entity.getModel().getBounds().getMin(), entity.getModel().getBounds().getMax());
    }

    @Override
    public void cleanUp() {
        shadersProgram.delete();
    }
}