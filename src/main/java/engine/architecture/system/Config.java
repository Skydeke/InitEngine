package engine.architecture.system;

import engine.utils.libraryWrappers.maths.joml.Vector2i;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

import java.io.InputStream;
import java.util.Properties;

@Getter
@Setter
public class Config {

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private static Config instance;
    boolean shadows;
    private int windowWidth;
    private int windowHeight;
    private String windowName;
    private boolean vsync;
    private int multisamples;
    private String renderEngine;
    private int numLights;
    private int shadowBufferWidth;
    private int shadowBufferHeight;
    private boolean ssao;
    private float ssaoRadius;
    private int ssaoSamples;
    private float ssaoPower;
    private boolean ssr;
    private int ssrRaymarchSteps;
    private int ssrBinarySearchSteps;
    private float ssrRayStepLen;
    private float ssrFalloff;
    private int ssrSamples;
    private boolean debugLayer;
    private boolean isWireframe;
    private Vector3f wireframeColor;
    private float ambientLight;

    public Config() {
        loadFromConfigFile();
    }

    public static Config instance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public void loadFromConfigFile() {

        Properties properties = new Properties();

        try {
            String path = "/config.properties";
            InputStream is = getClass().getResourceAsStream(path);
            properties.load(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // max lights
        numLights = Integer.valueOf(properties.getProperty("numLights"));

        // window settings
        windowWidth = Integer.valueOf(properties.getProperty("windowWidth"));
        windowHeight = Integer.valueOf(properties.getProperty("windowHeight"));
        windowName = properties.getProperty("windowName");
        vsync = properties.getProperty("isvsync").equalsIgnoreCase("true");
        multisamples = Integer.valueOf(properties.getProperty("multisamples"));

        // engine instance
        renderEngine = properties.getProperty("pipeline");

        // shadow settings
        shadows = Boolean.valueOf(properties.getProperty("shadows"));
        shadowBufferWidth = Integer.valueOf(properties.getProperty("shadow_buffer_x"));
        shadowBufferHeight = Integer.valueOf(properties.getProperty("shadow_buffer_y"));

        // SSAO settings
        ssao = Boolean.valueOf(properties.getProperty("ssao"));
        ssaoRadius = Float.valueOf(properties.getProperty("ssaoRadius"));
        ssaoSamples = Integer.valueOf(properties.getProperty("ssaoSamples"));
        ssaoPower = Float.valueOf(properties.getProperty("ssaoPower"));

        // SSR settings
        ssr = Boolean.valueOf(properties.getProperty("ssr"));
        ssrRaymarchSteps = Integer.valueOf(properties.getProperty("ssrRaymarchSteps"));
        ssrBinarySearchSteps = Integer.valueOf(properties.getProperty("ssrBinarySearchSteps"));
        ssrRayStepLen = Float.valueOf(properties.getProperty("ssrRayStepLen"));
        ssrFalloff = Float.valueOf(properties.getProperty("ssrFalloff"));
        ssrSamples = Integer.valueOf(properties.getProperty("ssrSamples"));

        // generic layer
        debugLayer = Boolean.valueOf(properties.getProperty("debug_layer"));

        ambientLight = Float.valueOf(properties.getProperty("ambientLight"));
    }

    public Vector2i getShadowBufferSize() {
        return new Vector2i(getShadowBufferWidth(), getShadowBufferHeight());
    }
}
