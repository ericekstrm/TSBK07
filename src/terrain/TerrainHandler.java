package terrain;

import camera.Camera;
import java.util.HashMap;
import java.util.Map;
import light.LightHandler;
import light.ShadowHandler;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import shader.TerrainShader;
import util.Matrix4f;
import util.Vector2f;
import util.Vector3f;
import util.Vector4f;

public class TerrainHandler
{

    Map<Vector2f, Terrain> terrainTiles = new HashMap<>();
    TerrainShader terrainShader;

    public TerrainHandler(Matrix4f projectionMatrix)
    {
        terrainShader = new TerrainShader();
        terrainShader.start();
        terrainShader.loadProjectionMatrix(projectionMatrix);
        terrainShader.connectTextureUnits();
        terrainShader.stop();

        addTerrain(0, 0, "island2.jpg", "cobblestone.jpg", "grass.jpg", "sand.jpg", "island2_blendmap.jpg");
        //addTerrain(0, -1, "pond.png", "grass.jpg", "dirt.jpg", "cobblestone.jpg", "pond_blendmap.png");
        //addTerrain(-1, 0, "height_map_lake.png", "grass.jpg", "dirt.jpg", "cobblestone_new.jpg", "blendmap_forrest2.jpg");
        //addTerrainFFT(-1, -1, "grass.jpg", "dirt.jpg", "cobblestone_new.jpg", "blendmap_forrest2.jpg");
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

    public void addTerrainFFT(int i, int j, String rTextures, String gTexture, String bTexture, String blendmap)
    {
        if (!terrainTiles.containsKey(new Vector2f(i, j)))
        {
            float[][] heightMap = TerrainGeneration.getFFTHeightMap(64, 64);
            Terrain t = new Terrain(heightMap, rTextures, gTexture, bTexture, blendmap);
            t.setPosition(i * Terrain.SIZE, 0, j * Terrain.SIZE);
            terrainTiles.put(new Vector2f(i, j), t);
        }
    }

    public void render(Camera camera, LightHandler lights, Vector4f clippingPlane, Matrix4f projectionMatrix, ShadowHandler shadows)
    {
        terrainShader.start();
        terrainShader.loadProjectionMatrix(projectionMatrix);
        terrainShader.loadWorldToViewMatrix(camera);
        terrainShader.loadLights(lights.getPointLights(), lights.getDirLights());
        terrainShader.loadClippingPlane(clippingPlane);

        //shadows
        terrainShader.loadLightSpaceMatrix(shadows.getLightSpaceMatrix());
        GL13.glActiveTexture(GL13.GL_TEXTURE10);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, shadows.getDepthMap());

        for (Terrain t : terrainTiles.values())
        {
            t.render(terrainShader);
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

    public Map<Vector2f, Terrain> getTerrainTiles()
    {
        return terrainTiles;
    }
}
