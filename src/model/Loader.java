package model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import util.Vector2f;
import util.Vector3f;

public class Loader
{

    public static SimpleModel loadObjModel(String filename)
    {
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException ex)
        {
            System.out.println("file not found");
        }

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        float[] verticesArray;
        float[] textureArray = {};
        float[] normalsArray;
        int[] indicesArray;

        try
        {
            String line;
            while (true)
            {
                line = br.readLine();
                String[] currentLine = line.split(" ");
                if (line.startsWith("v "))
                {
                    Vector3f vertex = new Vector3f(
                            Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]),
                            Float.parseFloat(currentLine[3]));
                    vertices.add(vertex);
                } else if (line.startsWith("vt "))
                {
                    Vector2f texture = new Vector2f(
                            Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]));
                    textures.add(texture);
                } else if (line.startsWith("vn "))
                {
                    Vector3f normal = new Vector3f(
                            Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]),
                            Float.parseFloat(currentLine[3]));
                    normals.add(normal);
                } else if (line.startsWith("f "))
                {
                    break;
                }
            }

            textureArray = new float[vertices.size() * 2];
            normalsArray = new float[normals.size() * 3];

            while (line != null)
            {
                if (!line.startsWith("f "))
                {
                    line = br.readLine();
                    continue;
                }
                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");

                processVertex(vertex1, indices, textures, normals, textureArray, normalsArray);
                processVertex(vertex2, indices, textures, normals, textureArray, normalsArray);
                processVertex(vertex3, indices, textures, normals, textureArray, normalsArray);
                line = br.readLine();
            }
            br.close();

        } catch (Exception e)
        {
        }

        verticesArray = new float[vertices.size() * 3];
        indicesArray = new int[vertices.size()];

        int vertexPointer = 0;
        for (Vector3f vertex : vertices)
        {
            verticesArray[vertexPointer++] = vertex.x;
            verticesArray[vertexPointer++] = vertex.y;
            verticesArray[vertexPointer++] = vertex.z;
        }

        for (int i = 0; i < indices.size(); i++)
        {
            indicesArray[i] = indices.get(i);
        }

        SimpleModel model = new SimpleModel();
        model.loadVertexVBO(verticesArray);
        model.loadTextureVBO(textureArray);
        model.loadIndicesVBO(indicesArray);
        //model.loadNormalVBO(normalsArray);

        return model;

    }

    private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector2f> textures, List<Vector3f> normals, float[] textureArray, float[] normalsArray)
    {
        int currentvertexPointer = Integer.parseInt(vertexData[0]) - 1;
        indices.add(currentvertexPointer);

        Vector2f currentTex = textures.get(Integer.parseInt(vertexData[1]) - 1);
        textureArray[currentvertexPointer * 2] = currentTex.x;
        textureArray[currentvertexPointer * 2 + 1] = 1 - currentTex.y;

        Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2]) - 1);
        normalsArray[currentvertexPointer * 3] = currentNorm.x;
        normalsArray[currentvertexPointer * 3 + 1] = currentNorm.y;
        normalsArray[currentvertexPointer * 3 + 2] = currentNorm.z;

    }
}
