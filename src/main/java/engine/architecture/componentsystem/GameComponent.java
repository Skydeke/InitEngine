package engine.architecture.componentsystem;

import engine.architecture.scene.node.Node;

public abstract class GameComponent extends Node{

    private Node parent;
    private ComponentGroup group;
    private boolean enabled = true;

    /**
     * Sets the game object that will the component will update
     *
     * @param parent the parent game object
     * @param group  the parent's components
     */
    final void init(Node parent, ComponentGroup group) {
        this.parent = parent;
        this.group = group;
    }

    /**
     * Sets whether the component is enable or not
     *
     * @param enabled true to enable the component false to disable
     */
    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Updates the component only if its enable
     * Every component is enable by default
     */
    public final void updateIfEnabled() {
        if (enabled) update();
    }

    /**
     * Initialize the component, get all the other components needed etc...
     */
    public void start() {

    }

    /**
     * Destroy the component
     */
    public void stop() {

    }

    /**
     * Update the component, the method is called once per frame
     */
    public void update() {

    }

    /**
     * Returns a component with the same type as received
     *
     * @param compClass the class of the required component
     * @param <T>       the type of the component
     * @return the component
     */
    protected final <T extends GameComponent> T getComponent(Class<T> compClass) {
        return group != null ? group.get(compClass) : null;
    }

    protected final <T extends GameComponent> T getNullableComponent(Class<T> compClass) {
        return group != null ? group.getNullable(compClass) : null;
    }

    protected final <T extends GameComponent> boolean hasComponent(Class<T> compClass) {
        if (group == null){
            return false;
        }else return group.getNullable(compClass) != null;
    }
}
