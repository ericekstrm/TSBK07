package shader;

import util.Matrix4f;
import util.Vector3f;

public class SkyBoxShader extends Shader
{

    private static final String VERTEX_FILE = "skybox.vert";
    private static final String FRAGMENT_FILE = "skybox.frag";

    private int location_projection;
    private int location_worldToView;
    private int location_modelToWorld;
    private int location_fogColor;

    public SkyBoxShader(Matrix4f projectionMatrix)
    {
        super(VERTEX_FILE, FRAGMENT_FILE);

        start();
        loadProjectionMatrix(projectionMatrix);
        stop();
    }

    @Override
    protected void getAllUniformLocations()
    {
        location_projection = getUniformLocation("projection");
        location_worldToView = getUniformLocation("worldToView");
        location_modelToWorld = getUniformLocation("modelToWorld");
        location_fogColor = getUniformLocation("fogColor");
    }

    public void loadProjectionMatrix(Matrix4f projection)
    {
        loadMatrix(location_projection, projection);
    }

    public void loadWorldToViewMatrix(Matrix4f worldToViewMatrix)
    {
        loadMatrix(location_worldToView, Matrix4f.remove_translation(worldToViewMatrix));
    }

    public void loadModelToWorldMatrix(Matrix4f modelToWorldMatrix)
    {
        loadMatrix(location_modelToWorld, modelToWorldMatrix);
    }

    public void loadFogcolor(Vector3f color)
    {
        loadVector(location_fogColor, color);
    }
}
