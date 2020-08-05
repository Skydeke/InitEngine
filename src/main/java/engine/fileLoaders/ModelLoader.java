package engine.fileLoaders;

import engine.architecture.models.Model;
import engine.architecture.models.ModelGenerator;

public final class ModelLoader {

    private final static String path = "/models/primitives/";

    public static Model sphere = ModelGenerator.generateSphere();
    public static Model cube = ModelGenerator.generateCube();
    public static Model posquad = ModelGenerator.generateSquare();
    public static Model thickquad;

    static {
        try {
            thickquad = load(path + "quad.obj");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ModelLoader() {
    }

    /**
     * Loads a file to a model (only vertex data, no textures or whatever)
     *
     * @param fileName the vertex data file
     * @return a new model
     */
    public static Model load(String fileName) throws Exception {
        String fileFormat = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (fileFormat) {
            case "obj":
                return loadOBJ(fileName);
            case "fbx":
            case "glb":
            case "gltf":
                return loadGLTF(fileName);
            default:
                throw new Exception("File format " + fileFormat + " is not supported");
        }
    }

    private static Model loadOBJ(String objFile) throws Exception {
        return OBJFileLoader.loadOBJ(objFile);
    }

    private static Model loadGLTF(String fbxFile) throws Exception {
        return GLTFFileLoader.loadGLTF(fbxFile);
    }
}
