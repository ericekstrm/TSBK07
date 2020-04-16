package shader;

import util.Matrix4f;

public class TextureModelShader extends ModelShader
{

    private static final String VERTEX_FILE = "model.vert";
    private static final String FRAGMENT_FILE = "model.frag";

    //textures
    private int location_texUnit;
    private int location_normalMap;

    public TextureModelShader(Matrix4f projectionMatrix)
    {
        super(VERTEX_FILE, FRAGMENT_FILE);

        getAllUniformLocations();

        start();
        loadProjectionMatrix(projectionMatrix);
        connectTextureUnits();
        stop();
    }

    public TextureModelShader(String vertexFile, String fragmentFile)
    {
        super(vertexFile, fragmentFile);
    }

    @Override
    protected void getAllUniformLocations()
    {
        super.getAllUniformLocations();
        location_texUnit = getUniformLocation("texUnit");
        location_normalMap = getUniformLocation("normalMap");
    }

    @Override
    public void bindAttributes()
    {
        bindAttribute(POS_ATTRIB, "in_Position");
        bindAttribute(TEX_ATTRIB, "in_Texture");
        bindAttribute(NORMAL_ATTRIB, "in_Normal");
    }

    public void connectTextureUnits()
    {
        loadInt(location_texUnit, 0);
        loadInt(location_normalMap, 1);
    }
}
