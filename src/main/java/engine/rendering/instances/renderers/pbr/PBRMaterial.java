package engine.rendering.instances.renderers.pbr;

import engine.fileLoaders.ImageLoader;
import engine.utils.libraryWrappers.maths.joml.Vector3f;
import engine.utils.libraryWrappers.opengl.textures.TextureObject;

public class PBRMaterial {

    TextureObject albedoMap = TextureObject.emptyTexture();
    TextureObject normalMap = TextureObject.emptyTexture();
    TextureObject roughnessMap = TextureObject.emptyTexture();
    TextureObject metalMap = TextureObject.emptyTexture();
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
            TextureObject albedo = ImageLoader.loadTexture(
                    texturePath + albedoFile, srgb)
                    .trilinearFilter().wrap();
            setAlbedoMap(albedo);

            TextureObject normal = ImageLoader.loadTexture(
                    texturePath + normalFile, srgb)
                    .trilinearFilter().wrap();
            setNormalMap(normal);

            TextureObject roughness = ImageLoader.loadTexture(
                    texturePath + roughnessFile, srgb)
                    .trilinearFilter().wrap();
            setRoughnessMap(roughness);

            TextureObject metal = ImageLoader.loadTexture(
                    texturePath + metalFile, srgb)
                    .trilinearFilter().wrap();
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

    public PBRMaterial() {
        this(new Vector3f(1, 1, 1), 0f, 0f);
    }

    public void useAllMaps(Boolean use) {
        this.is_metal_map = use;
        this.is_roughness_map = use;
        this.is_albedo_map = use;
        this.is_normal_map = use;
    }

    public Boolean isAlbedoMapped() {
        return is_albedo_map && albedoMap != null;
    }

    public Boolean isNormalMapped() {
        return is_normal_map && normalMap != null;
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


    public TextureObject getAlbedoMap() {
        return albedoMap;
    }

    public void setAlbedoMap(TextureObject albedoMap) {
        this.albedoMap = albedoMap;
    }

    public TextureObject getNormalMap() {
        return normalMap;
    }

    public void setNormalMap(TextureObject normalMap) {
        this.normalMap = normalMap;
    }

    public TextureObject getRoughnessMap() {
        return roughnessMap;
    }

    public void setRoughnessMap(TextureObject roughnessMap) {
        this.roughnessMap = roughnessMap;
    }

    public TextureObject getMetalMap() {
        return metalMap;
    }

    public void setMetalMap(TextureObject metalMap) {
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
