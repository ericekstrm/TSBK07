package main;

import camera.Camera;
import java.nio.DoubleBuffer;
import light.LightHandler;
import loader.Loader;
import model.ColorModel;
import org.lwjgl.BufferUtils;
import static org.lwjgl.glfw.GLFW.*;
import shader.ColorModelShader;
import terrain.TerrainHandler;
import util.Matrix4f;
import util.Vector2f;
import util.Vector3f;
import util.Vector4f;

public class Player
{

    Vector3f position;
    Vector3f direction;

    Camera camera;

    float playerSpeed = 1;

    ColorModel model;
    ColorModelShader shader;

    public Player(Vector3f pos, long window)
    {
        this.position = pos;
        camera = new Camera(new Vector3f(20, 20, 20), pos, window);

        model = new ColorModel(Loader.loadObj("character.obj"));
        model.setPosition(position);

        shader = new ColorModelShader();
    }

    public void checkInput(long window, TerrainHandler terrain)
    {
        rotateCamera(window);

        if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS)
        {
            position = position.add(direction).scale(playerSpeed);
            camera.position = camera.position.add(direction).scale(playerSpeed);
        }
        if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS)
        {
            position = position.subtract(direction).scale(playerSpeed);
            camera.position = camera.position.subtract(direction).scale(playerSpeed);
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS)
        {
            Vector3f moveDir = direction.cross(new Vector3f(0, 1, 0));
            position = position.subtract(moveDir).scale(playerSpeed);
            camera.position = camera.position.subtract(moveDir).scale(playerSpeed);
        }
        if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS)
        {
            Vector3f moveDir = direction.cross(new Vector3f(0, 1, 0));
            position = position.add(moveDir).scale(playerSpeed);
            camera.position = camera.position.add(moveDir).scale(playerSpeed);
        }
        float terrainHeight = terrain.getHeight(position.x, position.z);
        float heightDiff = terrainHeight - position.y;
        position.y = terrainHeight;
        setDirection();

        camera.position.y += heightDiff;
        camera.setLookAt(position);
        model.setPosition(position);
    }

    boolean rotating = false;

    public void rotateCamera(long window)
    {
        Vector2f screenCenter = new Vector2f(main.WIDTH, main.HEIGHT);

        if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS)
        {
            if (!rotating)
            {
                rotating = true;
                glfwSetCursorPos(window, screenCenter.x, screenCenter.y);
            }

            DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
            DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
            glfwGetCursorPos(window, xBuffer, yBuffer);
            float x = (float) xBuffer.get(0);
            float y = (float) yBuffer.get(0);

            float diffX = (screenCenter.x - x) / 10;
            float diffY = -(screenCenter.y - y) / 10;

            camera.position = camera.position.subtract(position);

            camera.position = Matrix4f.rotate(0, (float) diffX, 0).multiply(camera.position);
            Vector3f dir = camera.position.cross(camera.upVector);
            camera.position = Matrix4f.rotate((float) diffY, dir).multiply(camera.position);

            camera.position = camera.position.add(position);

            glfwSetCursorPos(window, screenCenter.x, screenCenter.y);
        } else
        {
            rotating = false;
        }
    }

    public void update(float deltaTime)
    {

    }

    public void render(Camera camera, LightHandler lights, Vector4f clippingPlane)
    {
        shader.start();
        shader.loadLights(lights.getPointLights(), lights.getDirLights());
        shader.loadWorldToViewMatrix(camera);
        shader.loadClippingPlane(clippingPlane);

        model.render(shader);

        shader.stop();
    }

    public void setDirection()
    {
        direction = camera.direction;
        direction.y = 0;
        direction = direction.normalize();
    }

    public Camera getCamera()
    {
        return camera;
    }
}
