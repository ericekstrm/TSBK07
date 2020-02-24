package terrain;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import model.Model;
import model.RawData;
import shader.Shader;
import util.Loader;
import util.Vector3f;

public class Terrain extends Model
{

    private static final int SIZE = 400;
    private static final int MAX_PIXEL_COLOR = 256 * 256 * 256;
    private static final int MAX_HEIGHT = 40;

    public float[][] heightData;

    public Terrain(Shader shader, String heightMap, String texture)
    {
        super(shader, generateTerrain(heightMap, texture));
        heightData = getHeightData(heightMap);
    }

    private static RawData generateTerrain(String heightMap, String texture)
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

                textureArray[v * 2] = (float) i / (vertexCount - 1);
                textureArray[v * 2 + 1] = (float) j / (vertexCount - 1);

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

                indicesArray[p++] = topLeft;
                indicesArray[p++] = bottomLeft;
                indicesArray[p++] = topRight;
                indicesArray[p++] = topRight;
                indicesArray[p++] = bottomLeft;
                indicesArray[p++] = bottomRight;
            }
        }

        int texID = Loader.loadTexture(texture);
        RawData data = new RawData(verticesArray, textureArray, indicesArray, normalsArray, texID);
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
