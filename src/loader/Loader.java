package loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import util.Vector2f;
import util.Vector3f;

public class Loader
{

    private static HashMap<String, Integer> textureIdMap = new HashMap<>();

    /**
     * Loads .obj files <br>
     * 
     * - One of the problems with this method is that it does not save on memory.
     * in exchange for the extra memory the load time is increased. (from linear
     * time to constant time) <br>
     * 
     * - how do we incorporate different materials for different faces?
     * 
     *
     * @param filename name of the file to load. (.obj file format)
     * @param textureFileName texture file name.
     * @return returns the RawData from the .obj file
     */
    public static RawData loadRawData(String filename, String textureFileName)
    {
        List<Material> materials = new ArrayList<>();
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
        List<String> indices = new ArrayList<>();

        try
        {
            //loop trough all coordinates and add them to the right list
            String line = br.readLine();
            while (line != null)
            {
                String[] currentLine = line.replaceAll("\\s+", " ").split(" ");
                if (line.startsWith("matlib"))
                {
                    // if the line starts with 'matlib' it designates a material file
                    // that need to be loaded. one .mtl file can contain many materials.
                    materials.addAll(Material.loadMtlFile(currentLine[1]));
                } else if (line.startsWith("v "))
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
                    String[] face = line.split(" ");
                    indices.add(face[1]);
                    indices.add(face[2]);
                    indices.add(face[3]);
                } else if (line.startsWith("usemtl"))
                {
                    //what happens here?
                    
                }

                line = br.readLine();
            }

            float[] verticesArray = new float[indices.size() * 3];
            float[] textureArray = new float[indices.size() * 2];
            float[] normalsArray = new float[indices.size() * 3];
            int[] indicesArray = new int[indices.size()];

            //loop through indices
            for (int i = 0; i < indices.size(); i++)
            {
                String[] vertex = indices.get(i).split("/");

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
                indicesArray[i] = i;
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
