package water;

import shader.WaterShader;
import java.util.ArrayList;
import java.util.List;
import light.LightHandler;
import loader.Texture;
import main.Camera;
import util.Matrix4f;

public class WaterHandler
{
    private static final String DUDV_file = "water/dudvmap.png";
    private static final String NORMAL_file = "water/normalmap.png";
    
    private static final float WAVE_SPEED = 0.03f;
    
    Texture dudvMap;
    Texture normalMap;

    WaterShader shader;
    List<WaterTile> waterTiles = new ArrayList<>();

    float moveFactor = 0;
    float height = -5;
    
    public WaterHandler()
    {
        shader = new WaterShader();
        shader.start();
        shader.loadProjectionMatrix(Matrix4f.frustum_new());
        shader.connectTextureUnits();
        shader.stop();
        
        dudvMap = new Texture(DUDV_file);
        normalMap = new Texture(NORMAL_file);
        
        //add tiles
        WaterTile tile = new WaterTile();
        tile.setPosition(200, height, 200);
        tile.setScale(3, 3, 3);
        waterTiles.add(tile);
    }
    
    public void update(float delta)
    {
        moveFactor += WAVE_SPEED * delta;
        moveFactor %= 1;
    }

    public void render(Camera camera, LightHandler lights, WaterFrameBuffer waterFrameBuffer)
    {
        shader.start();
        shader.loadWorldToViewMatrix(camera);
        shader.loadMoveFactor(moveFactor);
        shader.loadLight(lights.getPointLights().get(0));
        for (WaterTile tile : waterTiles)
        {
            tile.render(shader, waterFrameBuffer, dudvMap, normalMap);
        }
        shader.stop();
    }
    
    public float getHeight()
    {
        return height;
    }
}
