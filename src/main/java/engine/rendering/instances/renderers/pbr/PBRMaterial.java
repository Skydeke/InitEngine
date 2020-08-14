package engine.rendering.instances.renderers.pbr;

import engine.architecture.models.Material;
import engine.utils.libraryBindings.maths.joml.Vector3f;
import engine.utils.libraryBindings.opengl.textures.ITexture;
import engine.utils.libraryBindings.opengl.textures.ImageLoader;
import engine.utils.libraryBindings.opengl.textures.Texture;
import engine.utils.libraryBindings.opengl.textures.parameters.MagFilterParameter;
import engine.utils.libraryBindings.opengl.textures.parameters.MinFilterParameter;
import engine.utils.libraryBindings.opengl.textures.parameters.WrapParameter;

public class PBRMaterial extends Material {

    ITexture roughnessMap = Texture.NONE;
    ITexture metalMap = Texture.NONE;
    float IOR;
    private float metalConst, roughnessConst;
    private Vector3f albedoConst = new Vector3f();
    private boolean is_albedo_map, is_normal_map, is_roughness_map, is_metal_map;

    public PBRMaterial(float albedo_r, float albedo_g, float albedo_b,
                       float roughness, float metal) {
        this(new Vector3f(albedo_r, albedo_g, albedo_b), roughness, metal);
    }

    public PBRMaterial(String texturePath, boolean srgb) {
        this(texturePath, "png", srgb);
    }

    public PBRMaterial(String texturePath, String fileExt, boolean srgb) {

        this(texturePath, "albedo." + fileExt,
                "normal." + fileExt,
                "rough." + fileExt,
                "metal." + fileExt, srgb);
    }

    public PBRMaterial(String texturePath, String albedoFile, String normalFile,
                       String roughnessFile, String metalFile, boolean srgb) {
        super();
        try {
            Texture albedo = ImageLoader.loadTexture(texturePath + albedoFile, srgb);
            albedo.getFunctions().magFilter(MagFilterParameter.LINEAR);
            albedo.getFunctions().minFilter(MinFilterParameter.LINEAR_MIPMAP_LINEAR);
            albedo.getFunctions().wrapS(WrapParameter.REPEAT);
            albedo.getFunctions().wrapT(WrapParameter.REPEAT);
            setAlbedoMap(albedo);

            Texture normal = ImageLoader.loadTexture(texturePath + normalFile, srgb);
            albedo.getFunctions().magFilter(MagFilterParameter.LINEAR);
            albedo.getFunctions().minFilter(MinFilterParameter.LINEAR_MIPMAP_LINEAR);
            albedo.getFunctions().wrapS(WrapParameter.REPEAT);
            albedo.getFunctions().wrapT(WrapParameter.REPEAT);
            setNormalMap(normal);

            Texture roughness = ImageLoader.loadTexture(texturePath + roughnessFile, srgb);
            albedo.getFunctions().magFilter(MagFilterParameter.LINEAR);
            albedo.getFunctions().minFilter(MinFilterParameter.LINEAR_MIPMAP_LINEAR);
            albedo.getFunctions().wrapS(WrapParameter.REPEAT);
            albedo.getFunctions().wrapT(WrapParameter.REPEAT);
            setRoughnessMap(roughness);

            Texture metal = ImageLoader.loadTexture(texturePath + metalFile, srgb);
            albedo.getFunctions().magFilter(MagFilterParameter.LINEAR);
            albedo.getFunctions().minFilter(MinFilterParameter.LINEAR_MIPMAP_LINEAR);
            albedo.getFunctions().wrapS(WrapParameter.REPEAT);
            albedo.getFunctions().wrapT(WrapParameter.REPEAT);
            setMetalMap(metal);

            useAllMaps(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public PBRMaterial(Vector3f albedo, float roughness, float metal) {
        this.albedoConst = albedo;
        this.roughnessConst = roughness;
        this.metalConst = metal;
        useAllMaps(false);
    }

    public void useAllMaps(Boolean use) {
        this.is_metal_map = use;
        this.is_roughness_map = use;
        this.is_albedo_map = use;
        this.is_normal_map = use;
    }

    public Boolean isAlbedoMapped() {
        return is_albedo_map && diffuseTexture != null;
    }

    public Boolean isNormalMapped() {
        return is_normal_map && normalTexture != null;
    }

    public Boolean isRoughnessMapped() {
        return is_roughness_map && roughnessMap != null;
    }

    public Boolean isMetalMapped() {
        return is_metal_map && metalMap != null;
    }

    public void useAlbedoMap(Boolean use) {
        this.is_albedo_map = use;
    }

    public void useNormalMap(Boolean use) {
        this.is_normal_map = use;
    }

    public void useRoughnessMap(Boolean use) {
        this.is_roughness_map = use;
    }

    public void useMetalMap(Boolean use) {
        this.is_metal_map = use;
    }


    public ITexture getAlbedoMap() {
        return diffuseTexture;
    }

    public void setAlbedoMap(ITexture albedoMap) {
        this.diffuseTexture = albedoMap;
    }

    public ITexture getNormalMap() {
        return normalTexture;
    }

    public void setNormalMap(ITexture normalMap) {
        this.normalTexture = normalMap;
    }

    public ITexture getRoughnessMap() {
        return roughnessMap;
    }

    public void setRoughnessMap(ITexture roughnessMap) {
        this.roughnessMap = roughnessMap;
    }

    public ITexture getMetalMap() {
        return metalMap;
    }

    public void setMetalMap(ITexture metalMap) {
        this.metalMap = metalMap;
    }

    public float getMetalConst() {
        return metalConst;
    }

    public void setMetalConst(float metalConst) {
        this.metalConst = metalConst;
    }

    public float getRoughnessConst() {
        return roughnessConst;
    }

    public void setRoughnessConst(float roughnessConst) {
        this.roughnessConst = roughnessConst;
    }

    public Vector3f getAlbedoConst() {
        return albedoConst;
    }

    public void setAlbedoConst(Vector3f albedoConst) {
        this.albedoConst = albedoConst;
    }

    public float getIOR() {
        return IOR;
    }

    public void setIOR(float IOR) {
        this.IOR = IOR;
    }
}
