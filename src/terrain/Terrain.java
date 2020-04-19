package terrain;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import model.TextureModel;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shader.Shader;
import shader.TerrainShader;

public class Terrain extends TextureModel
{

    public static final float SIZE = 400;
    public static final int MAX_PIXEL_COLOR = 256 * 256 * 256;
    public static final int MAX_HEIGHT = 40;

    public float[][] heightData;

    public Terrain(String heightMap, String... textures)
    {
        super(TerrainGeneration.generateTerrain(heightMap, textures));
        heightData = getHeightData(heightMap);
    }
    
    public Terrain(float[][] heightMap, String... textures)
    {
        super(TerrainGeneration.perlinTerrain(heightMap, textures));
        heightData = new float[64][64]; //heightMap;
    }
    
    public void render(TerrainShader shader)
    {
        for (int i = 0; i < activeVAOs.size(); i++)
        {
            GL30.glBindVertexArray(activeVAOs.get(i));
            GL20.glEnableVertexAttribArray(Shader.POS_ATTRIB);
            GL20.glEnableVertexAttribArray(Shader.TEX_ATTRIB);
            GL20.glEnableVertexAttribArray(Shader.NORMAL_ATTRIB);

            shader.loadModelToWorldMatrix(getModelToViewMatrix());
            shader.loadMaterialLightingProperties(matProperties.get(i));
            //textures
            for (int j = 0; j < textureIDs.get(i).size(); j++)
            {
                glActiveTexture(GL_TEXTURE0 + j);
                glBindTexture(GL_TEXTURE_2D, textureIDs.get(i).get(j));
            }

            //draw!
            GL11.glDrawElements(GL11.GL_TRIANGLES, nrOfIndices.get(i), GL11.GL_UNSIGNED_INT, 0);
            deactivate();
        }
    }



    private static float[][] getHeightData(String heightMap)
    {
        BufferedImage image = null;
        try
        {
            image = ImageIO.read(new File("res/heightmaps/" + heightMap));
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
                heightData[i][j] = TerrainGeneration.getHeight(i, j, image);
            }
        }
        return heightData;
    }

}
