package shader;

import camera.Camera;
import java.util.List;
import light.DirectionalLight;
import light.PositionalLight;
import loader.Material;
import util.Matrix4f;
import util.Vector3f;
import util.Vector4f;

public class ModelShader extends Shader
{

    protected static final int MAX_LIGHTS = 4;

    private static final String VERTEX_FILE = "model.vert";
    private static final String FRAGMENT_FILE = "model.frag";

    //transforms
    protected int location_modelToWorld;
    protected int location_worldToView;
    protected int location_projection;

    //lighting coefficients
    protected int location_pointLightPosArr[];
    protected int location_pointLightColorArr[];
    private int location_r[];
    private int location_intensity[];
    protected int location_dirLightDirArr[];
    protected int location_dirLightColorArr[];

    //material properties
    protected int location_Ka;
    protected int location_Kd;
    protected int location_Ks;
    protected int location_specularExponent;

    //shadows
    private int location_lightSpaceMatrix;
    private int location_shadowMap;

    //clipping plane for water reflections
    protected int location_clippingPlane;

    protected int location_viewPos;

    //textures
    private int location_texUnit;
    private int location_normalMap;
    private int location_hasTexture;

    public ModelShader(Matrix4f projectionMatrix)
    {
        super(VERTEX_FILE, FRAGMENT_FILE);

        getAllUniformLocations();

        start();
        loadProjectionMatrix(projectionMatrix);
        connectTextureUnits();
        stop();
    }

    public ModelShader(String vertexFile, String fragmentFile)
    {
        super(vertexFile, fragmentFile);
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
        location_clippingPlane = getUniformLocation("clippingPlane");
        location_viewPos = getUniformLocation("viewPos");

        location_pointLightPosArr = new int[MAX_LIGHTS];
        location_pointLightColorArr = new int[MAX_LIGHTS];
        location_dirLightDirArr = new int[MAX_LIGHTS];
        location_dirLightColorArr = new int[MAX_LIGHTS];
        location_r = new int[MAX_LIGHTS];
        location_intensity = new int[MAX_LIGHTS];

        for (int i = 0; i < MAX_LIGHTS; i++)
        {
            location_pointLightPosArr[i] = super.getUniformLocation("pointLightPosArr[" + i + "]");
            location_pointLightColorArr[i] = super.getUniformLocation("pointLightColorArr[" + i + "]");
            location_dirLightDirArr[i] = super.getUniformLocation("dirLightDirArr[" + i + "]");
            location_dirLightColorArr[i] = super.getUniformLocation("dirLightColorArr[" + i + "]");

            location_r[i] = getUniformLocation("r[" + i + "]");
            location_intensity[i] = getUniformLocation("intensity[" + i + "]");
        }

        location_texUnit = getUniformLocation("texUnit");
        location_normalMap = getUniformLocation("normalMap");
        location_hasTexture = getUniformLocation("hasTexture");

        location_lightSpaceMatrix = getUniformLocation("lightSpaceMatrix");
        location_shadowMap = getUniformLocation("shadowMap");
    }

    public void connectTextureUnits()
    {
        loadInt(location_texUnit, 0);
        loadInt(location_normalMap, 1);
        loadInt(location_shadowMap, 10);
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

    public void loadHasTexture(boolean b)
    {
        loadBoolean(location_hasTexture, b);
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

    public void loadClippingPlane(Vector4f plane)
    {
        loadVector(location_clippingPlane, plane);
    }

    public void loadLightSpaceMatrix(Matrix4f lightSpaceMatrix)
    {
        loadMatrix(location_lightSpaceMatrix, lightSpaceMatrix);
    }
}
