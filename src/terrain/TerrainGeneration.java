package terrain;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import loader.Material;
import loader.RawData;
import loader.Texture;
import static terrain.Terrain.MAX_HEIGHT;
import static terrain.Terrain.MAX_PIXEL_COLOR;
import static terrain.Terrain.SIZE;
import util.Util;
import util.Vector3f;

public class TerrainGeneration
{

    public static RawData generateTerrain(String heightMap, String... textureFileNames)
    {
        BufferedImage image = null;
        try
        {
            image = ImageIO.read(new File("res/heightmaps/" + heightMap));
        } catch (IOException e)
        {
            System.out.println("cant read file: " + heightMap);
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

                indicesArray[p++] = topRight;
                indicesArray[p++] = bottomLeft;
                indicesArray[p++] = topLeft;
                indicesArray[p++] = bottomRight;
                indicesArray[p++] = bottomLeft;
                indicesArray[p++] = topRight;
            }
        }

        RawData data = new RawData(verticesArray, normalsArray, textureArray, indicesArray, new Texture(textureFileNames), new Material());
        return data;
    }

    public static float getHeight(int x, int z, BufferedImage image)
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

        return new Vector3f(heightL - heightR, 2f, heightU - heightD).normalize();
    }

    public static RawData perlinTerrain(float[][] heightMap, String... textureFileNames)
    {
        int vertexCount = 64;

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
                verticesArray[v * 3 + 1] = 0; //heightMap[i][j];
                verticesArray[v * 3 + 2] = SIZE / (vertexCount - 1) * j;

                Vector3f n = new Vector3f(0,1,0); //getPerlinNormal(i, j, heightMap);
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

                indicesArray[p++] = topRight;
                indicesArray[p++] = bottomLeft;
                indicesArray[p++] = topLeft;
                indicesArray[p++] = bottomRight;
                indicesArray[p++] = bottomLeft;
                indicesArray[p++] = topRight;
            }
        }

        RawData data = new RawData(verticesArray, normalsArray, textureArray, indicesArray, new Texture(textureFileNames), new Material());
        return data;
    }

    public static float[][] getFFTHeightMap(int width, int height)
    {
        float[][] frequencies = new float[width][height];

        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                System.out.println((float) Math.sqrt(i * i + j * j));
                frequencies[i][j] = 100 * Util.rand(0, 5) / ((float) Math.sqrt(i * i + j * j) + 1);
            }
        }
        float[][] ampitude = Util.fft2D(frequencies);
        return ampitude;
    }

    private static Vector3f getPerlinNormal(int x, int z, float[][] heightMap)
    {
        /*float heightL = heightMap[x - 1][z];
        float heightR = heightMap[x + 1][z];
        float heightU = heightMap[x][z - 1];
        float heightD = heightMap[x][z + 1];*/

        //return new Vector3f(heightL - heightR, 2f, heightU - heightD).normalize();
        return new Vector3f(1, 1, 1).normalize();
    }
}
