package engine.architecture.scene.entity;

import engine.architecture.componentsystem.ComponentBased;
import engine.architecture.componentsystem.ComponentGroup;
import engine.architecture.models.Model;
import engine.architecture.scene.node.Node;
import engine.rendering.abstracted.Processable;

public abstract class Entity extends Node implements ComponentBased, Processable {

    private final Model model;

    private final ComponentGroup components = new ComponentGroup(this);

    public Entity(Model model) {
        this.model = model;
    }

    @Override
    public ComponentGroup getComponents() {
        return components;
    }

    public abstract void process();

    @Override
    public Model getModel(){
        return model;
    }

    @Override
    public void update() {
        getComponents().update();
    }

    public void delete() {
        model.delete();
    }

    public int getUUID() {
        return super.UUID;
    }

}
