package engine.architecture.componentsystem;

import engine.architecture.scene.node.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ComponentGroup {

    private final Node parent;
    private final List<GameComponent> gameComponents = new ArrayList<>();
    private final List<GameComponent> newComponents = new ArrayList<>();

    public ComponentGroup(Node parent) {
        this.parent = parent;
    }

    /**
     * Adds a component to the component group
     *
     * @param gameComponent the component to add
     */
    public void add(GameComponent gameComponent) {
        if (getNullable(gameComponent.getClass()) == null) {
            newComponents.add(gameComponent);
            gameComponents.add(gameComponent);
            gameComponent.init(parent, this);
        }
    }

    /**
     * Removes a component to the component group
     *
     * @param gameComponent the component to add
     */
    public void remove(GameComponent gameComponent) {
        newComponents.remove(gameComponent);
        gameComponents.remove(gameComponent);
        gameComponent.stop();
    }

    /**
     * Returns a component of the same class as received
     *
     * @param compClass the class of the required component
     * @param <T>       the type of the component
     * @return a component of the class received
     * @throws NoSuchElementException if component is no to be found
     */
    public <T extends GameComponent> T get(Class<T> compClass) {
        final T component = getNullable(compClass);
        if (component == null) {
            throw new NoSuchElementException("Cannot find component of class " + compClass.getSimpleName());
        }
        return component;
    }

    /**
     * Returns a nullable component of the same class as received
     *
     * @param compClass the class of the required component
     * @param <T>       the type of the component
     * @return a component of the class received, null if not found
     */
    public <T extends GameComponent> T getNullable(Class<T> compClass) {
        for (GameComponent gameComponent : gameComponents) {
            if (compClass.isInstance(gameComponent)) {
                return compClass.cast(gameComponent);
            }
        }
        return null;
    }

    /**
     * Updates all the components
     */
    public void update() {
        startComponents();
        for (GameComponent gameComponent : gameComponents) {
            gameComponent.updateIfEnabled();
        }
    }

    private void startComponents() {
        newComponents.forEach(GameComponent::start);
        newComponents.clear();
    }

    public <T extends GameComponent> boolean hasComponent(Class<T> compClass) {
        return getNullable(compClass) != null;
    }
}
