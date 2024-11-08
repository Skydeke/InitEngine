package engine.utils.libraryBindings.maths.noise;

import engine.utils.libraryBindings.maths.joml.Vector3f;
import engine.utils.libraryBindings.maths.utils.Vector3;

public interface Noise3f {

    Noise3f ONE = (x, y, z) -> 1;

    Noise3f LENGTH = (x, y, z) -> Vector3.length(x, y, z);

    float noise(float x, float y, float z);

    default float noise(Vector3f v) {
        return noise(v.x, v.y, v.z);
    }

}
