package shader;

import util.Matrix4f;

public class TerrainShader extends ModelShader
{

    private static final String VERTEX_FILE = "terrain.vert";
    private static final String FRAGMENT_FILE = "terrain.frag";

    private int location_rTexture;
    private int location_gTexture;
    private int location_bTexture;
    private int location_blendmap;

    public TerrainShader()
    {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations()
    {
        super.getAllUniformLocations();
        location_rTexture = getUniformLocation("rTexture");
        location_gTexture = getUniformLocation("gTexture");
        location_bTexture = getUniformLocation("bTexture");
        location_blendmap = getUniformLocation("blendmap");
    }

    @Override
    public void connectTextureUnits()
    {
        super.connectTextureUnits();
        loadInt(location_rTexture, 0);
        loadInt(location_gTexture, 1);
        loadInt(location_bTexture, 2);
        loadInt(location_blendmap, 3);
    }
}
