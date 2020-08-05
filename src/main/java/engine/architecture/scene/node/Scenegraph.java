package engine.architecture.scene.node;

import engine.architecture.system.Pipeline;
import engine.rendering.abstracted.postprocessing.effects.Effect;
import engine.rendering.instances.camera.Camera;
import engine.rendering.instances.renderers.sky.Sky;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Scenegraph extends Node {

    private final Camera camera;
    private final List<Effect> effects;
    @Getter
    private Sky sky;

    public Scenegraph(Camera camera) {
        super();
        this.camera = camera;
        this.sky = new Sky();
        this.effects = new ArrayList<>();
    }

    public void addEffect(Effect effect) {
        this.effects.add(effect);
    }

    public void processEffects(Pipeline renderer) {
        for (Effect effect : effects) {
            effect.processIfEnabled(renderer, camera);
        }
    }

    public <T extends Effect> T getEffect(Class<T> c) {
        return effects.stream().filter(c::isInstance).findFirst().map(c::cast).orElse(null);
    }

    public void process() {
        super.process();
        sky.process();
    }
}
