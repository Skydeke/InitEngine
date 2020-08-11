package engine.fileLoaders;

import engine.utils.libraryBindings.opengl.textures.TextureCache;
import engine.utils.libraryBindings.opengl.textures.TextureObject;
import engine.utils.libraryBindings.opengl.utils.Utils;
import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AITexture;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL21.GL_SRGB8;
import static org.lwjgl.opengl.GL21.GL_SRGB8_ALPHA8;
import static org.lwjgl.opengl.GL30.GL_RGB16F;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;

public class ImageLoader {
    /**
     * Utility for loading image bytebuffers.
     *
     * @param filename image path with format "res/image/*"
     * @return flipped byte glapi with image data
     */
    public static ByteBuffer loadImage(String filename) {

        ByteBuffer buffer;

        try {
            buffer = Utils.ioResourceToBuffer(filename, 128 * 128);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer c = BufferUtils.createIntBuffer(1);

        if (!STBImage.stbi_info_from_memory(buffer, w, h, c)) {
            throw new RuntimeException("Failed to read image info: " + STBImage.stbi_failure_reason());
        }

        ByteBuffer image = STBImage.stbi_load_from_memory(buffer, w, h, c, 0);

        if (image == null) {
            throw new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason());
        }

        System.out.println("Image loaded :" + filename + "(" + w.get(0) + "," + h.get(0) + ")");

        return image;
    }

    /**
     * Utility for loading texture to openGL directly
     *
     * @param filename image path with format "res/images/*"
     * @return texture object with width, height, and handle
     */
    public static TextureObject loadTexture(String filename, boolean srgb) {
        if (TextureCache.getTexture(filename) != null){
//            System.out.println("LOADED CACHED TEXTURE!");
            return TextureCache.getTexture(filename);
        }

        ByteBuffer buffer;

        try {
            buffer = Utils.ioResourceToBuffer(filename, 128 * 128);
        } catch (IOException e) {
            return TextureObject.emptyTexture();
        } catch (NullPointerException e) {
            return TextureObject.emptyTexture();
        }

        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer c = BufferUtils.createIntBuffer(1);

        if (!STBImage.stbi_info_from_memory(buffer, w, h, c)) {
            throw new RuntimeException("Failed to read image info: " + STBImage.stbi_failure_reason());
        }

        ByteBuffer image = STBImage.stbi_load_from_memory(buffer, w, h, c, 0);

        if (image == null) {
            throw new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason());
        }

        int id = glGenTextures();
        TextureObject ret = new TextureObject(GL_TEXTURE_2D, w.get(0), h.get(0), id);
        ret.bilinearFilter();
        if (c.get(0) == 3) {
            if ((w.get(0) & 3) != 0) {
                glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (w.get(0) & 1));
            }
            if (srgb)
                ret.allocateImage2D(GL_SRGB8, GL_RGB, image);
            else
                ret.allocateImage2D(GL_RGB16F, GL_RGB, image);
        } else if (c.get(0) == 1) {
            ret.allocateImage2D(GL_RED, GL_RED, image);
        } else {
            if (srgb)
                ret.allocateImage2D(GL_SRGB8_ALPHA8, GL_RGBA, image);
            else
                ret.allocateImage2D(GL_RGBA16F, GL_RGBA, image);
        }

        STBImage.stbi_image_free(image);
        System.out.println("Texture " + id + " loaded: " + filename + " (" + w.get(0) + "," + h.get(0) + ")");
        TextureCache.addToCache(filename, ret);
        return ret;
    }

    /**
     * Utility for loading texture to openGL directly
     *
     * @param assimpTexture The Buffer in which the image is stored
     * @return texture object with width, height, and handle
     */
    public static TextureObject loadTextureFromBuffer(AITexture assimpTexture, boolean srgb) {
        if (TextureCache.getTexture(assimpTexture.hashCode() + "") != null){
//            System.out.println("LOADED CACHED TEXTURE!");
            return TextureCache.getTexture(assimpTexture.hashCode() + "");
        }

        ByteBuffer buffer = MemoryUtil.memByteBuffer(assimpTexture.pcData(0).address0(), assimpTexture.mWidth());
        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer c = BufferUtils.createIntBuffer(1);

        if (!STBImage.stbi_info_from_memory(buffer, w, h, c)) {
            throw new RuntimeException("Failed to read image info: " + STBImage.stbi_failure_reason());
        }

        ByteBuffer image = STBImage.stbi_load_from_memory(buffer, w, h, c, 0);

        if (image == null) {
            throw new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason());
        }

        int id = glGenTextures();
        TextureObject ret = new TextureObject(GL_TEXTURE_2D, w.get(0), h.get(0), id);
        ret.bilinearFilter();
        if (c.get(0) == 3) {
            if ((w.get(0) & 3) != 0) {
                glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (w.get(0) & 1));
            }
            if (srgb)
                ret.allocateImage2D(GL_SRGB8, GL_RGB, image);
            else
                ret.allocateImage2D(GL_RGB16F, GL_RGB, image);
        } else if (c.get(0) == 1) {
            ret.allocateImage2D(GL_RED, GL_RED, image);
        } else {
            if (srgb)
                ret.allocateImage2D(GL_SRGB8_ALPHA8, GL_RGBA, image);
            else
                ret.allocateImage2D(GL_RGBA16F, GL_RGBA, image);
        }

        STBImage.stbi_image_free(image);
        System.out.println("Texture " + id + " loaded: " + buffer.toString() + " (" + w.get(0) + "," + h.get(0) + ")");
        TextureCache.addToCache(assimpTexture.hashCode() + "", ret);
        return ret;
    }
}
