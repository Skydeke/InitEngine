package engine.utils.libraryBindings.opengl.textures;

import engine.utils.libraryBindings.opengl.textures.parameters.MagFilterParameter;
import engine.utils.libraryBindings.opengl.textures.parameters.MinFilterParameter;
import engine.utils.libraryBindings.opengl.textures.parameters.WrapParameter;

public class CubeMapTextureBuilder {

    private TextureInfo positiveX;
    private TextureInfo negativeX;
    private TextureInfo positiveY;
    private TextureInfo negativeY;
    private TextureInfo positiveZ;
    private TextureInfo negativeZ;

    /**
     * Sets the texture on the positive x
     *
     * @param textureFile the texture's file
     * @return this
     */
    public CubeMapTextureBuilder positiveX(String textureFile) throws Exception {
        positiveX = ImageLoader.load(textureFile);
        return this;
    }

    /**
     * Sets the texture on the negative x
     *
     * @param textureFile the texture's file
     * @return this
     */
    public CubeMapTextureBuilder negativeX(String textureFile) throws Exception {
        negativeX = ImageLoader.load(textureFile);
        return this;
    }

    /**
     * Sets the texture on the positive y
     *
     * @param textureFile the texture's file
     * @return this
     */
    public CubeMapTextureBuilder positiveY(String textureFile) throws Exception {
        positiveY = ImageLoader.load(textureFile);
        return this;
    }

    /**
     * Sets the texture on the negative y
     *
     * @param textureFile the texture's file
     * @return this
     */
    public CubeMapTextureBuilder negativeY(String textureFile) throws Exception {
        negativeY = ImageLoader.load(textureFile);
        return this;
    }

    /**
     * Sets the texture on the positive z
     *
     * @param textureFile the texture's file
     * @return this
     */
    public CubeMapTextureBuilder positiveZ(String textureFile) throws Exception {
        positiveZ = ImageLoader.load(textureFile);
        return this;
    }

    /**
     * Sets the texture on the negative z
     *
     * @param textureFile the texture's file
     * @return this
     */
    public CubeMapTextureBuilder negativeZ(String textureFile) throws Exception {
        negativeZ = ImageLoader.load(textureFile);
        return this;
    }

    public CubeMapTexture create() {
        CubeMapTexture texture = CubeMapTexture.of(positiveX, positiveY, positiveZ, negativeX, negativeY, negativeZ);
        TextureFunctions function = new TextureFunctions(texture, TextureTarget.TEXTURE_CUBE_MAP);
        function.minFilter(MinFilterParameter.LINEAR)
                .magFilter(MagFilterParameter.LINEAR)
                .wrapS(WrapParameter.CLAMP_TO_EDGE)
                .wrapT(WrapParameter.CLAMP_TO_EDGE);
        return texture;
    }

}
