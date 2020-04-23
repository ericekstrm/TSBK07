package shader;

import camera.Camera;
import util.Matrix4f;
import util.Vector3f;

public class LightShader extends Shader
{
    private static final String VERTEX_FILE = "light.vert";
    private static final String FRAGMENT_FILE = "light.frag";

    private int location_color;
    private int location_modelToWorld;
    private int location_worldToView;
    private int location_projection;
    
    public LightShader(Matrix4f projectionMatrix)
    {
        super(VERTEX_FILE, FRAGMENT_FILE);
        
        start();
        loadProjectionMatrix(projectionMatrix);
        stop();
        
        getAllUniformLocations();
    }
    
    @Override
    protected void getAllUniformLocations()
    {
        location_color = getUniformLocation("color");
        location_modelToWorld = getUniformLocation("modelToWorld");
        location_worldToView = getUniformLocation("worldToView");
        location_projection = getUniformLocation("projection");
    }
    
    @Override
    public void bindAttributes()
    {
        bindAttribute(POS_ATTRIB, "in_Position");
    }
    
    public void loadColor(Vector3f color)
    {
        loadVector(location_color, color);
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
