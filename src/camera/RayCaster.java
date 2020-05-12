package camera;

import java.nio.DoubleBuffer;
import main.main;
import org.lwjgl.BufferUtils;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import terrain.TerrainHandler;
import util.Matrix4f;
import util.Vector3f;
import util.Vector4f;

public class RayCaster
{

    private Vector3f ray;
    private Matrix4f projectionMatrixInverse;

    public RayCaster(Matrix4f projectionMatrix)
    {
        projectionMatrixInverse = projectionMatrix.inverse();
        }

    /**
     * Uses the window and the camera to calculate a new ray.
     * 
     * @param window
     * @param camera 
     */
    public void update(long window, Camera camera)
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
        //ray_clip = ray_clip.normalize();

        Vector4f ray_eye = projectionMatrixInverse.multiply(ray_clip);
        Vector4f ray_world = Matrix4f.translate(camera.position.x, camera.position.y, camera.position.z).inverse().multiply(camera.getWorldtoViewMatrix().inverse().multiply(ray_eye));
        ray = new Vector3f(ray_world.x, ray_world.y, ray_world.z).normalize();
    }
    
    /**
     * Gives the point on the terrain where the ray intersects.
     * @param terrain
     * @param camera
     * @return 
     */
    protected Vector3f getTerrainPosition(TerrainHandler terrain, Camera camera)
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

        return ray.scale((float) (maxDis + minDis) / 2).add(camera.position);
    }
}
