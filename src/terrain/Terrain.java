package terrain;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import loader.Texture;
import model.TextureModel;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import org.lwjgl.opengl.GL13;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shader.Shader;
import shader.TerrainShader;

public class Terrain extends TextureModel
{
    public static final String TERRAIN_TEXTURE_PATH = "terrain/textures/";
    public static final String TERRAIN_BLENDMAP_PATH = "terrain/blendmaps/";

    public static final float SIZE = 400;
    public static final int MAX_PIXEL_COLOR = 256 * 256 * 256;
    public static final int MAX_HEIGHT = 40;

    public float[][] heightData;

    private int rTexture;
    private int gTexture;
    private int bTexture;
    private int blendMap;

    public Terrain(String heightMap, String rTexture, String gTexture, String bTexture, String blendMap)
    {
        super(TerrainGeneration.generateTerrain(heightMap));
        heightData = getHeightData(heightMap);

        this.rTexture = Texture.load(TERRAIN_TEXTURE_PATH + rTexture);
        this.gTexture = Texture.load(TERRAIN_TEXTURE_PATH + gTexture);
        this.bTexture = Texture.load(TERRAIN_TEXTURE_PATH + bTexture);
        this.blendMap = Texture.load(TERRAIN_BLENDMAP_PATH + blendMap);
        //textureIDs.add(new Texture(textures));
    }

    public Terrain(float[][] heightMap, String rTexture, String gTexture, String bTexture, String blendMap)
    {
        super(TerrainGeneration.perlinTerrain(heightMap));
        heightData = new float[64][64]; //heightMap;
        
        this.rTexture = Texture.load(TERRAIN_TEXTURE_PATH + rTexture);
        this.gTexture = Texture.load(TERRAIN_TEXTURE_PATH + gTexture);
        this.bTexture = Texture.load(TERRAIN_TEXTURE_PATH + bTexture);
        this.blendMap = Texture.load(TERRAIN_BLENDMAP_PATH + blendMap);
    }

    public void render(TerrainShader shader)
    {
        for (int i = 0; i < activeVAOs.size(); i++)
        {
            GL30.glBindVertexArray(activeVAOs.get(i));
            GL20.glEnableVertexAttribArray(Shader.POS_ATTRIB);
            GL20.glEnableVertexAttribArray(Shader.TEX_ATTRIB);
            GL20.glEnableVertexAttribArray(Shader.NORMAL_ATTRIB);

            shader.loadModelToWorldMatrix(getModelToWorldMatrix());
            shader.loadMaterialLightingProperties(matProperties.get(i));
            //textures
            /*for (int j = 0; j < textureIDs.get(i).size(); j++)
            {
                glActiveTexture(GL_TEXTURE0 + j);
                glBindTexture(GL_TEXTURE_2D, textureIDs.get(i).get(j));
            }*/
            glActiveTexture(GL13.GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, rTexture);
            glActiveTexture(GL13.GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, gTexture);
            glActiveTexture(GL13.GL_TEXTURE2);
            glBindTexture(GL_TEXTURE_2D, bTexture);
            glActiveTexture(GL13.GL_TEXTURE3);
            glBindTexture(GL_TEXTURE_2D, blendMap);

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
            image = ImageIO.read(new File("res/terrain/heightmaps/" + heightMap));
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
