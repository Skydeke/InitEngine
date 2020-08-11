package engine.utils.libraryBindings.maths.transform;

import engine.utils.libraryBindings.maths.joml.Vector3f;
import engine.utils.libraryBindings.maths.utils.Vector3;

public class Position {

    private final Vector3f value;

    private Position(Vector3f value) {
        this.value = value;
    }

    public static Position of(float x, float y, float z) {
        Vector3f value = Vector3.of(x, y, z);
        return new Position(value);
    }

    public static Position create() {
        Vector3f position = Vector3.create();
        return new Position(position);
    }

    public Vector3f getValue() {
        return value;
    }
}
