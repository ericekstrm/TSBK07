package shader;

import static shader.Shader.POS_ATTRIB;
import util.Matrix4f;

public class ParticleShader extends Shader{

    private static final String VERTEX_FILE = "particle.vert";
    private static final String FRAGMENT_FILE = "particle.frag";

    private int location_modelToView;
    private int location_projection;
    private int location_texUnit;
    
    public ParticleShader(Matrix4f projectionMatrix)
    {
        super(VERTEX_FILE, FRAGMENT_FILE);
        
        start();
        loadProjectionMatrix(projectionMatrix);
        connectTextureUnits();
        stop();
        
        getAllUniformLocations();
    }
    
    @Override
    protected void getAllUniformLocations()
    {
        location_modelToView = getUniformLocation("modelToView");
        location_projection = getUniformLocation("projection");
        location_texUnit = getUniformLocation("texUnit");
    }
    
    @Override
    public void bindAttributes()
    {
        bindAttribute(POS_ATTRIB, "in_Position");
    }

    public void loadModelToViewMatrix(Matrix4f modelToWorldMatrix, Matrix4f worldToViewMatrix)
    {
        //loadMatrix(location_modelToView, worldToViewMatrix.multiply(modelToWorldMatrix));
        loadMatrix(location_modelToView, worldToViewMatrix.multiply(modelToWorldMatrix));
    }
    
    public void loadProjectionMatrix(Matrix4f projection)
    {
        loadMatrix(location_projection, projection);
    }
    
    public void connectTextureUnits()
    {
        loadInt(location_texUnit, 0);
    }
}
