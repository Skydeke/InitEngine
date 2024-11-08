package instance;

import engine.architecture.componentsystem.LightComponent;
import engine.architecture.event.InputManager;
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
import engine.fileLoaders.ModelLoader;
import engine.rendering.instances.camera.FlyCamera;
import engine.rendering.instances.postprocessing.bloom.Bloom;
import engine.rendering.instances.postprocessing.celshading.CelShading;
import engine.rendering.instances.postprocessing.contrast.Contrast;
import engine.rendering.instances.postprocessing.radialblur.RadialBlur;
import engine.rendering.instances.postprocessing.ssr.SSRP;
import engine.rendering.instances.postprocessing.hdr.Hdr;
import engine.rendering.instances.postprocessing.gammacorrection.GammaCorrection;
import engine.rendering.instances.renderers.UUIDRenderer;
import engine.rendering.instances.renderers.entity.EntityRenderer;
import engine.rendering.instances.renderers.pbr.PBRMaterial;
import engine.rendering.instances.renderers.pbr.PBRModel;
import engine.rendering.instances.renderers.shadow.ShadowRenderer;
import engine.utils.libraryBindings.maths.objects.Transform;
import engine.utils.libraryBindings.opengl.utils.GlUtils;

import static org.lwjgl.glfw.GLFW.*;

public class Game extends SimpleApplication {

  private Node sceneRoot, lights;
  private double duration = 1;
  private PBRModel dragon1;

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

    // renderer.getSceneContext().getPipeline().getPostProcessing().add(new
    // Contrast(1.4f));
    // renderer.getSceneContext().getPipeline().getPostProcessing().add(new
    // Bloom(.1f, .1f, .1f));
    // renderer.getSceneContext().getPipeline().getPostProcessing().add(new
    // CelShading());
    // renderer.getSceneContext().getPipeline().getPostProcessing().add(new Hdr());
    // renderer.getSceneContext().getPipeline().getPostProcessing().add(new
    // RadialBlur());
    // renderer.getSceneContext().getPipeline().getPostProcessing().add(new
    // GammaCorrection());

    Model cube = ModelLoader.load("/models/cube.gltf");
    PBRMaterial fu = new PBRMaterial("images/plastic_squares/", false);
    dragon1 = new PBRModel(cube, fu);

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
    Model m = ModelLoader.load("/models/mat_test.obj");

    PBRModel m1 = new PBRModel(m, new PBRMaterial(15f, 0.488f, 0.5f, 0.95f, 0f));
    m1.getTransform().setPosition(0, 0, -15);
    m1.getTransform().setScale(2f);
    PBRModel m2 = new PBRModel(m, new PBRMaterial("images/chipped_paint/", false));
    m2.getTransform().setPosition(0, 0, -10);
    m2.getTransform().setScale(2f);

    Model model = ModelLoader.load("/models/scene.gltf");
    // Model model = ModelLoader.load("/models/sceneFile/nIScene.gltf");
    Entity e = new Entity(model) {
      @Override
      public void process() {
        EntityRenderer.getInstance().process(this);
        ShadowRenderer.getInstance().process(this);
        UUIDRenderer.getInstance().process(this);
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
    LightManager.getSun().setIntensity(5f).getTransform().setRotation(0, -0.5f, 0);

    context.getScene().addChildren(sceneRoot, lights);

    context.getCamera().setController(new FlyCamera());
    // context.getCamera().setController(new
    // ThirdPersonCamera(dragon1.getTransform(), context.getCamera()));
  }

  private double t, q;
  private boolean move = true;

  @Override
  public void onUpdate(double timeDelta) {
    t = t + duration;
    if (InputManager.instance().isKeyReleased(GLFW_KEY_E)) {
      move = !move;
    }
    if (InputManager.instance().isKeyPressed(GLFW_KEY_F5)) {
      GlUtils.drawPolygonLine();
    }
    if (InputManager.instance().isKeyPressed(GLFW_KEY_F6)) {
      GlUtils.drawPolygonFill();
    }

    if (InputManager.instance().isKeyReleased(GLFW_KEY_U)) {
      dragon1.getComponents().add(new LightComponent());
    }
    if (InputManager.instance().isKeyReleased(GLFW_KEY_I)) {
      if (dragon1.getComponents().hasComponent(LightComponent.class)) {
        LightComponent lc = dragon1.getComponents().get(LightComponent.class);
        dragon1.getComponents().remove(lc);
      }
    }
    if (InputManager.instance().isKeyPressed(GLFW_KEY_O)) {
      dragon1.getTransform().addPosition(0, +0.1f, 0);
    }
    if (InputManager.instance().isKeyPressed(GLFW_KEY_P)) {
      dragon1.getTransform().addPosition(0, -0.1f, 0);
    }

    this.duration = timeDelta;
    LightManager.getSun().update();
    if (move)
      q += this.duration;
    // LightManager.getSun().getTransform().setRotation(0, (float)
    // (-0.007*Math.cos(Math.PI*2/24*t)), -0.043f);
    Transform sunLocation = LightManager.getSun().getTransform();
    sunLocation.setRotation(0, clamp(0, 1, (float) Math.sin(t)), (float) Math.cos(t));
    lights.getTransform().setPosition((float) (Math.sin(q / 4)) * 20, 1f, 0f);
  }

  public float clamp(float min, float max, float v) {
    if (v > min && v < max) {
      return v;
    } else if (v < min) {
      return min;
    } else if (v > max) {
      return max;
    }
    return v;
  }

  private void buildFloor() {

    int amount = 4;
    float scale = 6;

    PBRModel model;
    PBRMaterial mat = new PBRMaterial("images/black_marble/",
        "albedo.tga", "normal.tga", "rough.tga", "metal.png", false);
    mat.setMetalConst(0.05f);
    mat.useMetalMap(false);

    PBRMaterial mat2 = new PBRMaterial("images/tiles/", "jpg", false);
    mat2.setMetalConst(0.4f);
    mat2.useMetalMap(false);

    for (int i = -amount / 2; i < amount / 2; i++) {
      for (int j = -amount / 2; j < amount / 2; j++) {

        if ((i == -1 || i == 0) && (j == -1 || j == 0))
          model = new PBRModel(ModelLoader.thickquad, mat2);
        else
          model = new PBRModel(ModelLoader.thickquad, mat);
        model.getTransform().addPosition(i * scale * 2, 0, j * scale * 2);
        model.getTransform().setScale(scale);
        model.getTransform().setRotation(-90, 0, 0);

        sceneRoot.addChild(model);
      }
    }
  }

  private void buildWalls() {
    PBRModel model;

    PBRMaterial mat = new PBRMaterial("images/stone_wall/", "jpg", false);
    // PBRMaterial mat = new PBRMaterial("images/metal_plates/","jpg", false);

    float scale = 6;
    int amount = 4;

    for (int i = 0; i < amount; i++) {
      model = new PBRModel(ModelLoader.thickquad, mat);
      model.getTransform().addPosition(2 * scale * (i - amount / 2.0f), -model.getModel().getLowest() * scale - .1f,
          -(1 + amount) * scale);
      model.getTransform().setScale(scale);
      model.setUVscalar(1f);
      sceneRoot.addChild(model);
    }

    for (int i = 0; i < amount; i++) {
      model = new PBRModel(ModelLoader.thickquad, mat);
      model.getTransform().addPosition(-(1 + amount) * scale, -model.getModel().getLowest() * scale - .1f,
          2 * scale * (i - amount / 2.0f));
      model.getTransform().setScale(scale);
      model.getTransform().addRotation(0, 90, 0);
      model.setUVscalar(1f);
      sceneRoot.addChild(model);
    }

    for (int i = 0; i < amount; i++) {
      model = new PBRModel(ModelLoader.thickquad, mat);
      model.getTransform().addPosition(2 * scale * (i - amount / 2.0f), -model.getModel().getLowest() * scale - .1f,
          (amount - 1) * scale);
      model.getTransform().setScale(scale);
      model.getTransform().addRotation(0, 180, 0);
      model.setUVscalar(1f);
      sceneRoot.addChild(model);
    }

    for (int i = 0; i < amount; i++) {
      model = new PBRModel(ModelLoader.thickquad, mat);
      model.getTransform().addPosition((amount - 1) * scale, -model.getModel().getLowest() * scale - .1f,
          2 * scale * (i - amount / 2.0f));
      model.getTransform().setScale(scale);
      model.getTransform().addRotation(0, -90, 0);
      model.setUVscalar(1f);
      sceneRoot.addChild(model);
    }

  }
}
