package light;

import camera.Camera;
import java.util.ArrayList;
import java.util.List;
import shader.LightShader;
import util.Matrix4f;
import util.Vector3f;

public class LightHandler
{

    private LightShader lightShader;

    private List<PositionalLight> pointLights = new ArrayList<>();
    private List<DirectionalLight> dirLights = new ArrayList<>();
    private Sun sun;

    public LightHandler(Matrix4f projectionMatrix)
    {
        lightShader = new LightShader(projectionMatrix);
        sun = new Sun(projectionMatrix);

        addPosLight(new Vector3f(-114.410576f, -1.0080415f, -267.67914f), new Vector3f(226f / 255, 88f / 255, 34f / 255));
        addPosLight(new Vector3f(-35.859524f, 3f, -336.1039f), new Vector3f(255f / 255, 214f / 255, 170f / 255));
    }

    public void addPosLight(Vector3f pos, Vector3f color)
    {
        pointLights.add(new PositionalLight(pos, color));
    }

    public void addDirLight(Vector3f dir, Vector3f color)
    {
        dirLights.add(new DirectionalLight(dir, color));
    }

    public void render(Camera camera)
    {
        sun.render(camera);
        lightShader.start();
        lightShader.loadWorldToViewMatrix(camera);

        for (PositionalLight light : pointLights)
        {
            light.render(lightShader);
        }

        lightShader.stop();
    }

    public void moveLight(int index, Matrix4f transform)
    {
        pointLights.get(index).setPosition(transform.multiply(pointLights.get(index).getPosition()));
    }

    public void rotateDirLight(int index, Matrix4f rotationMatrix)
    {
        dirLights.get(index).setDirection(rotationMatrix.multiply(dirLights.get(index).getDirection()));
    }

    public List<PositionalLight> getPointLights()
    {
        return pointLights;
    }

    public List<DirectionalLight> getDirLights()
    {
        List<DirectionalLight> lights = new ArrayList(dirLights);
        lights.add(sun.getDirLight());
        return lights;
    }

    public Sun getSun()
    {
        return sun;
    }

    public void update(float deltaTime)
    {
        //rotates the sun around the z-axis one lap every 6 minutes
        sun.update(deltaTime);
    }
}
