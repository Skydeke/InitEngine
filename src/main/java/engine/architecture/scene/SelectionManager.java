package engine.architecture.scene;

import engine.architecture.scene.node.Node;

import java.util.ArrayList;
import java.util.stream.Stream;

public class SelectionManager {

    private ArrayList<Node> selected;

    public SelectionManager() {
        selected = new ArrayList<>();
    }

    public void addSelection(Node node) {
        node.setSelected(true);
        selected.add(node);
    }

    public void remove(Node node) {
        node.setSelected(false);
        selected.remove(node);
    }

    public void clear() {
        selected.forEach(e -> e.setSelected(false));
        selected.clear();
    }

    public Stream<Node> stream() {
        return selected.stream();
    }
}
