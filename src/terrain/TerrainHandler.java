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
        if (x < 0 || z < 0 || x > terrain.heightData.length || z > terrain.heightData.length)
        {
            return 0;
        }
        float height = terrain.heightData[Math.round(x)][Math.round(z)];
        return height;
    }
}
