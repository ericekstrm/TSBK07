package shader;

import util.Matrix4f;
import util.Vector3f;

public class GUIShader extends Shader{

    private int location_texture;
    private int location_transform;
    
    private int location_textColor;
    
    public GUIShader()
    {
        super("gui.vert", "gui.frag");
        
        start();
        connectTextureUnits();
        stop();
    }
    
    public GUIShader(String vert, String frag)
    {
        super(vert, frag);
        
        start();
        connectTextureUnits();
        stop();
    }

    @Override
    protected void getAllUniformLocations()
    {
        location_texture = getUniformLocation("texUnit");
        location_transform = getUniformLocation("transform");
        
        location_textColor = getUniformLocation("textColor");
    }
    
    public void connectTextureUnits()
    {
        loadInt(location_texture, 0);
    }
    
    public void loadTransformMatrix(Matrix4f transform)
    {
        loadMatrix(location_transform, transform);
    }
    
    public void loadTextColor(Vector3f color)
    {
        loadVector(location_textColor, color);
    }
}
