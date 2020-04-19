package loader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import util.Vector2f;
import util.Vector3f;

public class Loader
{

    public static List<RawData> loadObj(String filename)
    {

        //load .obj file from disk
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader("res/objects/" + filename));
            System.out.println("Loading object: " + filename);
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
                    // if the line starts with 'matlib' it designates a material file
                    // that need to be loaded. One .mtl file can contain many materials.
                    materials.putAll(Material.loadMtlFile(currentLine[1]));
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
                    if (currentLine.length == 4)
                    {
                        indices.add( currentLine[1]);
                        indices.add( currentLine[2]);
                        indices.add( currentLine[3]);
                    } else if (currentLine.length == 5)
                    {
                        indices.add( currentLine[1]);
                        indices.add( currentLine[2]);
                        indices.add( currentLine[3]);
                        indices.add( currentLine[1]);
                        indices.add( currentLine[3]);
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
                            rawdatas.add(packageData(vertices, textures, normals, indices, currentMaterial));
                            indices.clear();
                        }
                    }
                    currentMaterialName = currentLine[1];
                }
                line = br.readLine();
            }
            rawdatas.add(packageData(vertices, textures, normals, indices, materials.get(currentMaterialName)));

            return rawdatas;

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
            i++;
        }

        for (int ix = 0; ix < indices.size(); ix++)
        {
            indicesArray[ix] = ix;
        }

        RawData data = new RawData(verticesArray, normalsArray, textureArray, indicesArray, material);
        return data;
    }

    /**
     * Loads .obj files <br>
     *
     * - One of the problems with this method is that it does not save on
     * memory. in exchange for the extra memory the load time is decreased.
     * (from linear time to constant time) <br>
     *
     * - how do we incorporate different materials for different faces?
     *
     *
     * @param filename name of the file to load. (.obj file format)
     * @param textureFileName texture file name.
     * @return returns the RawData from the .obj file
     */
    public static RawData loadRawData(String filename, String textureFileName, String normalMapFileName)
    {
        //load .obj file
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader("res/objects/" + filename));
            System.out.println("Loading object: " + filename);
        } catch (FileNotFoundException ex)
        {
            System.out.println("file not found: " + filename);
            return null;
        }

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();

        List<String> indices = new ArrayList<>();
        List<Material> materials = new ArrayList<>();

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
                    materials.addAll(Material.loadMtlFile(currentLine[1]).values());
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
                textureArray[i * 2 + 1] = -t.y; //negative on the y coordinate for the texture coordinate. why is it needed?

                Vector3f n = normals.get(Integer.parseInt(vertex[2]) - 1);
                normalsArray[i * 3] = n.x;
                normalsArray[i * 3 + 1] = n.y;
                normalsArray[i * 3 + 2] = n.z;
            }

            for (int i = 0; i < indicesArray.length; i++)
            {
                indicesArray[i] = i;
            }

            RawData data = new RawData(verticesArray, normalsArray, textureArray, indicesArray, new Texture(textureFileName, normalMapFileName), new Material());
            return data;

        } catch (IOException | NumberFormatException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static RawData loadRawData(String filename, String textureFileName)
    {
        //load .obj file
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader("res/objects/" + filename));
            System.out.println("Loading object: " + filename);
        } catch (FileNotFoundException ex)
        {
            System.out.println("file not found: " + filename);
            return null;
        }

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();

        List<String> indices = new ArrayList<>();
        List<Material> materials = new ArrayList<>();
        
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
                    materials.addAll(Material.loadMtlFile(currentLine[1]).values());
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
                    if (currentLine.length == 4)
                    {
                        indices.add( currentLine[1]);
                        indices.add( currentLine[2]);
                        indices.add( currentLine[3]);
                    } else if (currentLine.length == 5)
                    {
                        indices.add( currentLine[1]);
                        indices.add( currentLine[2]);
                        indices.add( currentLine[3]);
                        indices.add( currentLine[1]);
                        indices.add( currentLine[3]);
                        indices.add(currentLine[4]);
                    }
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
                textureArray[i * 2 + 1] = -t.y; //negative on the y coordinate for the texture coordinate. why is it needed?

                Vector3f n = normals.get(Integer.parseInt(vertex[2]) - 1);
                normalsArray[i * 3] = n.x;
                normalsArray[i * 3 + 1] = n.y;
                normalsArray[i * 3 + 2] = n.z;
            }

            for (int i = 0; i < indicesArray.length; i++)
            {
                indicesArray[i] = i;
            }

            RawData data = new RawData(verticesArray, normalsArray, textureArray, indicesArray, new Texture(textureFileName), new Material());
            return data;

        } catch (IOException | NumberFormatException e)
        {
            e.printStackTrace();
            return null;
        }
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
