package terrain;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import model.Model;
import loader.RawData;
import shader.Shader;
import loader.Loader;
import loader.MaterialProperties;
import loader.Texture;
import util.Vector3f;

public class Terrain extends Model
{

    public static final float SIZE = 400;
    public static final int MAX_PIXEL_COLOR = 256 * 256 * 256;
    public static final int MAX_HEIGHT = 40;

    public float[][] heightData;

    public Terrain(Shader shader, String heightMap, String... textures)
    {
        super(shader, generateTerrain(heightMap, textures));
        heightData = getHeightData(heightMap);
    }

    private static RawData generateTerrain(String heightMap, String... textureFileNames)
    {
        BufferedImage image = null;
        try
        {
            image = ImageIO.read(new File("res/textures/" + heightMap));
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        int vertexCount = image.getHeight();

        float[] verticesArray = new float[vertexCount * vertexCount * 3];
        float[] normalsArray = new float[vertexCount * vertexCount * 3];
        float[] textureArray = new float[vertexCount * vertexCount * 2];
        int[] indicesArray = new int[(vertexCount - 1) * (vertexCount - 1) * 6];
        int v = 0;
        for (int i = 0; i < vertexCount; i++)
        {
            for (int j = 0; j < vertexCount; j++)
            {
                verticesArray[v * 3] = SIZE / (vertexCount - 1) * i;
                verticesArray[v * 3 + 1] = getHeight(i, j, image);
                verticesArray[v * 3 + 2] = SIZE / (vertexCount - 1) * j;

                Vector3f n = getNormal(i, j, image);
                normalsArray[v * 3] = n.x;
                normalsArray[v * 3 + 1] = n.y;
                normalsArray[v * 3 + 2] = n.z;

                textureArray[v * 2] = (float) i / (vertexCount - 1) * 50;
                textureArray[v * 2 + 1] = (float) j / (vertexCount - 1) * 50;

                v++;
            }
        }

        int p = 0;
        for (int i = 0; i < vertexCount - 1; i++)
        {
            for (int j = 0; j < vertexCount - 1; j++)
            {
                int topLeft = i * vertexCount + j;
                int topRight = topLeft + 1;
                int bottomLeft = (i + 1) * vertexCount + j;
                int bottomRight = bottomLeft + 1;

                indicesArray[p++] = topRight;
                indicesArray[p++] = bottomLeft;
                indicesArray[p++] = topLeft;
                indicesArray[p++] = bottomRight;
                indicesArray[p++] = bottomLeft;
                indicesArray[p++] = topRight;
            }
        }

        ArrayList<int[]> indicesList = new ArrayList<>();
        indicesList.add(indicesArray);

        ArrayList<Texture> textureIDs = new ArrayList<>();
        for (String textureFileName : textureFileNames)
        {
            textureIDs.add(new Texture(textureFileName));
        }

        ArrayList<MaterialProperties> materialProperties = new ArrayList<>();
        materialProperties.add(new MaterialProperties());

        RawData data = new RawData(verticesArray, normalsArray, textureArray, indicesList, textureIDs, materialProperties);
        return data;
    }

    private static float getHeight(int x, int z, BufferedImage image)
    {
        if (x < 0 || x >= image.getWidth() || z < 0 || z >= image.getWidth())
        {
            return 0;
        }
        float height = image.getRGB(x, z);
        height += MAX_PIXEL_COLOR / 2f; //shift to zero
        height /= MAX_PIXEL_COLOR / 2f; //normalize
        height *= MAX_HEIGHT;         //scale to desired level
        return height;
    }

    private static Vector3f getNormal(int x, int z, BufferedImage image)
    {
        float heightL = getHeight(x - 1, z, image);
        float heightR = getHeight(x + 1, z, image);
        float heightU = getHeight(x, z - 1, image);
        float heightD = getHeight(x, z + 1, image);

        return new Vector3f(heightL - heightR, 2f, heightD - heightU).normalize();
    }

    private static float[][] getHeightData(String heightMap)
    {
        BufferedImage image = null;
        try
        {
            image = ImageIO.read(new File("res/textures/" + heightMap));
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        int vertexCount = image.getHeight();
        float[][] heightData = new float[vertexCount][vertexCount];

        for (int i = 0; i < vertexCount; i++)
        {
            for (int j = 0; j < vertexCount; j++)
            {
                heightData[i][j] = getHeight(i, j, image);
            }
        }
        return heightData;
    }

}
