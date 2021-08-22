package engine.architecture.scene.node;

import engine.utils.libraryBindings.maths.objects.Transform;
import engine.utils.libraryBindings.opengl.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Node {

    public int UUID;
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

    public void removeChild(Node child) {
        children.remove(child);
    }

    private void setParent(Node parent) {
        getTransform().addTransformation(parent.getTransform().getTransform());
        this.parent = parent;
    }

    public String getName() {
        return Objects.requireNonNullElseGet(debugName, () -> this.getClass().getSimpleName() + "#" + UUID);
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
            child.update();
    }

    public void process() {
        children.forEach(Node::process);
    }

    public void cleanup() {
        children.forEach(Node::cleanup);
    }


    @FunctionalInterface
    public interface Condition {
        boolean isvalid(Node node);
    }

}