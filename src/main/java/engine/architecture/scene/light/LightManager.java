package engine.architecture.scene.light;

import engine.architecture.system.Config;

import java.util.ArrayList;


public class LightManager {

    private static LightManager instance;
    private DirectionalLight sun;
    private ArrayList<Light> sceneLights;

    private LightManager() {
        sceneLights = new ArrayList<>();
    }

    private static LightManager instance() {
        if (instance == null)
            instance = new LightManager();
        return instance;
    }

    public static Boolean registerLight(Light light) {
        instance();
        if (light instanceof DirectionalLight)
            return true;
        if (instance().sceneLights.size() < Config.instance().getNumLights()) {
            instance().sceneLights.add(light);
            return true;
        }
        return false;
    }

    public static DirectionalLight getSun() {
        return instance().sun;
    }

    public static void setSun(DirectionalLight sun) {
        instance().sun = sun;
    }

    public static ArrayList<Light> getSceneLights() {
        return instance().sceneLights;
    }

    public static Light getLight(int index) {
        if (index >= instance().sceneLights.size()) {
            return null;
        }
        return instance().sceneLights.get(index);
    }
}
