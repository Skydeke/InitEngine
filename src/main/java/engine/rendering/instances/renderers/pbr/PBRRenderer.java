package engine.rendering.instances.renderers.pbr;

import engine.architecture.models.Model;
import engine.architecture.scene.SceneContext;
import engine.architecture.scene.entity.Entity;
import engine.architecture.scene.node.Node;
import engine.rendering.abstracted.renderers.Renderer3D;
import engine.utils.libraryBindings.maths.joml.FrustumIntersection;
import engine.utils.libraryBindings.maths.joml.Vector3f;
import engine.utils.libraryBindings.maths.utils.Matrix4;
import engine.utils.libraryBindings.opengl.shaders.*;
import engine.utils.libraryBindings.opengl.textures.ITexture;
import engine.utils.libraryBindings.opengl.utils.GlUtils;
import lombok.Getter;

public class PBRRenderer extends Renderer3D<PBRModel> {

    private final ShadersProgram<PBRModel> shadersProgram;
    private final FrustumIntersection frustumIntersection = new FrustumIntersection();

    private static final String VERT_FILE = "res/shaders/pbr/pbr_vs.glsl";
    private static final String FRAG_FILE = "res/shaders/pbr/pbr_fs.glsl";

    @Getter
    private static final PBRRenderer instance;

    static {
        try {
            instance = new PBRRenderer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private PBRRenderer() throws Exception {
        this.shadersProgram = ShadersProgram.create(VERT_FILE, FRAG_FILE);

        shadersProgram.addPerInstanceUniform(new UniformBooleanProperty<>("map_albedo") {
            @Override
            public boolean getUniformValue(RenderState<PBRModel> state) {
                return state.getInstance().getMaterial().isAlbedoMapped();
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformTextureProperty<>("albedoMap", 0) {
            @Override
            public ITexture getUniformValue(RenderState<PBRModel> state) {
                return state.getInstance().getMaterial().getAlbedoMap();
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformValueProperty<>("albedoConst") {
            @Override
            public Vector3f getUniformValue(RenderState<PBRModel> state) {
                return state.getInstance().getMaterial().getAlbedoConst();
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformBooleanProperty<>("map_normal") {
            @Override
            public boolean getUniformValue(RenderState<PBRModel> state) {
                return state.getInstance().getMaterial().isNormalMapped();
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformTextureProperty<>("normalMap", 1) {
            @Override
            public ITexture getUniformValue(RenderState<PBRModel> state) {
                return state.getInstance().getMaterial().getNormalMap();
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformBooleanProperty<>("map_roughness") {
            @Override
            public boolean getUniformValue(RenderState<PBRModel> state) {
                return state.getInstance().getMaterial().isRoughnessMapped();
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformTextureProperty<>("roughnessMap", 2) {
            @Override
            public ITexture getUniformValue(RenderState<PBRModel> state) {
                return state.getInstance().getMaterial().getRoughnessMap();
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformFloatProperty<>("roughnessConst") {
            @Override
            public float getUniformValue(RenderState<PBRModel> state) {
                return state.getInstance().getMaterial().getRoughnessConst();
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformBooleanProperty<>("map_metal") {
            @Override
            public boolean getUniformValue(RenderState<PBRModel> state) {
                return state.getInstance().getMaterial().isMetalMapped();
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformTextureProperty<>("metalMap", 3) {
            @Override
            public ITexture getUniformValue(RenderState<PBRModel> state) {
                return state.getInstance().getMaterial().getMetalMap();
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformFloatProperty<>("metalConst") {
            @Override
            public float getUniformValue(RenderState<PBRModel> state) {
                return state.getInstance().getMaterial().getMetalConst();
            }
        });

        shadersProgram.addPerRenderUniform(new UniformValueProperty<>("projectionMatrix") {
            @Override
            public UniformValue getUniformValue(RenderState<PBRModel> state) {
                return getContext().getCamera().getProjectionMatrix();
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformValueProperty<>("modelMatrix") {
            @Override
            public UniformValue getUniformValue(RenderState<PBRModel> state) {
                return state.getInstance().getTransform().getTransformationMatrix();
            }
        });

        shadersProgram.addPerRenderUniform(new UniformValueProperty<>("viewMatrix") {
            @Override
            public UniformValue getUniformValue(RenderState<PBRModel> state) {
                return getContext().getCamera().getViewMatrix();
            }
        });

        shadersProgram.addPerInstanceUniform(new UniformFloatProperty<>("UVscale") {
            @Override
            public float getUniformValue(RenderState<PBRModel> state) {
                return state.getInstance().getUVscalar();
            }
        });
    }

    @Override
    public void render(SceneContext context) {
        if (renderList.isEmpty()) return;

        shadersProgram.bind();
        GlUtils.disableBlending();

        RenderState<PBRModel> renderState = new RenderState<>(this, context.getCamera());
        shadersProgram.updatePerRenderUniforms(renderState);

        for (Model model : renderList.keySet()) {
            for (int i = 0; i < model.getMeshes().length; i++) {
                model.bindAndConfigure(i);

                for (PBRModel entity : renderList.get(model)) {
                    frustumIntersection.set(
                            context.getCamera().getProjectionViewMatrix().mul(
                                    entity.getTransform().getTransformationMatrix(), Matrix4.pool.poolAndGive()
                            )
                    );
                    if (!checkRenderPass(entity)) continue;

                    RenderState<PBRModel> instanceState = new RenderState<>(this, entity, context.getCamera(), i);
                    shadersProgram.updatePerInstanceUniforms(instanceState);
                    model.render(instanceState, i);
                }
            }
        }
        shadersProgram.unbind();
    }

    @Override
    public void render(SceneContext context, Node.Condition condition) {
        if (renderList.isEmpty()) return;

        shadersProgram.bind();
        RenderState<PBRModel> renderState = new RenderState<>(this, context.getCamera());
        shadersProgram.updatePerRenderUniforms(renderState);

        for (Model model : renderList.keySet()) {
            for (int i = 0; i < model.getMeshes().length; i++) {
                model.bindAndConfigure(i);

                for (PBRModel entity : renderList.get(model)) {
                    frustumIntersection.set(
                            context.getCamera().getProjectionViewMatrix().mul(
                                    entity.getTransform().getTransformationMatrix(), Matrix4.pool.poolAndGive()
                            )
                    );
                    if (!checkRenderPass(entity)) continue;

                    RenderState<PBRModel> instanceState = new RenderState<>(this, entity, context.getCamera(), i);
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