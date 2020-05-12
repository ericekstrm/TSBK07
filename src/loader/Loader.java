package loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Model;
import util.Vector2f;
import util.Vector3f;

public class Loader
{

    private static final Map<String, RawData[]> loadedModelDatas = new HashMap<>();

    public static RawData[] loadObj(String filename)
    {
        if (loadedModelDatas.containsKey(filename))
        {
            return loadedModelDatas.get(filename);
        }

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
        float maxRadius = 0;

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
                    float x = Float.parseFloat(currentLine[1]);
                    float y = Float.parseFloat(currentLine[2]);
                    float z = Float.parseFloat(currentLine[3]);

                    if (y > maxRadius)
                    {
                        maxRadius = y;
                    }

                    float radius = (float) Math.sqrt(x * x + y * y + z * z);
                    if (radius > maxHeight)
                    {
                        maxHeight = radius;
                    }

                    Vector3f vertex = new Vector3f(x, y, z);
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
            data.maxRadius = maxRadius;
            rawdatas.add(data);

            loadedModelDatas.put(filename, rawdatas.toArray(new RawData[0]));
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
        Map<String, Vector3f> tangents = new HashMap<>();

        float[] verticesArray = new float[indices.size() * 3];
        float[] textureArray = new float[indices.size() * 2];
        float[] normalsArray = new float[indices.size() * 3];
        float[] tangentsArray = new float[indices.size() * 3];
        int[] indicesArray = new int[indices.size()];

        //loop through indices
        for (int i = 0; i < indices.size(); i++)
        {
            indicesArray[i] = i;

            String[] vertex = indices.get(i).split("/");

            Vector3f v = getCoord(vertex[0], vertices);
            verticesArray[i * 3] = v.x;
            verticesArray[i * 3 + 1] = v.y;
            verticesArray[i * 3 + 2] = v.z;

            Vector2f t = getTextureCoord(vertex[1], textures);
            textureArray[i * 2] = t.x;
            textureArray[i * 2 + 1] = t.y;

            int index = Integer.parseInt(vertex[2]);
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

            //size of 'indices' is aways a product of 3 (since triangles).
            //so we can use that to decide when to calculate tangents (since we do that for each triangle)
            if (i % 3 == 0)
            {
                String[] vertex2 = indices.get(i + 1).split("/");
                String[] vertex3 = indices.get(i + 2).split("/");

                Vector2f t1 = t;
                Vector2f t2 = getTextureCoord(vertex2[1], textures);
                Vector2f t3 = getTextureCoord(vertex3[1], textures);

                float deltaU1 = t2.x - t1.x;
                float deltaU2 = t3.x - t2.x;
                float deltaV1 = t2.y - t1.y;
                float deltaV2 = t3.y - t2.y;

                float a = 1 / (deltaU1 * deltaV2 - deltaU2 * deltaV1);

                Vector3f v1 = v;
                Vector3f v2 = getCoord(vertex2[0], vertices);
                Vector3f v3 = getCoord(vertex3[0], vertices);

                Vector3f E1 = v2.subtract(v1);
                Vector3f E2 = v3.subtract(v2);

                float Tx = a * deltaV2 * E1.x - deltaV2 * E2.x;
                float Ty = a * deltaV2 * E1.y - deltaV2 * E2.y;
                float Tz = a * deltaV2 * E1.z - deltaV2 * E2.z;
                Vector3f T = new Vector3f(Tx, Ty, Tz);

                if (tangents.containsKey(vertex[0]))
                {
                    tangents.put(vertex[0], tangents.get(vertex[0]).add(T));
                } else
                {
                    tangents.put(vertex[0], T);
                }

                if (tangents.containsKey(vertex2[0]))
                {
                    tangents.put(vertex2[0], tangents.get(vertex2[0]).add(T));
                } else
                {
                    tangents.put(vertex2[0], T);
                }

                if (tangents.containsKey(vertex3[0]))
                {
                    tangents.put(vertex3[0], tangents.get(vertex3[0]).add(T));
                } else
                {
                    tangents.put(vertex3[0], T);
                }
            }

            //TODO:
            //https://learnopengl.com/Advanced-Lighting/Normal-Mapping
        }

        for (int ix = 0; ix < indices.size(); ix++)
        {
            String[] vertex = indices.get(ix).split("/");

            int index = Integer.parseInt(vertex[0]);
            //for the negative syntax sometimes used.
            if (index < 0)
            {
                index = tangents.size() + index;
            }
            
            Vector3f T = tangents.get("" + index).normalize();
            tangentsArray[ix * 2] = T.x;
            tangentsArray[ix * 2 + 1] = T.y;
            tangentsArray[ix * 2 + 2] = T.z;
        }

        RawData data = new RawData(verticesArray, normalsArray, tangentsArray, textureArray, indicesArray, material);
        return data;
    }

    private static Vector3f getCoord(String data, List<Vector3f> vertices)
    {
        int index = Integer.parseInt(data);
        //for the negative syntax sometimes used.
        if (index < 0)
        {
            index = vertices.size() + index;
        } else
        {
            index--;
        }
        return vertices.get(index);
    }

    private static Vector2f getTextureCoord(String data, List<Vector2f> textures)
    {
        Vector2f t = new Vector2f(0, 0);
        if (!data.equals(""))
        {
            int index = Integer.parseInt(data);
            //for the negative syntax sometimes used.
            if (index < 0)
            {
                index = textures.size() + index;
            } else
            {
                index--;
            }
            t = textures.get(index);
        }
        return t;
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

        return new RawData(vertices, null, null, textures, indices, null);
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
