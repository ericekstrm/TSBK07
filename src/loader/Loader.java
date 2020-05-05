package loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Model;
import util.Vector2f;
import util.Vector3f;

public class Loader
{

    public static RawData[] loadObj(String filename)
    {
        String folder = "";
        //load .obj file from disk
        BufferedReader br = null;
        try
        {
            if (filename.contains("."))
            {
                br = new BufferedReader(new FileReader("res/objects/" + filename));
                System.out.println("Loading object: " + filename);
            } else
            {
                //if no dot is present, it is a folder instead
                folder = filename + "/";
                br = new BufferedReader(new FileReader("res/objects/" + folder + filename + ".obj"));
                System.out.println("Loading object: " + filename);
            }

        } catch (FileNotFoundException ex)
        {
            System.out.println("file not found: " + filename);
            return null;
        }

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();

        List<String> indices = new ArrayList<>();
        Map<String, Material> materials = new HashMap<>();
        List<String> loadedMaterialFiles = new ArrayList<>();

        float maxHeight = 0;

        List<RawData> rawdatas = new ArrayList<>();

        try
        {
            //loop trough all coordinates and add them to the right list
            String line = br.readLine();
            String currentMaterialName = "";
            while (line != null)
            {
                String[] currentLine = line.replaceAll("\\s+", " ").split(" ");
                if (line.startsWith("mtllib"))
                {
                    if (!loadedMaterialFiles.contains(currentLine[1]))
                    {
                        // if the line starts with 'matlib' it designates a material file
                        // that need to be loaded. One .mtl file can contain many materials.
                        materials.putAll(Material.loadMtlFile("objects/" + folder, currentLine[1]));
                        loadedMaterialFiles.add(currentLine[1]);
                    }
                } else if (line.startsWith("v "))
                {
                    float height = Float.parseFloat(currentLine[2]);
                    if (height > maxHeight)
                    {
                        maxHeight = height;
                    }
                    Vector3f vertex = new Vector3f(
                            Float.parseFloat(currentLine[1]),
                            height,
                            Float.parseFloat(currentLine[3]));
                    vertices.add(vertex);
                } else if (line.startsWith("vt "))
                {
                    Vector2f texture = new Vector2f(
                            Float.parseFloat(currentLine[1]),
                            -Float.parseFloat(currentLine[2]));
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
                    if (currentLine.length == 4)
                    {
                        indices.add(currentLine[1]);
                        indices.add(currentLine[2]);
                        indices.add(currentLine[3]);
                    } else if (currentLine.length == 5)
                    {
                        indices.add(currentLine[1]);
                        indices.add(currentLine[2]);
                        indices.add(currentLine[3]);
                        indices.add(currentLine[1]);
                        indices.add(currentLine[3]);
                        indices.add(currentLine[4]);
                    }

                } else if (line.startsWith("usemtl"))
                {
                    // 'usemtl' indicates a shift to another material, which needs to be
                    // a new RawData. To do that we save away the indices along with the material name.
                    if (!currentMaterialName.equals(""))
                    {
                        if (!indices.isEmpty())
                        {
                            Material currentMaterial = materials.get(currentMaterialName);
                            if (currentMaterial == null)
                            {
                                System.out.println("no material named: " + currentMaterialName);
                            }
                            RawData data = packageData(vertices, textures, normals, indices, currentMaterial);
                            data.maxHeight = maxHeight;
                            rawdatas.add(data);
                            indices.clear();
                        }
                    }
                    currentMaterialName = currentLine[1];
                }
                line = br.readLine();
            }
            RawData data = packageData(vertices, textures, normals, indices, materials.get(currentMaterialName));
            data.maxHeight = maxHeight;
            rawdatas.add(data);

            return rawdatas.toArray(new RawData[0]);

        } catch (IOException | NumberFormatException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private static RawData packageData(List<Vector3f> vertices, List<Vector2f> textures,
                                       List<Vector3f> normals, List<String> indices,
                                       Material material)
    {

        float[] verticesArray = new float[indices.size() * 3];
        float[] textureArray = new float[indices.size() * 2];
        float[] normalsArray = new float[indices.size() * 3];
        int[] indicesArray = new int[indices.size()];

        //loop through indices
        int i = 0;
        for (String str : indices)
        {
            String[] vertex = str.split("/");

            int index = Integer.parseInt(vertex[0]);
            if (index < 0)
            {
                index = vertices.size() + index;
            } else
            {
                index--;
            }
            Vector3f v = vertices.get(index);
            verticesArray[i * 3] = v.x;
            verticesArray[i * 3 + 1] = v.y;
            verticesArray[i * 3 + 2] = v.z;

            Vector2f t = new Vector2f(0, 0);
            if (!vertex[1].equals(""))
            {
                index = Integer.parseInt(vertex[1]);
                if (index < 0)
                {
                    index = textures.size() + index;
                } else
                {
                    index--;
                }
                t = textures.get(index);
            }
            textureArray[i * 2] = t.x;
            textureArray[i * 2 + 1] = t.y;

            index = Integer.parseInt(vertex[2]);
            if (index < 0)
            {
                index = normals.size() + index;
            } else
            {
                index--;
            }
            Vector3f n = normals.get(index);
            normalsArray[i * 3] = n.x;
            normalsArray[i * 3 + 1] = n.y;
            normalsArray[i * 3 + 2] = n.z;
            i++;
        }

        for (int ix = 0; ix < indices.size(); ix++)
        {
            indicesArray[ix] = ix;
        }

        RawData data = new RawData(verticesArray, normalsArray, textureArray, indicesArray, material);
        return data;
    }

    public static List<Model> loadAllObjects()
    {
        File file = new File("res/objects/");
        String[] directories = file.list(new FilenameFilter()
        {
            @Override
            public boolean accept(File current, String name)
            {
                return new File(current, name).isDirectory();
            }
        });
        
        List<Model> models = new ArrayList<>();
        
        for (String dir : directories)
        {
            if (dir.startsWith("_") || dir.equals("skybox") || dir.equals("sun") || dir.equals("water"))
            {
                continue;
            }
            Model m = new Model(dir);
            models.add(m);
        }
        return models;
    }

    /**
     * @param x - x position of the quad in normalized coordinates [1, -1]
     * @param y - y position of the quad in normalized coordinates [1, -1]
     * @param width - width of the quad in normalized coordinates [1, -1]
     * @param height - height of the quad in normalized coordinates [1, -1]
     * @param xTex - x position of the texture coordinate.
     * @param yTex - y position of the texture coordinate.
     * @param maxXTex - width of the texture coordinate.
     * @param maxYTex - height of the texture coordinate.
     * @return RawModel of the quad. <b>does not have normals or material!!</b>
     */
    public static RawData loadQuad(float x, float y, float width, float height,
                                   float xTex, float yTex, float maxXTex, float maxYTex)
    {
        float[] vertices = new float[]
        {
            x, y, 0,
            x + width, y, 0,
            x, y + height, 0,
            x + width, y + height, 0
        };
        float[] textures = new float[]
        {
            xTex, maxYTex,
            maxXTex, maxYTex,
            xTex, yTex,
            maxXTex, yTex
        };

        //add data that is specific to that vao
        int[] indices = new int[]
        {
            0, 1, 2, 1, 2, 3
        };

        return new RawData(vertices, null, textures, indices, null);
    }

    /**
     * @param x - x position of the quad in normalized coordinates [1, -1]
     * @param y - y position of the quad in normalized coordinates [1, -1]
     * @param width - width of the quad in normalized coordinates [1, -1]
     * @param height - height of the quad in normalized coordinates [1, -1]
     * @return RawModel of the quad. <b>does not have normals or material!!</b>
     */
    public static RawData loadQuad(float x, float y, float width, float height)
    {
        return loadQuad(x, y, width, height, 0, 0, 1, 1);
    }
}
