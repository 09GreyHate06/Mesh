package utils;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.TextureData;
import glCore.renderer.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.w3c.dom.Text;
import scene.Scene;
import org.lwjgl.assimp.*;
import org.lwjgl.PointerBuffer;
import scene.components.renderering.MaterialComponent;
import scene.components.renderering.MeshComponent;
import scene.components.renderering.MeshRendererComponent;
import scene.components.renderering.Primitive;

import java.io.*;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;




public class ModelLoader {
    private static HashMap<String, Texture2D> _currentTextureMaps = new HashMap<>();
    private static String _directory;

    public static Entity loadModel(String filename, Scene scene){
        int dirLastIndex = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
        _directory = filename.substring(0, Math.max(dirLastIndex, 0));

        AIScene aiScene = Assimp.aiImportFile(filename, Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate  |
                Assimp.aiProcess_GenNormals | Assimp.aiProcess_CalcTangentSpace);

        if(aiScene == null || ((aiScene.mFlags() & Assimp.AI_SCENE_FLAGS_INCOMPLETE) == 1) || aiScene.mRootNode() == null){
            throw new RuntimeException("Failed to load model: " + filename + "\n[Description] " + Assimp.aiGetErrorString());
        }

        Entity entity = processNode(aiScene.mRootNode(), aiScene, null, scene);

        _currentTextureMaps.clear();
        _directory = "";

        return entity;
    }

    private static Entity processNode(AINode node, final AIScene aiScene, Entity parent, Scene scene){
        Entity nodeEntity = scene.createEntity(node.mName().dataString(), parent);

        for(int i = 0; i < node.mNumMeshes(); i++){
            AIMesh mesh = AIMesh.create(aiScene.mMeshes().get(node.mMeshes().get(i)));
            Entity meshEntity = scene.createEntity(mesh.mName().dataString(), nodeEntity);
            processMesh(mesh, aiScene, meshEntity.addAndReturn(new MeshComponent()), meshEntity.addAndReturn(new MaterialComponent()));
            var mr = meshEntity.addAndReturn(new MeshRendererComponent());
            mr.primitive = Primitive.Triangles;
            mr.receiveLight = true;
        }
        for(int i = 0; i < node.mNumChildren(); i++){
            AINode childNode = AINode.create(node.mChildren().get(i));
            processNode(childNode, aiScene, nodeEntity, scene);
        }

        if(node == aiScene.mRootNode())
            return nodeEntity;
        else
            return null;
    }

    private static void processMesh(AIMesh aiMesh, final AIScene aiScene, MeshComponent meshComponent, MaterialComponent matComponent)  {

/*        class Vertex implements Serializable{
            public Vector3f position = new Vector3f();
            public Vector2f texCoord = new Vector2f();
            public Vector3f normal = new Vector3f();
            public Vector3f tangent = new Vector3f();
            public Vector3f bitangent = new Vector3f();
        }*/

        int offset = 14;
        int posOffset = 0;
        int texCoordOffset = 3;
        int normalOffset = 5;
        int tangentOffset = 8;
        int bitangentOffset = 11;

        float[] vertices = new float[aiMesh.mNumVertices() * offset];
        List<Integer> indices = new ArrayList<>();

        for(int i = 0; i < aiMesh.mNumVertices(); i++){

            AIVector3D pos = aiMesh.mVertices().get(i);

            // position
            vertices[i * offset + posOffset] = pos.x();
            vertices[i * offset + posOffset + 1] = pos.y();
            vertices[i * offset + posOffset + 2] = pos.z();


            // texCoord
            AIVector3D texCoord = null;
            if(aiMesh.mTextureCoords(0) != null) {
                texCoord = aiMesh.mTextureCoords(0).get(i);
                vertices[i * offset + texCoordOffset] = texCoord.x();
                vertices[i * offset + texCoordOffset + 1] = texCoord.y();
            }


            // normals
            AIVector3D norm = aiMesh.mNormals().get(i);
            vertices[i * offset + normalOffset] = norm.x();
            vertices[i * offset + normalOffset + 1] = norm.y();
            vertices[i * offset + normalOffset + 2] = norm.z();


            if(aiMesh.mTangents() != null && aiMesh.mBitangents() != null){
                AIVector3D tangent = aiMesh.mTangents().get(i);
                AIVector3D bitangent = aiMesh.mBitangents().get(i);

                // tangent
                vertices[i * offset + tangentOffset] = tangent.x();
                vertices[i * offset + tangentOffset + 1] = tangent.y();
                vertices[i * offset + tangentOffset + 2] = tangent.z();

                // bitangent
                vertices[i * offset + bitangentOffset] = bitangent.x();
                vertices[i * offset + bitangentOffset + 1] = bitangent.y();
                vertices[i * offset + bitangentOffset + 2] = bitangent.z();
            } else {
                // tangent
                vertices[i * offset + tangentOffset] = 1.0f;
                vertices[i * offset + tangentOffset + 1] = 0.0f;
                vertices[i * offset + tangentOffset + 2] = 0.0f;

                // bitangent
                vertices[i * offset + bitangentOffset] = 0.0f;
                vertices[i * offset + bitangentOffset + 1] = 1.0f;
                vertices[i * offset + bitangentOffset + 2] = 0.0f;
            }
        }

        for(int i = 0; i < aiMesh.mNumFaces(); i++){
            AIFace face = AIFace.create(aiMesh.mFaces().get(i).address());
            for(int j = 0; j < face.mNumIndices(); j++){
                indices.add(face.mIndices().get(j));
            }
        }

        Texture2D diffuseMap = null;
        Texture2D specularMap = null;
        Texture2D normalMap = null;

        Vector3f color = new Vector3f(1.0f);
        float[] shininess = new float[]{ 32.0f };

        if(aiMesh.mMaterialIndex() >= 0){
            AIMaterial mat = AIMaterial.create(aiScene.mMaterials().get(aiMesh.mMaterialIndex()));
            diffuseMap = loadTextureMap(mat, Assimp.aiTextureType_DIFFUSE);
            specularMap = loadTextureMap(mat, Assimp.aiTextureType_SPECULAR);
            normalMap = loadTextureMap(mat, Assimp.aiTextureType_HEIGHT);
            if(normalMap == null)
                normalMap = loadTextureMap(mat, Assimp.aiTextureType_NORMALS);

            color = loadSolidColor(mat, Assimp.AI_MATKEY_COLOR_DIFFUSE);
            int[] max = new int[]{ 1 };
            if(Assimp.aiGetMaterialFloatArray(mat, Assimp.AI_MATKEY_SHININESS, 0, 0, shininess, max)
                    != Assimp.aiReturn_SUCCESS){
                shininess[0] = 32.0f;
            }
        }

        if(specularMap == null && diffuseMap != null)
            specularMap = diffuseMap.addRef(Texture2D.class);

        matComponent.diffuseMap = diffuseMap;
        matComponent.specularMap = specularMap;
        matComponent.normalMap = normalMap;
        matComponent.color = new Vector4f(color, 1.0f);
        matComponent.tiling = new Vector2f(1.0f, 1.0f);
        matComponent.shininess = shininess[0];

        VertexBufferLayout vbLayout = new VertexBufferLayout();
        vbLayout.pushElements(0, 3, false);
        vbLayout.pushElements(1, 2, false);
        vbLayout.pushElements(2, 3, false);
        vbLayout.pushElements(3, 3, false);
        vbLayout.pushElements(4, 3, false);

        VertexBuffer vbo = new VertexBuffer(vertices, vbLayout);
        VertexArray vao = new VertexArray();
        vao.bind();

        int[] indicesPrim = new int[indices.size()];
        for(int i = 0; i < indices.size(); i++)
            indicesPrim[i] = indices.get(i);
        IndexBuffer ibo = new IndexBuffer(indicesPrim);

        vao.addVertexBuffer(vbo);
        vao.addIndexBuffer(ibo);

        meshComponent.vao = vao;
    }

    private static Texture2D loadTextureMap(AIMaterial mat, int type){
        AIString path = AIString.calloc();
        Assimp.aiGetMaterialTexture(mat, type, 0, path, null, null, null, null, null, (int[]) null);
        String texPath = path.dataString();
        if(texPath == null || texPath.isEmpty()) return null;
        String texDirectory = _directory + "/" + texPath;
        if(_currentTextureMaps.containsKey(texDirectory)){
            return _currentTextureMaps.get(texDirectory).addRef(Texture2D.class);
        }

        Texture2D tex = new Texture2D(texDirectory, true);
        _currentTextureMaps.put(texDirectory, tex);
        return tex;
    }

    private static Vector3f loadSolidColor(AIMaterial mat, String matKey){
        AIColor4D color = AIColor4D.create();
        if(Assimp.aiGetMaterialColor(mat, matKey, 0, 0, color) == Assimp.aiReturn_SUCCESS)
            return new Vector3f(color.r(), color.g(), color.b());
        return new Vector3f(1.0f);
    }
}