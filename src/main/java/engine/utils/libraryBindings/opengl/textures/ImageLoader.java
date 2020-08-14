package engine.utils.libraryBindings.opengl.textures;

import engine.utils.libraryBindings.opengl.constants.DataType;
import engine.utils.libraryBindings.opengl.constants.FormatType;
import engine.utils.libraryBindings.opengl.textures.parameters.MagFilterParameter;
import engine.utils.libraryBindings.opengl.textures.parameters.MinFilterParameter;
import engine.utils.libraryBindings.opengl.utils.Utils;
import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AITexture;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.glPixelStorei;

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
    public static TextureInfo load(String filename) {

        ByteBuffer buffer = null;
        try {
            System.err.println("Failed to load: " + filename);
            buffer = Utils.ioResourceToBuffer(filename, 128 * 128);
        } catch (IOException e) {
            e.printStackTrace();
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

        STBImage.stbi_image_free(image);
        return new TextureInfo(w.get(), h.get(), image);
    }

    /**
     * Utility for loading texture to openGL directly
     *
     * @param assimpTexture the by Assimp provided textureData
     * @return texture object with width, height, and handle
     */
    public static TextureInfo load(AITexture assimpTexture) {

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

        STBImage.stbi_image_free(image);
        return new TextureInfo(w.get(), h.get(), image);
    }
    /**
     * Utility for loading texture to openGL directly
     *
     * @param filename image path with format "res/images/*"
     * @return texture object with width, height, and handle
     */
    public static Texture loadTexture(String filename, boolean srgb) {
        if (TextureCache.getTexture(filename) != null){
//            System.out.println("LOADED CACHED TEXTURE!");
            return (Texture) TextureCache.getTexture(filename);
        }

        ByteBuffer buffer;

        try {
            buffer = Utils.ioResourceToBuffer(filename, 128 * 128);
        } catch (IOException | NullPointerException e) {
            return Texture.NONE;
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
        TextureBuilder retB = Texture.builder()
                .setMinFilter(MinFilterParameter.LINEAR_MIPMAP_LINEAR)
                .setMagFilter(MagFilterParameter.LINEAR)
                .setAnisotropicFilter(4f)
                .setGenerateMipMap(true);
        if (c.get(0) == 3) {
            if ((w.get(0) & 3) != 0) {
                glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (w.get(0) & 1));
            }
            if (srgb)
                retB.setTextureData(w.get(), h.get(), image, FormatType.SRGB8, FormatType.RGB, DataType.U_BYTE);
            else
                retB.setTextureData(w.get(), h.get(), image, FormatType.RGB16F, FormatType.RGB, DataType.U_BYTE);
        } else if (c.get(0) == 1) {
            retB.setTextureData(w.get(), h.get(), image, FormatType.RED, FormatType.RED, DataType.U_BYTE);
        } else {
            if (srgb)
                retB.setTextureData(w.get(), h.get(), image, FormatType.SRGB8_ALPHA8, FormatType.RGBA, DataType.U_BYTE);
            else
                retB.setTextureData(w.get(), h.get(), image, FormatType.RGBA16F, FormatType.RGBA, DataType.U_BYTE);
        }
        Texture ret = retB.create();
        STBImage.stbi_image_free(image);
        System.out.println("Texture " + TextureCache.getCacheSize() + " loaded: " + filename + " (" + w.get(0) + "," + h.get(0) + ")");
        TextureCache.addToCache(filename, ret);
        return ret;
    }

    /**
     * Utility for loading texture to openGL directly
     *
     * @param assimpTexture The Buffer in which the image is stored
     * @return texture object with width, height, and handle
     */
    public static Texture loadTextureFromBuffer(AITexture assimpTexture, boolean srgb) {
        if (TextureCache.getTexture(assimpTexture.hashCode() + "") != null){
//            System.out.println("LOADED CACHED TEXTURE!");
            return (Texture) TextureCache.getTexture(assimpTexture.hashCode() + "");
        }

        ByteBuffer buffer = MemoryUtil.memByteBuffer(assimpTexture.pcData(assimpTexture.mWidth()).address0(),
                assimpTexture.mWidth());
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

        TextureBuilder retB = Texture.builder()
                .setMinFilter(MinFilterParameter.LINEAR)
                .setMagFilter(MagFilterParameter.LINEAR)
                .setAnisotropicFilter(4f)
                .setGenerateMipMap(true);
        if (c.get(0) == 3) {
            if ((w.get(0) & 3) != 0) {
                glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (w.get(0) & 1));
            }
            if (srgb)
                retB.setTextureData(w.get(), h.get(), image, FormatType.SRGB8, FormatType.RGB, DataType.U_BYTE);
            else
                retB.setTextureData(w.get(), h.get(), image, FormatType.RGB16F, FormatType.RGB, DataType.U_BYTE);
        } else if (c.get(0) == 1) {
            retB.setTextureData(w.get(), h.get(), image, FormatType.RED, FormatType.RED, DataType.U_BYTE);
        } else {
            if (srgb)
                retB.setTextureData(w.get(), h.get(), image, FormatType.SRGB8_ALPHA8, FormatType.RGBA, DataType.U_BYTE);
            else
                retB.setTextureData(w.get(), h.get(), image, FormatType.RGBA16F, FormatType.RGBA, DataType.U_BYTE);
        }
        Texture ret = retB.create();
        STBImage.stbi_image_free(image);
        System.out.println("Texture " + TextureCache.getCacheSize()  + " loaded: " + buffer.toString() + " (" + w.get(0) + "," + h.get(0) + ")");
        TextureCache.addToCache(assimpTexture.hashCode() + "", ret);
        return ret;
    }
}
