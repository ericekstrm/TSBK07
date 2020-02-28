package terrain;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import light.Lights;
import main.Camera;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform3fv;
import shader.Shader;
import util.Vector2f;

public class TerrainHandler
{

    Map<Vector2f, Terrain> terrainTiles = new HashMap<>();
    Shader terrainShader = new Shader("terrain.vert", "terrain.frag");

    public TerrainHandler()
    {
        for (int i = -2; i < 2; i++)
        {
            for (int j = -2; j < 2; j++)
            {
                addTerrain(i, j);
            }
        }
    }

    public void addTerrain(int i, int j)
    {
        if (!terrainTiles.containsKey(new Vector2f(i, j)))
        {
            Terrain t = new Terrain(terrainShader, "height_map.png", "grass.jpg", "dirt.jpg");
            t.setPosition(i * Terrain.SIZE, 0, j * Terrain.SIZE);
            terrainTiles.put(new Vector2f(i, j), t);
        }
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

        for (Map.Entry<Vector2f, Terrain> t : terrainTiles.entrySet())
        {
            t.getValue().render(terrainShader);
        }

        terrainShader.stop();
    }

    public float getHeight(float xin, float zin)
    {
        //find which terrain patch we are in.
        int i =  (int)Math.floor(xin / Terrain.SIZE);
        int j =  (int)Math.floor(zin / Terrain.SIZE);
        float[][] heightData = terrainTiles.get(new Vector2f(i, j)).heightData;

        //normalize coordinates
        float x = (xin / Terrain.SIZE - i) * heightData.length;
        float z = (zin / Terrain.SIZE - j) * heightData.length;

        if (x < 1 || z < 1 || Math.floor(x) + 1 >= heightData.length || Math.floor(z) + 1 >= heightData.length)
        {
            return 0;
        }
        int x1 = (int) Math.floor(x);
        int x2 = (int) Math.floor(x) + 1;
        int z1 = (int) Math.floor(z);
        int z2 = (int) Math.floor(z) + 1;

        //interpolation in the x-direction
        float f1 = (x2 - x) / (x2 - x1) * heightData[x1][z1]
                + (x - x1) / (x2 - x1) * heightData[x2][z1];

        float f2 = (x2 - x) / (x2 - x1) * heightData[x1][z2]
                + (x - x1) / (x2 - x1) * heightData[x2][z2];

        //interpolate in the z-direction
        float h = (z2 - z) / (z2 - z1) * f1 + (z - z1) / (z2 - z1) * f2;

        return h;
    }
}
