package terrain;

import java.nio.FloatBuffer;
import light.Lights;
import main.Camera;
import model.Model;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import shader.Shader;

public class TerrainHandler
{

    //static List<Model> terrainTiles = new ArrayList<>();
    Terrain terrain;
    Shader terrainShader = new Shader("test.vert", "test.frag");

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
        FloatBuffer worldToView = BufferUtils.createFloatBuffer(16);
        camera.getWorldtoViewMatrix().toBuffer(worldToView);
        glUniformMatrix4fv(glGetUniformLocation(terrainShader.getProgramID(), "worldToView"), false, worldToView);

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
