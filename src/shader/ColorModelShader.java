package shader;

import static shader.Shader.NORMAL_ATTRIB;
import static shader.Shader.POS_ATTRIB;
import util.Matrix4f;

public class ColorModelShader extends ModelShader{
    
    private static final String VERTEX_FILE = "colormodel.vert";
    private static final String FRAGMENT_FILE = "colormodel.frag";

    public ColorModelShader(Matrix4f projectionMatrix)
    {
        super(VERTEX_FILE, FRAGMENT_FILE);
        
        getAllUniformLocations();
        
        start();
        loadProjectionMatrix(projectionMatrix);
        stop();
    }
    
    public ColorModelShader(String vertexFile, String fragmentFile)
    {
        super(vertexFile, fragmentFile);
    }

    @Override
    public void bindAttributes()
    {
        bindAttribute(POS_ATTRIB, "in_Position");
        bindAttribute(NORMAL_ATTRIB, "in_Normal");
    }
}
