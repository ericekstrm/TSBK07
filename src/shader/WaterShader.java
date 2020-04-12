package shader;

import camera.Camera;
import light.PositionalLight;
import camera.FreeCamera;
import util.Matrix4f;

public class WaterShader extends Shader
{

    private static final String VERTEX_FILE = "water.vert";
    private static final String FRAGMENT_FILE = "water.frag";

    private int location_modelToWorld;
    private int location_worldToView;
    private int location_projection;
    private int location_cameraPos;

    private int location_lightColor;
    private int location_lightPosition;
    
    private int location_texReflection;
    private int location_texRefraction;
    private int location_dudvMap;
    private int location_normalMap;
    private int location_depthMap;
    private int location_moveFactor;

    public WaterShader()
    {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void connectTextureUnits()
    {
        loadInt(location_texReflection, 0);
        loadInt(location_texRefraction, 1);
        loadInt(location_dudvMap, 2);
        loadInt(location_normalMap, 3);
        loadInt(location_depthMap, 4);
    }

    @Override
    public void bindAttributes()
    {
        bindAttribute(POS_ATTRIB, "in_Position");
        bindAttribute(TEX_ATTRIB, "in_Texture");
        bindAttribute(NORMAL_ATTRIB, "in_Normal");
    }

    @Override
    protected void getAllUniformLocations()
    {
        location_modelToWorld = getUniformLocation("modelToWorld");
        location_worldToView = getUniformLocation("worldToView");
        location_projection = getUniformLocation("projection");
        location_cameraPos = getUniformLocation("cameraPos");
        
        location_lightColor = getUniformLocation("lightColor");
        location_lightPosition = getUniformLocation("lightPosition");

        location_texReflection = getUniformLocation("reflectionTexture");
        location_texRefraction = getUniformLocation("refractionTexture");
        location_dudvMap = getUniformLocation("dudvMap");
        location_normalMap = getUniformLocation("normalMap");
        location_depthMap = getUniformLocation("depthMap");
        location_moveFactor = getUniformLocation("moveFactor");
    }

    public void loadModelToWorldMatrix(Matrix4f modelToWorldMatrix)
    {
        loadMatrix(location_modelToWorld, modelToWorldMatrix);
    }

    public void loadWorldToViewMatrix(Camera camera)
    {
        loadMatrix(location_worldToView, camera.getWorldtoViewMatrix());
        loadVector(location_cameraPos, camera.getPosition());
    }

    public void loadProjectionMatrix(Matrix4f projection)
    {
        loadMatrix(location_projection, projection);
    }

    public void loadMoveFactor(float factor)
    {
        loadFloat(location_moveFactor, factor);
    }
    
    public void loadLight(PositionalLight light)
    {
        loadVector(location_lightColor, light.getColor());
        loadVector(location_lightPosition, light.getPosition());
    }
}
