package engine.rendering.abstracted.postprocessing.effects;

import engine.architecture.scene.node.Node;
import engine.architecture.system.Pipeline;
import engine.rendering.instances.camera.Camera;

/**
 * This class represent an effect in the scene such as water reflection or shadows
 * which would be difficult to achieve with regular rendering
 *
 * @author Saar ----
 * @version 1.2
 * @since 14.2.2018
 */
public abstract class Effect {

    protected final Node group;
    private boolean enabled = true;

    protected Effect(Node group) {
        this.group = group;
    }

    public abstract void process(Pipeline renderManager, Camera camera);

    public final void processIfEnabled(Pipeline renderManager, Camera camera) {
        if (enabled) process(renderManager, camera);
    }

    public final void delete() {
        onDelete();
    }

    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    /**
     * Invoked when the effect is deleted
     */
    protected void onDelete() {

    }

    /**
     * Invoked when the effect is enabled
     */
    protected void onEnable() {

    }

    /**
     * Invoked when the effect is disabled
     */
    protected void onDisable() {

    }
}
