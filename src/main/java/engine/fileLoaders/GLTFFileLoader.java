package engine.fileLoaders;

import engine.architecture.models.Material;
import engine.architecture.models.Mesh;
import engine.architecture.models.SimpleModel;
import engine.utils.IOUtils;
import engine.utils.libraryBindings.maths.joml.Vector3f;
import engine.utils.libraryBindings.maths.objects.Box;
import engine.utils.libraryBindings.opengl.constants.RenderMode;
import engine.utils.libraryBindings.opengl.utils.Utils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.system.MemoryUtil.*;

public class GLTFFileLoader {

    private static HashMap<String, SimpleModel> loadedModels = new HashMap<>();
    private static Vector3f min = new Vector3f();
    private static Vector3f max = new Vector3f();
    private static Vector3f tmp = new Vector3f();

    public static SimpleModel loadGLTF(String objFileName) {
        SimpleModel m = loadedModels.get(objFileName);
        System.out.println("Loading file: " + objFileName);
        if (m != null) {
            return m;
        }
        min = new Vector3f();
        max = new Vector3f();
        tmp = new Vector3f();
        AIFileIO fileIo = AIFileIO.create();
        AIFileOpenProcI fileOpenProc = new AIFileOpenProc() {
            public long invoke(long pFileIO, long fileName, long openMode) {
                AIFile aiFile = AIFile.create();
                final ByteBuffer data;
                String fileNameUtf8 = memUTF8(fileName);
                try {
                    data = IOUtils.ioResourceToByteBuffer(fileNameUtf8, 8192);
                } catch (IOException e) {
                    throw new RuntimeException("Could not open file: " + fileNameUtf8 + " here: " + objFileName);
                }
                AIFileReadProcI fileReadProc = new AIFileReadProc() {
                    public long invoke(long pFile, long pBuffer, long size, long count) {
                        long max = Math.min(data.remaining(), size * count);
                        memCopy(memAddress(data) + data.position(), pBuffer, max);
                        return max;
                    }
                };
                AIFileSeekI fileSeekProc = new AIFileSeek() {
                    public int invoke(long pFile, long offset, int origin) {
                        if (origin == Assimp.aiOrigin_CUR) {
                            data.position(data.position() + (int) offset);
                        } else if (origin == Assimp.aiOrigin_SET) {
                            data.position((int) offset);
                        } else if (origin == Assimp.aiOrigin_END) {
                            data.position(data.limit() + (int) offset);
                        }
                        return 0;
                    }
                };
                AIFileTellProcI fileTellProc = new AIFileTellProc() {
                    public long invoke(long pFile) {
                        return data.limit();
                    }
                };
                aiFile.ReadProc(fileReadProc);
                aiFile.SeekProc(fileSeekProc);
                aiFile.FileSizeProc(fileTellProc);
                return aiFile.address();
            }
        };
        AIFileCloseProcI fileCloseProc = new AIFileCloseProc() {
            public void invoke(long pFileIO, long pFile) {
                /* Nothing to do */
            }
        };
        fileIo.set(fileOpenProc, fileCloseProc, NULL);
        AIScene scene = aiImportFileEx(objFileName.replaceFirst("/", ""),
                aiProcess_PreTransformVertices | aiProcess_GenSmoothNormals | aiProcess_Triangulate | aiProcess_EmbedTextures |
                        aiProcess_JoinIdenticalVertices | aiProcess_ImproveCacheLocality | aiProcess_OptimizeMeshes, fileIo);
        if (scene == null) {
            throw new IllegalStateException(aiGetErrorString());
        }

        int numMaterials = scene.mNumMaterials();
        PointerBuffer aiMaterials = scene.mMaterials();
        ArrayList<Material> materials = new ArrayList<>();
        for (int i = 0; i < numMaterials; i++) {
            AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
            processMaterial(aiMaterial, materials, scene);
        }
        int numMeshes = scene.mMeshes().capacity();
        System.out.println("Loading file with " + numMeshes + " meshes.");
        PointerBuffer aiMeshes = scene.mMeshes();
        Mesh[] meshes = new Mesh[numMeshes];
        for (int i = 0; i < numMeshes; i++) {
            assert aiMeshes != null;
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            Mesh mesh = processMesh(aiMesh, materials);
            meshes[i] = mesh;
        }
        m = new SimpleModel(meshes, RenderMode.TRIANGLES, Box.of(min, max));
        loadedModels.put(objFileName, m);
        scene.free();
        return m;
    }

    private static void processMaterial(AIMaterial aiMaterial, ArrayList<Material> materials, AIScene s) {
        Material material = new Material(aiMaterial, s);
        materials.add(material);
    }

    private static Mesh processMesh(AIMesh aiMesh, ArrayList<Material> materials) {
        ArrayList<Float> vertices = new ArrayList<>();
        ArrayList<Float> textures = new ArrayList<>();
        ArrayList<Float> normals = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();
        processVertices(aiMesh, vertices);
        processTextCoords(aiMesh, textures);
        processIndices(aiMesh, indices);
        Mesh mesh;
        if (aiMesh.mNormals() != null) {
            processNormals(aiMesh, normals);
            mesh = new Mesh(Utils.listToArray(vertices), Utils.listToArray(textures), Utils.listToArray(normals), Utils.listIntToArray(indices));
        } else {
            mesh = new Mesh(Utils.listToArray(vertices), Utils.listToArray(textures), Utils.listIntToArray(indices));
        }
        Material material;
        int materialIdx = aiMesh.mMaterialIndex();
        if (materialIdx >= 0 && materialIdx < materials.size()) {
            material = materials.get(materialIdx);
        } else {
            material = null;
        }
        mesh.setMaterial(material);
        return mesh;
    }

    private static void processIndices(AIMesh aiMesh, ArrayList<Integer> indices) {
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        while (aiFaces.remaining() > 0) {
            AIFace aiFace = aiFaces.get();
            for (int i = 0; i < aiFace.mIndices().capacity(); i++) {
                indices.add(aiFace.mIndices().get(i));
            }
        }
    }


    private static void processTextCoords(AIMesh aiMesh, ArrayList<Float> textures) {
        AIVector3D.Buffer aiTextCoords = aiMesh.mTextureCoords(0);

        if (aiTextCoords == null) {
            return;
        }
        while (aiTextCoords.remaining() > 0) {
            AIVector3D aiNormal = aiTextCoords.get();
            textures.add(aiNormal.x());
            textures.add(aiNormal.y());
//            textures.add(aiNormal.z());
        }
    }

    private static void processNormals(AIMesh aiMesh, ArrayList<Float> normals) {
        AIVector3D.Buffer aiNormals = aiMesh.mNormals();
        assert aiNormals != null;
        while (aiNormals.remaining() > 0) {
            AIVector3D aiNormal = aiNormals.get();
            normals.add(aiNormal.x());
            normals.add(aiNormal.y());
            normals.add(aiNormal.z());
        }
    }

    private static void processVertices(AIMesh aiMesh, ArrayList<Float> vertices) {
        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        while (aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();
            vertices.add(aiVertex.x());
            vertices.add(aiVertex.y());
            vertices.add(aiVertex.z());
            tmp = new Vector3f(aiVertex.x(), aiVertex.y(), aiVertex.z());
            max.max(tmp);
            min.min(tmp);
        }
    }
}