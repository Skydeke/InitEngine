package engine.rendering.instances.renderers.pbr;

import engine.architecture.scene.light.Light;
import engine.architecture.scene.light.LightManager;
import engine.architecture.system.Config;
import engine.rendering.Shader;
import engine.utils.libraryWrappers.opengl.textures.TextureObject;
import lombok.Getter;

import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.*;

public class PBRDeferredShader extends Shader {

    @Getter
    public TextureObject targetTexture;
    private int numLights;

    public PBRDeferredShader() {
        super();

        createComputeShader("res/shaders/pbr/pbr_deferred_lighting_cs.glsl");
        link();

        addUniform("ssao");
        addUniform("ssaoPower");

        addUniform("camerapos");
        addUniform("numLights");

        addUniform("sun.color");
        addUniform("sun.intensity");
        addUniform("sun.direction");
        addUniform("sun.ambient");

        addUniform("isShadow");
        addUniform("shadowDepthMap");
        addUniform("shadowSpaceMatrix");

        addUniform("multisamples");

        numLights = Config.instance().getNumLights();
        for (int i = 0; i < numLights; i++) {
            addUniform("lights[" + i + "].color");
            addUniform("lights[" + i + "].intensity");
            addUniform("lights[" + i + "].position");
            addUniform("lights[" + i + "].activated");
        }
    }

    public void compute(TextureObject albedo, TextureObject position, TextureObject normal,
                        TextureObject lightDepth, TextureObject ssao, TextureObject out) {

        bind();

        // TODO: rework light manager
        int currlights = LightManager.getSceneLights().size();
        setUniform("numLights", currlights);
        for (int i = 0; i < currlights; i++) {
            Light currLight = LightManager.getLight(i);
            if (currLight == null || !currLight.isActivated())
                setUniform("lights[" + i + "].activated", 0);
            else {
                setUniform("lights[" + i + "].activated", 1);
                setUniform("lights[" + i + "].color", currLight.getColor());
                setUniform("lights[" + i + "].intensity", currLight.getIntensity());
                setUniform("lights[" + i + "].position", currLight.getTransform().getWorldPosition());
            }
        }

        if (LightManager.getSun() != null) {
            setUniform("sun.color", LightManager.getSun().getColor());
            setUniform("sun.intensity", LightManager.getSun().getIntensity());
            setUniform("sun.direction", LightManager.getSun().getTransform().getEulerAngles());
            setUniform("sun.ambient", Config.instance().getAmbientLight());
        }

        setUniform("ssao", Config.instance().isSsao() ? 1 : 0);
        setUniform("ssaoPower", Config.instance().getSsaoPower());

        setUniform("isShadow", Config.instance().isShadows() ? 1 : 0);
        activeTexture(lightDepth, 0);
        setUniform("shadowDepthMap", 0);
        setUniform("shadowSpaceMatrix", LightManager.getSun().getLightSpaceMatrix());


        setUniform("camerapos", boundContext.getCamera().getTransform().getPosition());

        setUniform("multisamples", Config.instance().getMultisamples());

        bindImage(0, albedo.getId(), GL_READ_ONLY, GL_RGBA16F);
        bindImage(1, position.getId(), GL_READ_ONLY, GL_RGBA32F);
        bindImage(2, normal.getId(), GL_READ_ONLY, GL_RGBA32F);
        bindImage(3, ssao.getId(), GL_READ_ONLY, GL_R16F);
        bindImage(4, out.getId(), GL_WRITE_ONLY, GL_RGBA16F);
        compute(16, 16, boundContext.getResolution());
        unbind();
    }
}
