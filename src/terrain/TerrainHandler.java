package terrain;

import camera.Camera;
import java.util.HashMap;
import java.util.Map;
import light.LightHandler;
import camera.FreeCamera;
import shader.TerrainShader;
import util.Matrix4f;
import util.Vector2f;
import util.Vector3f;
import util.Vector4f;

public class TerrainHandler
{

    Map<Vector2f, Terrain> terrainTiles = new HashMap<>();
    TerrainShader terrainShader;

    public TerrainHandler()
    {
        terrainShader = new TerrainShader();
        terrainShader.start();
        terrainShader.loadProjectionMatrix(Matrix4f.frustum_new());
        terrainShader.connectTextureUnits();
        terrainShader.stop();

        for (int i = -2; i < 3; i++)
        {
            for (int j = -2; j < 3; j++)
            {
                if ((i != 0 || j != 0) && (i != -1 || j != -1))
                {
                    addTerrain(i, j, "height_map.png", "grass.jpg", "dirt.jpg", "cobblestone.jpg", "blendmap.jpg");
                }
            }
        }
        addTerrain(0, 0, "height_map_lake.png", "grass.jpg", "dirt.jpg", "cobblestone.jpg", "blendmap.jpg");
        addTerrain(-1, -1, "height_map_forrest.png", "grass.jpg", "dirt.jpg", "cobblestone_new.jpg", "blendmap_forrest2.jpg");
    }

    public void addTerrain(int i, int j, String heightmap, String rTextures, String gTexture, String bTexture, String blendmap)
    {
        if (!terrainTiles.containsKey(new Vector2f(i, j)))
        {
            Terrain t = new Terrain(heightmap, rTextures, gTexture, bTexture, blendmap);
            t.setPosition(i * Terrain.SIZE, 0, j * Terrain.SIZE);
            terrainTiles.put(new Vector2f(i, j), t);
        }
    }

    public void render(Camera camera, LightHandler lights, Vector4f clippingPlane)
    {
        terrainShader.start();
        terrainShader.loadLights(lights.getPointLights(), lights.getDirLights());
        terrainShader.loadWorldToViewMatrix(camera);
        terrainShader.loadClippingPlane(clippingPlane);

        for (Map.Entry<Vector2f, Terrain> t : terrainTiles.entrySet())
        {
            t.getValue().render(terrainShader);
        }

        terrainShader.stop();
    }

    /**
     * returns the height of a point of the terrain.
     *
     * <br> not really working great :(
     *
     * @param xin
     * @param zin
     * @return
     */
    public float getHeight(float xin, float zin)
    {
        //find which terrain patch we are in.
        int i = (int) Math.floor(xin / Terrain.SIZE);
        int j = (int) Math.floor(zin / Terrain.SIZE);
        Terrain t = terrainTiles.get(new Vector2f(i, j));
        if (t == null)
        {
            return 0;
        }
        float[][] heightData = t.heightData;

        //normalise coordinates
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

    /**
     * returns the normal vector of a point of the terrain.
     *
     * <br> good enough for now.
     *
     * @param xin
     * @param zin
     * @return
     */
    public Vector3f getNormal(float xin, float zin)
    {
        //find which terrain patch we are in.
        int i = (int) Math.floor(xin / Terrain.SIZE);
        int j = (int) Math.floor(zin / Terrain.SIZE);
        Terrain t = terrainTiles.get(new Vector2f(i, j));
        if (t == null)
        {
            return new Vector3f(0, 1, 0);
        }
        float[][] heightData = t.heightData;

        //normalize coordinates
        float x = (xin / Terrain.SIZE - i) * heightData.length;
        float z = (zin / Terrain.SIZE - j) * heightData.length;

        //if outside the terrain, assume the plain is flat
        if (x < 1 || z < 1 || Math.floor(x) + 1 >= heightData.length || Math.floor(z) + 1 >= heightData.length)
        {
            return new Vector3f(0, 1, 0);
        }

        int x1 = (int) Math.floor(x);
        int x2 = (int) Math.floor(x) + 1;
        int z1 = (int) Math.floor(z);
        int z2 = (int) Math.floor(z) + 1;

        //which triangle are we in?
        if (x - x1 < z - z1)
        {
            Vector3f v1 = new Vector3f(x2 - x1, heightData[x2][z2] - heightData[x1][z1], z2 - z1);
            Vector3f v2 = new Vector3f(x2 - x1, heightData[x2][z1] - heightData[x1][z1], z1 - z1);
            return v1.cross(v2).normalize();

        } else
        {
            Vector3f v1 = new Vector3f(x2 - x1, heightData[x2][z2] - heightData[x1][z1], z2 - z1);
            Vector3f v2 = new Vector3f(x1 - x1, heightData[x1][z2] - heightData[x1][z1], z2 - z1);
            return v2.cross(v1).normalize();
        }
    }
}
