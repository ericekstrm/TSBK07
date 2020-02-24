package terrain;

import java.nio.FloatBuffer;
import light.Lights;
import main.Camera;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform3fv;
import shader.Shader;

public class TerrainHandler
{

    //static List<Model> terrainTiles = new ArrayList<>();
    Terrain terrain;
    Shader terrainShader = new Shader("terrain.vert", "terrain.frag");

    public TerrainHandler()
    {
        terrain = new Terrain(terrainShader, "height_map.png", "grass.jpg");
    }

    public void render(Camera camera, Lights lights)
    {
        terrainShader.start();

        lights.loadLights(terrainShader);

        FloatBuffer viewPos = BufferUtils.createFloatBuffer(3);
        camera.getPosition().toBuffer(viewPos);
        glUniform3fv(glGetUniformLocation(terrainShader.getProgramID(), "viewPos"), viewPos);

        //world-to-view matrix
        camera.worldToViewUniform(terrainShader);
        
        terrain.render(terrainShader);

        terrainShader.stop();
    }

    public float getHeight(float x, float z)
    {
    	
        if (x < 1 || z < 1 || Math.floor(x) + 1 >= terrain.heightData.length || Math.floor(z) + 1 >= terrain.heightData.length)
        {
            return 0;
        }
        int x1 = (int) Math.floor(x);
        int x2 = (int) Math.floor(x) + 1;
        int z1 = (int) Math.floor(z);
        int z2 = (int) Math.floor(z) + 1;
        
        //interpolation in the x-direction
        float f1 = (x2 - x) / (x2 - x1) * terrain.heightData[x1][z1] + 
        		   (x - x1) / (x2 - x1) * terrain.heightData[x2][z1];
        
        float f2 = (x2 - x) / (x2 - x1) * terrain.heightData[x1][z2] + 
        		   (x - x1) / (x2 - x1) * terrain.heightData[x2][z2];
        
        float h = (z2 - z) / (z2 - z1) * f1 + (z - z1) / (z2 - z1) * f2;
        
        float height = terrain.heightData[Math.round(x)][Math.round(z)];
        return h;
    }
}
