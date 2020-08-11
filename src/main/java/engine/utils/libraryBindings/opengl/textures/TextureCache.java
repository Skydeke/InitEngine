package engine.utils.libraryBindings.opengl.textures;

import java.util.HashMap;
import java.util.Map;

public final class TextureCache {

    private static final Map<String, TextureObject> CACHE = new HashMap<>();

    private TextureCache() {

    }

    public static void addToCache(String file, TextureObject texture) {
        CACHE.put(file, texture);
    }

    public static TextureObject getTexture(String file) {
        return CACHE.get(file);
    }
}
