package engine.architecture.scene.node;

import engine.utils.libraryBindings.maths.objects.Transform;
import engine.utils.libraryBindings.opengl.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Node {

    public int UUID;

    @Getter
    @Setter
    private boolean activated = true;
    @Getter
    @Setter
    private boolean selected = false;
    @Getter
    @Setter
    private boolean hidden = false;

    @Setter
    private String debugName;

    @Getter
    private Node parent;
    @Getter
    private List<Node> children;

    private Transform transform;

    public Node() {
        super();
        UUID = Utils.generateNewUUID_3D();
        this.transform = new Transform();
        this.children = new ArrayList<>();
    }

    public Transform getTransform() {
        return transform;
    }

    public void addChild(Node child) {
        child.setParent(this);
        children.add(child);
    }

    private void setParent(Node parent) {
        getTransform().addTransformation(parent.getTransform().getTransform());
        this.parent = parent;
    }

    public String getName() {
        if (debugName == null)
            return this.getClass().getSimpleName() + "#" + UUID;
        else
            return debugName;

    }

    public void addChildren(Node... children) {
        getChildren().addAll(Arrays.asList(children));
        for (Node child : children)
            child.setParent(this);
    }

    public ArrayList<Node> collect() {
        ArrayList<Node> ret = new ArrayList<>();
        ret.add(this);
        for (Node child : children)
            ret.addAll(child.collect());
        return ret;
    }

    public void update() {
        for (Node child : children)
            if (child.isActivated()) child.update();
    }

    public void render() {
        for (Node child : children)
            if (child.isActivated() && !child.isHidden()) {
                child.render();
            }
    }

    public void render(Condition condition) {
        for (Node child : children) {
            if (child.isActivated() && condition.isvalid(child)) {
                child.render(condition);
            }
        }
    }

    public void process() {
        for (Node child : children) {
            child.process();
        }
    }

    public void cleanup() {
        children.forEach(child -> child.cleanup());
    }

    public boolean isActivated() {
        return activated;
    }

    public void activate() {
        activated = true;
    }

    public void deactivate() {
        activated = false;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        for (Node child : children) child.setSelected(selected);
    }

    @FunctionalInterface
    public interface Condition {
        boolean isvalid(Node node);
    }

}