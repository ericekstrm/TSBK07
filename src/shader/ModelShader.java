package shader;

import java.util.List;
import light.DirectionalLight;
import light.PositionalLight;
import loader.Material;
import main.Camera;
import util.Matrix4f;
import util.Vector3f;
import util.Vector4f;

public abstract class ModelShader extends Shader
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
    protected int location_Kc;
    protected int location_Kl;
    protected int location_Kq;
    protected int location_dirLightDirArr[];
    protected int location_dirLightColorArr[];

    //material properties
    protected int location_Ka;
    protected int location_Kd;
    protected int location_Ks;
    protected int location_specularExponent;

    //clipping plane for water reflections
    protected int location_clippingPlane;

    protected int location_viewPos;

    public ModelShader()
    {
        super(VERTEX_FILE, FRAGMENT_FILE);

        getAllUniformLocations();

        start();
        loadProjectionMatrix(Matrix4f.frustum_new());
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
        location_Kc = getUniformLocation("Kc");
        location_Kl = getUniformLocation("Kl");
        location_Kq = getUniformLocation("Kq");
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

        for (int i = 0; i < MAX_LIGHTS; i++)
        {
            location_pointLightPosArr[i] = super.getUniformLocation("pointLightPosArr[" + i + "]");
            location_pointLightColorArr[i] = super.getUniformLocation("pointLightColorArr[" + i + "]");
            location_dirLightDirArr[i] = super.getUniformLocation("dirLightDirArr[" + i + "]");
            location_dirLightColorArr[i] = super.getUniformLocation("dirLightColorArr[" + i + "]");
        }
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
            } else
            {
                loadVector(location_pointLightPosArr[i], new Vector3f(0, 0, 0));
                loadVector(location_pointLightColorArr[i], new Vector3f(0, 0, 0));
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

        loadFloat(location_Kc, 1);
        loadFloat(location_Kl, 0.045f);
        loadFloat(location_Kq, 0.0075f);
    }

    public void loadClippingPlane(Vector4f plane)
    {
        loadVector(location_clippingPlane, plane);
    }
}
