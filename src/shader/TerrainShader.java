package shader;

import java.util.List;
import light.DirectionalLight;
import light.PositionalLight;
import util.Vector3f;

public class TerrainShader extends ModelShader
{

    private static final String VERTEX_FILE = "terrain.vert";
    private static final String FRAGMENT_FILE = "terrain.frag";

    private int location_r[];
    private int location_intensity[];

    private int location_rTexture;
    private int location_gTexture;
    private int location_bTexture;
    private int location_blendmap;

    public TerrainShader()
    {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations()
    {
        super.getAllUniformLocations();
        location_rTexture = getUniformLocation("rTexture");
        location_gTexture = getUniformLocation("gTexture");
        location_bTexture = getUniformLocation("bTexture");
        location_blendmap = getUniformLocation("blendmap");

        location_r = new int[MAX_LIGHTS];
        location_intensity = new int[MAX_LIGHTS];

        for (int i = 0; i < MAX_LIGHTS; i++)
        {
            location_r[i] = getUniformLocation("r[" + i + "]");
            location_intensity[i] = getUniformLocation("intensity[" + i + "]");
        }
    }

    public void connectTextureUnits()
    {
        loadInt(location_rTexture, 0);
        loadInt(location_gTexture, 1);
        loadInt(location_bTexture, 2);
        loadInt(location_blendmap, 3);
    }

    @Override
    public void loadLights(List<PositionalLight> pointLights, List<DirectionalLight> dirLights)
    {
        for (int i = 0; i < MAX_LIGHTS; i++)
        {
            if (i < pointLights.size())
            {
                loadVector(location_pointLightPosArr[i], pointLights.get(i).getPosition());
                loadVector(location_pointLightColorArr[i], pointLights.get(i).getColor());
                loadFloat(location_r[i], pointLights.get(i).getR());
                loadFloat(location_intensity[i], pointLights.get(i).getIntensity());
            } else
            {
                loadVector(location_pointLightPosArr[i], new Vector3f(0, 0, 0));
                loadVector(location_pointLightColorArr[i], new Vector3f(0, 0, 0));
                loadFloat(location_r[i], 0);
                loadFloat(location_intensity[i], 0);
            }
            if (i < dirLights.size())
            {
                loadVector(location_dirLightDirArr[i], dirLights.get(i).getDirection());
                loadVector(location_dirLightColorArr[i], dirLights.get(i).getColor());
            } else
            {
                loadVector(location_dirLightDirArr[i], new Vector3f(0, 0, 0));
                loadVector(location_dirLightColorArr[i], new Vector3f(0, 0, 0));
            }
        }
    }
}
