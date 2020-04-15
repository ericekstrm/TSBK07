package camera;

import java.nio.DoubleBuffer;
import main.main;
import model.Model;
import model.ModelHandler;
import org.lwjgl.BufferUtils;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import terrain.TerrainHandler;
import util.Matrix4f;
import util.Vector3f;
import util.Vector4f;

public class RayCaster
{

    Vector3f ray;

    Camera camera;

    public RayCaster(Camera camera)
    {
        this.camera = camera;
    }

    public void update(long window)
    {
        DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(window, xBuffer, yBuffer);
        float xpos = (float) xBuffer.get(0);
        float ypos = (float) yBuffer.get(0);

        //noramlize coordinates
        float x = (2.0f * (float) xpos) / main.WIDTH - 1.0f;
        float y = 1.0f - (2.0f * (float) ypos) / main.HEIGHT;
        Vector4f ray_clip = new Vector4f(x, y, 1, 1);
        ray_clip = ray_clip.normalize();

        Vector4f ray_eye = Matrix4f.frustum_new().inverse().multiply(ray_clip);
        Vector4f ray_world = camera.getWorldtoViewMatrix().inverse().multiply(ray_eye);
        ray = new Vector3f(ray_world.x, ray_world.y, ray_world.z).normalize();
    }

    public void useRay(ModelHandler models, TerrainHandler terrain)
    {
        double minDis = 0;
        double maxDis = 600;
        for (int i = 0; i < 100; i++)
        {
            double scale = (maxDis + minDis) / 2;
            Vector3f guess = ray.scale((float) scale).add(camera.position);

            if (guess.y < terrain.getHeight(guess.x, guess.z))
            {
                maxDis -= (maxDis - minDis) / 2;
            } else
            {
                minDis += (maxDis - minDis) / 2;
            }
        }

        Vector3f terrainPos = ray.scale((float) (maxDis + minDis) / 2).add(camera.position);

        Model m = models.get("pine");
        m.setPosition(terrainPos);
        models.set("pine", m);
    }

    public void setCamera(Camera camera)
    {
        this.camera = camera;
    }
}
