package shader;

import camera.Camera;
import util.Matrix4f;

public class ShadowShader extends Shader
{

    private static final String VERTEX_FILE = "shadow.vert";
    private static final String FRAGMENT_FILE = "shadow.frag";

    protected int location_modelToView;
    protected int location_projection;

    private int location_texUnit;
    private int location_hasTexture;

    public ShadowShader(Matrix4f projectionMatrix)
    {
        super(VERTEX_FILE, FRAGMENT_FILE);

        getAllUniformLocations();

        start();
        loadProjectionMatrix(projectionMatrix);
        connectTextureUnits();
        stop();
    }

    @Override
    protected void getAllUniformLocations()
    {
        location_modelToView = getUniformLocation("modelToView");
        location_projection = getUniformLocation("projection");
        location_texUnit = getUniformLocation("texUnit");
        location_hasTexture = getUniformLocation("hasTexture");
    }
    
    public void connectTextureUnits()
    {
        loadInt(location_texUnit, 0);
    }

    public void loadModelToViewMatrix(Matrix4f modelToWorld, Camera camera)
    {
        loadMatrix(location_modelToView, camera.getWorldtoViewMatrix().multiply(modelToWorld));
    }

    public void loadProjectionMatrix(Matrix4f projection)
    {
        loadMatrix(location_projection, projection);
    }
    
    public void loadHasTexture(boolean b)
    {
        loadBoolean(location_hasTexture, b);
    }
}
