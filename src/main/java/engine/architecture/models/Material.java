package engine.architecture.models;

import engine.fileLoaders.ImageLoader;
import engine.utils.libraryWrappers.maths.joml.Vector4f;
import engine.utils.libraryWrappers.opengl.textures.TextureObject;
import engine.utils.libraryWrappers.opengl.utils.GlUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.nio.IntBuffer;

import static org.lwjgl.assimp.Assimp.*;

public class Material {

    private String name;

    private Vector4f diffuseColor;
    private Vector4f specularColor;
    private Vector4f ambientColor;
    private Vector4f emissiveColor;
    private Vector4f transparentColor;

    private TextureObject diffuseTexture = TextureObject.emptyTexture();
    private boolean useDiffuseTex = false;

    private boolean cullBackface = true;

    public Material(AIMaterial aiMaterial, AIScene scene) {
        PointerBuffer properties = aiMaterial.mProperties(); // array of pointers to AIMaterialProperty structs
        for (int j = 0; j < properties.remaining(); j++) {
            AIMaterialProperty prop = AIMaterialProperty.create(properties.get(j));
            switch (prop.mKey().dataString()) {
                case Assimp.AI_MATKEY_NAME:
                    AIString name = AIString.create();
                    Assimp.aiGetMaterialString(aiMaterial, prop.mKey().dataString(), 0, 0, name);
                    this.name = name.dataString();
                    break;

                case Assimp.AI_MATKEY_COLOR_DIFFUSE:
                    AIColor4D diffuseColor = AIColor4D.create();
                    Assimp.aiGetMaterialColor(aiMaterial, prop.mKey().dataString(), 0, 0, diffuseColor);
                    this.diffuseColor = new Vector4f(diffuseColor.r(), diffuseColor.g(), diffuseColor.b(), diffuseColor.a());
//                System.out.println("Color: " + diffuseColor.r() + diffuseColor.g() + diffuseColor.b() + diffuseColor.a());
                    break;
                case Assimp.AI_MATKEY_COLOR_SPECULAR:
                    AIColor4D specularColor = AIColor4D.create();
                    Assimp.aiGetMaterialColor(aiMaterial, prop.mKey().dataString(), 0, 0, specularColor);
                    this.specularColor = new Vector4f(specularColor.r(), specularColor.g(), specularColor.b(), specularColor.a());
                    break;
                case Assimp.AI_MATKEY_COLOR_AMBIENT:
                    AIColor4D ambientColor = AIColor4D.create();
                    Assimp.aiGetMaterialColor(aiMaterial, prop.mKey().dataString(), 0, 0, ambientColor);
                    this.ambientColor = new Vector4f(ambientColor.r(), ambientColor.g(), ambientColor.b(), ambientColor.a());
                    break;
                case Assimp.AI_MATKEY_COLOR_EMISSIVE:
                    AIColor4D emissiveColor = AIColor4D.create();
                    Assimp.aiGetMaterialColor(aiMaterial, prop.mKey().dataString(), 0, 0, emissiveColor);
                    this.emissiveColor = new Vector4f(emissiveColor.r(), emissiveColor.g(), emissiveColor.b(), emissiveColor.a());
                    break;
                case Assimp.AI_MATKEY_COLOR_TRANSPARENT:
                    AIColor4D transparentColor = AIColor4D.create();
                    Assimp.aiGetMaterialColor(aiMaterial, prop.mKey().dataString(), 0, 0, transparentColor);
                    this.transparentColor = new Vector4f(transparentColor.r(), transparentColor.g(), transparentColor.b(), transparentColor.a());
                    break;


                case Assimp.AI_MATKEY_ENABLE_WIREFRAME:
                    //Specifies whether wireframe rendering must be turned on for the material. 0 for false, !0 for true. st f
//                    System.out.println("Wireframe 0f 1t: " + prop.mData().get());
                    break;
                case Assimp.AI_MATKEY_TWOSIDED:
                    //Specifies whether meshes using this material must be rendered without backface culling. 0 for false, !0 for true.
                    cullBackface = prop.mData().get() == AI_FALSE;
                    break;
                case Assimp.AI_MATKEY_OPACITY: {
                    //Defines the opacity of the material in a range between 0..1.
                    //Use this value to decide whether you have to activate alpha blending for rendering.
                    // OPACITY != 1 usually also implies TWOSIDED=1 to avoid cull artifacts.
                    break;
                }
                case Assimp.AI_MATKEY_SHININESS:
                    //Defines the shininess of a phong-shaded material. This is actually the exponent of the phong specular equation
                    break;
                case Assimp.AI_MATKEY_SHININESS_STRENGTH:
                    //Scales the specular color of the material.
                    break;
                case aiAI_MATKEY_GLTF_PBRMETALLICROUGHNESS_METALLIC_FACTOR:
                    System.out.println("Metallic Factor: " + prop.mData().get());
                    break;
                case aiAI_MATKEY_GLTF_PBRMETALLICROUGHNESS_ROUGHNESS_FACTOR:
                    System.out.println("Roughness Factor: " + prop.mData().get());
                    break;
            }
        }
        diffuseTexture = getTexture(aiTextureType_DIFFUSE, aiMaterial, scene);
        if (diffuseTexture != null){
            useDiffuseTex = true;
        }else {
            diffuseTexture = TextureObject.emptyTexture();
        }
    }

    public Material(boolean bfc){
        cullBackface = bfc;
    }

    private TextureObject getTexture(int aiTextureType, AIMaterial material, AIScene scene){
        AIString texturename = AIString.calloc();
        Assimp.aiGetMaterialTexture(material, aiTextureType, 0, texturename, (IntBuffer) null, null, null, null, null, null);
        String textPath = texturename.dataString();
        if (textPath.length() > 0 && !textPath.startsWith("*")) {
            try {
                return ImageLoader.loadTexture(textPath, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(textPath.length() > 0){
            PointerBuffer aiTextures = scene.mTextures();
            AITexture embeddedTexture = AITexture.create(aiTextures.get(Integer.parseInt(textPath.replace("*", ""))));
            return ImageLoader.loadTextureFromBuffer(embeddedTexture, false);
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public Vector4f getDiffuseColor() {
        return diffuseColor;
    }

    public Vector4f getSpecularColor() {
        return specularColor;
    }

    public Vector4f getAmbientColor() {
        return ambientColor;
    }

    public Vector4f getEmissiveColor() {
        return emissiveColor;
    }

    public Vector4f getTransparentColor() {
        return transparentColor;
    }

    public TextureObject getDiffuseTexture() {
        return diffuseTexture;
    }

    public boolean hasTexture() {
        return useDiffuseTex;
    }

    void preconfigure() {
        if (cullBackface){
            GlUtils.enableCulling();
        }else {
            GlUtils.disableCulling();
        }
    }

    public void setBackfaceCulling(boolean b) {
        cullBackface = b;
    }
}
