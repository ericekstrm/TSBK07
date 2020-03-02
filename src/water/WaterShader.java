package water;

import main.Camera;
import shader.Shader;
import util.Matrix4f;

public class WaterShader extends Shader
{

    private static final String VERTEX_FILE = "water.vert";
    private static final String FRAGMENT_FILE = "water.frag";

    private int location_modelToWorld;
    private int location_worldToView;
    private int location_projection;

    private int location_texUnit;

    public WaterShader()
    {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void connectTextureUnits()
    {
        loadInt(location_texUnit, 0);
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
        location_texUnit = getUniformLocation("texUnit");
    }

    public void loadModelToWorldMatrix(Matrix4f modelToWorldMatrix)
    {
        loadMatrix(location_modelToWorld, modelToWorldMatrix);
    }

    public void loadWorldToViewMatrix(Camera camera)
    {
        loadMatrix(location_worldToView, camera.getWorldtoViewMatrix());
    }

    public void loadProjectionMatrix(Matrix4f projection)
    {
        loadMatrix(location_projection, projection);
    }

}
