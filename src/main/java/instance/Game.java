package instance;

import engine.architecture.models.Model;
import engine.architecture.scene.entity.Entity;
import engine.architecture.scene.light.DirectionalLight;
import engine.architecture.scene.light.LightManager;
import engine.architecture.scene.light.PointLight;
import engine.architecture.scene.node.Node;
import engine.architecture.system.AppContext;
import engine.architecture.system.GameEngine;
import engine.architecture.system.SimpleApplication;
import engine.architecture.system.Window;
import engine.architecture.ui.event.InputManager;
import engine.fileLoaders.ModelLoader;
import engine.rendering.instances.camera.FlyCamera;
import engine.rendering.instances.renderers.entity.EntityRenderer;
import engine.rendering.instances.renderers.pbr.PBRMaterial;
import engine.rendering.instances.renderers.pbr.PBRModel;
import engine.rendering.instances.renderers.shadow.ShadowRenderer;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_T;

public class Game extends SimpleApplication {

    private Node sceneRoot, lights;
    private double duration = 1;

    public static void main(String[] args) {
        try {
            SimpleApplication game = new Game();
            GameEngine gameEngine = new GameEngine(game);
            gameEngine.init();
            gameEngine.start();
        } catch (Exception e) {
            System.err.println("Error running the game");
            e.printStackTrace();
        }
    }


    @Override
    public void onInit(Window window, AppContext renderer) throws Exception {
        sceneRoot = new Node();
        lights = new Node();


        Model cube = ModelLoader.load("/models/cube.gltf");
        PBRMaterial fu = new PBRMaterial("images/plastic_squares/", false);
        Entity dragon1 = new Entity(cube) {
            @Override
            public void process() {
                EntityRenderer.getInstance().process(this);
                ShadowRenderer.getInstance().process(this);
            }
        };

        Model mesh = ModelLoader.load("/models/dragon.obj");
        PBRModel dragon2 = new PBRModel(mesh, new PBRMaterial("images/chipped_paint/",
                "albedo.png", "normal.png", "rough.png", "metal.png", false));
        dragon1.getTransform().setScale(.8f);
        dragon1.getTransform().setPosition(0f, 0, -22);
        dragon2.getTransform().setScale(.8f);
        dragon2.getTransform().setPosition(-13f, 0, -22);
        dragon2.getTransform().setRotation((float) (Math.PI), 0, 0);
        sceneRoot.addChildren(dragon1, dragon2);

        buildFloor();
        buildWalls();

        /* material testers **/
        Node matTesters = new Node();
        Model m = ModelLoader.load("/models/doublebarrel.obj");

        PBRModel m1 = new PBRModel(m, new PBRMaterial(0.15f, 0.488f, 0.5f, 0.95f, 0f));
        m1.getTransform().setPosition(0, 0, -15);
        m1.getTransform().setScale(2f);
        PBRModel m2 = new PBRModel(m, new PBRMaterial("images/chipped_paint/", false));
        m2.getTransform().setPosition(0, 0, -10);
        m2.getTransform().setScale(2f);

        Model modl5 = ModelLoader.load("/models/sceneFile/nIScene.gltf");
        Entity e = new Entity(modl5) {
            @Override
            public void process() {
                EntityRenderer.getInstance().process(this);
                ShadowRenderer.getInstance().process(this);
            }
        };
        e.getTransform().setPosition(70, 0, 0);
        e.getTransform().setScale(2f);
        sceneRoot.addChild(e);

        PBRMaterial mat = new PBRMaterial("images/streaked_metal/", false);
        mat.useNormalMap(false);
        mat.setMetalConst(1.0f);
        mat.useMetalMap(false);
        PBRModel m3 = new PBRModel(m, mat);
        m3.getTransform().setPosition(0, 0, -5);
        m3.getTransform().setScale(2f);

        PBRMaterial mat4 = new PBRMaterial("images/metal/", true);
        PBRModel m4 = new PBRModel(m, mat4);
        m4.getTransform().setPosition(0, 0, 0);
        m4.getTransform().setScale(2f);

        PBRModel m5 = new PBRModel(m, new PBRMaterial("images/plastic_squares/", false));
        m5.getTransform().setPosition(0, 0, 5);
        m5.getTransform().setScale(2f);

        matTesters.getTransform().addPosition(12, -m1.getModel().getLowest(), 0);
        matTesters.addChildren(m1, m2, m3, m4, m5);
        sceneRoot.addChild(matTesters);

        PointLight light = new PointLight();
        light.setColor(0.1f, 0.5f, 1f).setIntensity(20f).getTransform().setPosition(-10, 0, -10);
        PointLight light1 = new PointLight();
        light1.setColor(0.1f, 0.5f, 1f).setIntensity(20f).getTransform().setPosition(-10, 0, -20);
        PointLight light2 = new PointLight();
        light2.setColor(0.1f, 0.5f, 1f).setIntensity(20f).getTransform().setPosition(-20, 0, -20);
        PointLight light3 = new PointLight();
        light3.setColor(0.1f, 0.5f, 1f).setIntensity(20f).getTransform().setPosition(-20, 0, -10);
        lights.addChildren(light, light1, light2, light3);


        LightManager.setSun(new DirectionalLight());
        LightManager.getSun().setIntensity(1.2f).getTransform().setRotation(0, -1, 0);

        context.getScene().addChildren(sceneRoot, lights);

        context.getCamera().setController(new FlyCamera());
    }


    private double t, q;
    private boolean move = true;

    @Override
    public void onUpdate(double timeDelta) {
        if(InputManager.instance().isKeyHeld(GLFW_KEY_T)){
            t = t + duration;
        }
        if(InputManager.instance().isKeyReleased(GLFW_KEY_E)){
            move = !move;
        }
        this.duration = timeDelta;
        LightManager.getSun().update();
        if (move)
            q += this.duration;

        lights.getTransform().setPosition((float) (Math.sin(q / 4)) * 20, 1f, 0f);
        LightManager.getSun().getTransform().setRotation((float) (Math.sin(t) * 0.5), 0.5f, (float) -(Math.cos(t) * 0.5));

//        System.out.print("\u001B[32m" + "\r fps: " + GameEngine.FRAMES_PER_SECOND + "\u001B[0m");
    }

    private void buildFloor() {

        int amount = 4;
        float scale = 6;

        PBRModel model;
        PBRMaterial mat = new PBRMaterial("images/white_marble/",
                "albedo.tga", "normal.tga", "rough.tga", "metal.png", false);
        mat.setMetalConst(0.05f);
//        mat.setMetalConst(0.85f);
//        mat.setMetalConst(1f);
        mat.useMetalMap(false);

        PBRMaterial mat2 = new PBRMaterial("images/wood_floor/", false);
        mat2.setMetalConst(0.4f);
        mat2.useMetalMap(false);

        for (int i = -amount / 2; i < amount / 2; i++) {
            for (int j = -amount / 2; j < amount / 2; j++) {

                if ((i == -1 || i == 0) && (j == -1 || j == 0)) model = new PBRModel(ModelLoader.thickquad, mat2);
                else model = new PBRModel(ModelLoader.thickquad, mat);
                model.getTransform().addPosition(i * scale * 2, 0, j * scale * 2);
                model.getTransform().setScale(scale);
                model.getTransform().setRotation(-90, 0, 0);

                sceneRoot.addChild(model);
            }
        }
    }

    private void buildWalls() {
        PBRModel model;

        PBRMaterial mat = new PBRMaterial("images/black_marble/",
                "albedo.tga", "normal.tga", "rough.tga", "metal.png", false);

        float scale = 6;
        int amount = 4;

        for (int i = 0; i < amount; i++) {
            model = new PBRModel(ModelLoader.thickquad, mat);
            model.getTransform().addPosition(2 * scale * (i - amount / 2.0f), -model.getModel().getLowest() * scale - .1f, -(1 + amount) * scale);
            model.getTransform().setScale(scale);
            model.setUVscalar(1f);
            sceneRoot.addChild(model);
        }

        for (int i = 0; i < amount; i++) {
            model = new PBRModel(ModelLoader.thickquad, mat);
            model.getTransform().addPosition(-(1 + amount) * scale, -model.getModel().getLowest() * scale - .1f, 2 * scale * (i - amount / 2.0f));
            model.getTransform().setScale(scale);
            model.getTransform().addRotation(0, 90, 0);
            model.setUVscalar(1f);
            sceneRoot.addChild(model);
        }

        for (int i = 0; i < amount; i++) {
            model = new PBRModel(ModelLoader.thickquad, mat);
            model.getTransform().addPosition(2 * scale * (i - amount / 2.0f), -model.getModel().getLowest() * scale - .1f, (amount - 1) * scale);
            model.getTransform().setScale(scale);
            model.getTransform().addRotation(0, 180, 0);
            model.setUVscalar(1f);
            sceneRoot.addChild(model);
        }

        for (int i = 0; i < amount; i++) {
            model = new PBRModel(ModelLoader.thickquad, mat);
            model.getTransform().addPosition((amount - 1) * scale, -model.getModel().getLowest() * scale - .1f, 2 * scale * (i - amount / 2.0f));
            model.getTransform().setScale(scale);
            model.getTransform().addRotation(0, -90, 0);
            model.setUVscalar(1f);
            sceneRoot.addChild(model);
        }

    }
}
