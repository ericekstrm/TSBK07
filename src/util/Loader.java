package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

public class Loader
{

    private static HashMap<String, Integer> textureIdMap = new HashMap<>();

    public static RawData loadRawData(String filename, String textureFileName)
    {
        //load .obj file
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader("res/" + filename));
            System.out.println("Loading file: " + filename);
        } catch (FileNotFoundException ex)
        {
            System.out.println("file not found: " + filename);
            return null;
        }

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        List<String> indexIdentifiers = new ArrayList<>();

        try
        {
            //loop trough all coordinates and add them to the right list
            String line;
            while (true)
            {
                line = br.readLine().replaceAll("\\s+", " ");

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

            //loops until the end of the file.
            while (line != null)
            {
                if (line.startsWith("f"))
                {
                    String[] face = line.split(" ");

                    for (int i = 1; i < face.length; i++)
                    {
                        //if that specific vertex already exists, find the index of it and add that to the list of indices.
                        //otherwise add the vertex to the list of vertices and again add the index to the list of indices.
                        if (!indexIdentifiers.contains(face[i]))
                        {
                            indexIdentifiers.add(face[i]);
                        }
                        indices.add(indexIdentifiers.indexOf(face[i]));
                    }
                }

                //reads new line and removes extra whitespace
                line = br.readLine();
            }

            float[] verticesArray = new float[indexIdentifiers.size() * 3];
            float[] textureArray = new float[indexIdentifiers.size() * 2];
            float[] normalsArray = new float[indexIdentifiers.size() * 3];
            int[] indicesArray = new int[indices.size()];

            //loop through all unit indexIdentifiers
            for (int i = 0; i < indexIdentifiers.size(); i++)
            {
                String[] vertex = indexIdentifiers.get(i).split("/");

                Vector3f v = vertices.get(Integer.parseInt(vertex[0]) - 1);
                verticesArray[i * 3] = v.x;
                verticesArray[i * 3 + 1] = v.y;
                verticesArray[i * 3 + 2] = v.z;

                Vector2f t = new Vector2f(0, 0);
                if (!vertex[1].equals(""))
                {
                    t = textures.get(Integer.parseInt(vertex[1]) - 1);
                }
                textureArray[i * 2] = t.x;
                textureArray[i * 2 + 1] = t.y;

                Vector3f n = normals.get(Integer.parseInt(vertex[2]) - 1);
                normalsArray[i * 3] = n.x;
                normalsArray[i * 3 + 1] = n.y;
                normalsArray[i * 3 + 2] = n.z;
            }

            for (int i = 0; i < indicesArray.length; i++)
            {
                indicesArray[i] = indices.get(i);
            }

            RawData data = new RawData(verticesArray, textureArray, indicesArray, normalsArray, loadTexture(textureFileName));
            return data;

        } catch (IOException | NumberFormatException e)
        {
            e.printStackTrace();
            return null;
        }

    }

    public static int loadTexture(String texture)
    {
        if (texture.equals(""))
        {
            return 0;
        }
        if (textureIdMap.containsKey(texture))
        {
            return textureIdMap.get(texture);
        }

        int width;
        int height;
        ByteBuffer buffer;
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            File file = new File("res/textures/" + texture);
            String filePath = file.getAbsolutePath();
            buffer = STBImage.stbi_load(filePath, w, h, channels, 4);
            if (buffer == null)
            {
                throw new Exception("Can't load file " + texture + " " + STBImage.stbi_failure_reason());
            }
            width = w.get();
            height = h.get();

            int id = GL11.glGenTextures();
            textureIdMap.put(texture, id);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0,
                              GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            STBImage.stbi_image_free(buffer);
            return id;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }
}
