package shader;

import java.util.List;
import light.DirectionalLight;
import light.PositionalLight;
import loader.Material;
import main.Camera;
import util.Matrix4f;
import util.Vector3f;

public class TerrainShader extends Shader
{
    private static final int MAX_LIGHTS = 4;

    private static final String VERTEX_FILE = "terrain.vert";
    private static final String FRAGMENT_FILE = "terrain.frag";

    //transforms
    private int location_modelToWorld;
    private int location_worldToView;
    private int location_projection;
    
    //lighting coefficients
    private int location_pointLightPosArr[];
    private int location_pointLightColorArr[];
    private int location_r[];
    private int location_intensity[];
    
    private int location_dirLightDirArr[];
    private int location_dirLightColorArr[];

    //material properties
    private int location_Ka;
    private int location_Kd;
    private int location_Ks;
    private int location_specularExponent;

    private int location_viewPos;
    
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
        location_modelToWorld = getUniformLocation("modelToWorld");
        location_worldToView = getUniformLocation("worldToView");
        location_projection = getUniformLocation("projection");
        location_Ka = getUniformLocation("Ka");
        location_Kd = getUniformLocation("Kd");
        location_Ks = getUniformLocation("Ks");
        location_specularExponent = getUniformLocation("specularExponent");
        location_viewPos = getUniformLocation("viewPos");
        location_rTexture = getUniformLocation("rTexture");
        location_gTexture = getUniformLocation("gTexture");
        location_bTexture = getUniformLocation("bTexture");
        location_blendmap = getUniformLocation("blendmap");
        
        location_pointLightPosArr = new int[MAX_LIGHTS];
        location_pointLightColorArr = new int[MAX_LIGHTS];
        location_dirLightDirArr = new int[MAX_LIGHTS];
        location_dirLightColorArr = new int[MAX_LIGHTS];
        location_r = new int[MAX_LIGHTS];
        location_intensity = new int[MAX_LIGHTS];

        for (int i = 0; i < MAX_LIGHTS; i++) {
            location_pointLightPosArr[i] = super.getUniformLocation("pointLightPosArr[" + i + "]");
            location_pointLightColorArr[i] = super.getUniformLocation("pointLightColorArr[" + i + "]");
            location_dirLightDirArr[i] = super.getUniformLocation("dirLightDirArr[" + i + "]");
            location_dirLightColorArr[i] = super.getUniformLocation("dirLightColorArr[" + i + "]");
            
            location_r[i] = getUniformLocation("r[" + i + "]");
            location_intensity[i] = getUniformLocation("intensity[" + i + "]");
        }
    }
    
    public void connectTextureUnits() {
        loadInt(location_rTexture, 0);
        loadInt(location_gTexture, 1);
        loadInt(location_bTexture, 2);
        loadInt(location_blendmap, 3);
    }
    
    @Override
    public void bindAttributes()
    {
        bindAttribute(POS_ATTRIB, "in_Position");
        bindAttribute(TEX_ATTRIB, "in_Texture");
        bindAttribute(NORMAL_ATTRIB, "in_Normal");
    }

    public void loadModelToWorldMatrix(Matrix4f modelToWorldMatrix)
    {
        loadMatrix(location_modelToWorld, modelToWorldMatrix);
    }

    public void loadWorldToViewMatrix(Camera camera)
    {
        loadMatrix(location_worldToView, camera.getWorldtoViewMatrix());
        loadVector(location_viewPos, camera.getPosition());
    }

    public void loadProjectionMatrix(Matrix4f projection)
    {
        loadMatrix(location_projection, projection);
    }
    
    public void loadMaterialLightingProperties(Material mat)
    {
        loadVector(location_Ka, mat.Ka);
        loadVector(location_Kd, mat.Kd);
        loadVector(location_Ks, mat.Ks);
        loadFloat(location_specularExponent, mat.Ns);
    }
    
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
                loadVector(location_pointLightPosArr[i], new Vector3f(0,0,0));
                loadVector(location_pointLightColorArr[i], new Vector3f(0,0,0));
                loadFloat(location_r[i], 0);
                loadFloat(location_intensity[i], 0);
            }
            if (i < dirLights.size())
            {
                loadVector(location_dirLightDirArr[i], dirLights.get(i).getDirection());
                loadVector(location_dirLightColorArr[i], dirLights.get(i).getColor());
            } else
            {
                loadVector(location_dirLightDirArr[i], new Vector3f(0,0,0));
                loadVector(location_dirLightColorArr[i], new Vector3f(0,0,0));
            }
        }
    }
}
