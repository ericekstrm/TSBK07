package water;

import camera.Camera;
import shader.WaterShader;
import java.util.ArrayList;
import java.util.List;
import light.LightHandler;
import loader.Texture;
import util.Matrix4f;
import util.Vector4f;

public class WaterHandler
{

    private static final String DUDV_file = "objects/water/dudvmap.png";
    private static final String NORMAL_file = "objects/water/normalmap.png";

    private static final float WAVE_SPEED = 0.03f;

    Texture dudvMap;
    Texture normalMap;

    WaterShader shader;
    List<WaterTile> waterTiles = new ArrayList<>();

    float moveFactor = 0;
    float height = -3;

    WaterFrameBuffer waterFrameBuffer;
    Vector4f clippingPlane = new Vector4f(0, -1, 0, 8);

    public WaterHandler(Matrix4f projectionMatrix)
    {
        shader = new WaterShader();
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.connectTextureUnits();
        shader.stop();

        dudvMap = new Texture(DUDV_file);
        normalMap = new Texture(NORMAL_file);

        //add tiles
        WaterTile tile = new WaterTile();
        tile.setPosition(0, height, 0);
        tile.setScale(10, 10, 10);
        waterTiles.add(tile);

        waterFrameBuffer = new WaterFrameBuffer();
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

    public void bindRefractionFrameBuffer()
    {
        waterFrameBuffer.bindRefractionFrameBuffer();
    }

    public void bindReflectionFrameBuffer()
    {
        waterFrameBuffer.bindReflectionFrameBuffer();
    }
    
    public void unbindCurrentFrameBuffer()
    {
        waterFrameBuffer.unbindCurrentFrameBuffer();
    }
    
    public WaterFrameBuffer getFrameBuffer()
    {
        return waterFrameBuffer;
    }
}
