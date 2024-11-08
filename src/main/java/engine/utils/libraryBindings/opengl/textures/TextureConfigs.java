package engine.utils.libraryBindings.opengl.textures;


import engine.utils.libraryBindings.maths.joml.Vector4f;
import engine.utils.libraryBindings.maths.utils.Vector4;
import engine.utils.libraryBindings.opengl.constants.DataType;
import engine.utils.libraryBindings.opengl.constants.FormatType;
import engine.utils.libraryBindings.opengl.textures.parameters.MagFilterParameter;
import engine.utils.libraryBindings.opengl.textures.parameters.MinFilterParameter;
import engine.utils.libraryBindings.opengl.textures.parameters.WrapParameter;

public class TextureConfigs {

    public final FormatType internalFormat;
    public final FormatType format;
    public final DataType dataType;

    public final Vector4f borderColour = Vector4.create();
    public MinFilterParameter minFilter;
    public MagFilterParameter magFilter;
    public WrapParameter wrapS;
    public WrapParameter wrapT;
    public float levelOfDetailBias;
    public float anisotropicFilter;
    public boolean mipmap = true;

    public TextureConfigs() {
        this(FormatType.RGBA16F, FormatType.RGBA, DataType.U_BYTE);
    }

    public TextureConfigs(FormatType internalFormat, FormatType format, DataType dataType) {
        this.internalFormat = internalFormat;
        this.format = format;
        this.dataType = dataType;
    }

    public TextureConfigs copy() {
        final TextureConfigs configs = new TextureConfigs(internalFormat, format, dataType);
        configs.borderColour.set(this.borderColour);
        configs.levelOfDetailBias = this.levelOfDetailBias;
        configs.anisotropicFilter = this.anisotropicFilter;
        configs.minFilter = this.minFilter;
        configs.magFilter = this.magFilter;
        configs.mipmap = this.mipmap;
        configs.wrapS = this.wrapS;
        configs.wrapT = this.wrapT;
        return configs;
    }
}
